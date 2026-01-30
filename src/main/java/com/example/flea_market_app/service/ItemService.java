package com.example.flea_market_app.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.example.flea_market_app.entity.Item;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.repository.ItemRepository;

@Service
public class ItemService {
	private final ItemRepository itemRepository;
	private final CategoryService categoryService;
	private final CloudinaryService cloudinaryService;

	public ItemService(ItemRepository itemRepository, CategoryService categoryService,
			CloudinaryService cloudinaryService) {
		this.itemRepository = itemRepository;
		this.categoryService = categoryService;
		this.cloudinaryService = cloudinaryService;
	}

	public Page<Item> searchItems(SearchCriteria criteria) {
		String status = criteria.isIncludeSold() ? null : "出品中";
		String keyword = criteria.getKeyword();
		Long categoryId = criteria.getCategoryId();
		Integer minPrice = criteria.getMinPrice();
		Integer maxPrice = criteria.getMaxPrice();

		if ("likesDesc".equals(criteria.getSort())) {
			Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());

			if (categoryId != null) {
				List<Long> categoryIds = categoryService.getCategoryIdsWithDescendants(categoryId);
				if (categoryIds.isEmpty()) {
					return Page.empty(pageable); // 空のリストなら空のページを返す
				}
				return itemRepository.findAndSortByLikes(keyword, categoryIds, status, minPrice, maxPrice, pageable);
			} else {
				return itemRepository.findAndSortByLikesWithoutCategory(keyword, status, minPrice, maxPrice, pageable);
			}
		}

		Sort sort;
		String sortOrder = criteria.getSort() == null ? "createdAtDesc" : criteria.getSort();
		switch (sortOrder) {
			case "priceAsc":
				sort = Sort.by(Sort.Direction.ASC, "price");
				break;
			case "priceDesc":
				sort = Sort.by(Sort.Direction.DESC, "price");
				break;
			case "createdAtDesc":
			default:
				sort = Sort.by(Sort.Direction.DESC, "createdAt");
				break;
		}

		Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);

		// キーワードあり ＋ カテゴリー指定あり
		if (keyword != null && !keyword.isEmpty() && categoryId != null) {
			return itemRepository.findByNameAndHierarchyCategoryAndStatusOptional(keyword, categoryId, status, minPrice, maxPrice, pageable);
		}
		// キーワードのみ
		else if (keyword != null && !keyword.isEmpty()) {
			return itemRepository.findByNameContainingIgnoreCaseAndStatusOptional(keyword, status, minPrice, maxPrice, pageable);
		}
		// カテゴリー指定のみ（階層対応メソッドを呼び出し）
		else if (categoryId != null) {
			return itemRepository.findByHierarchyCategoryAndStatusOptional(categoryId, status, minPrice, maxPrice, pageable);
		}
		// 条件なし
		else {
			return itemRepository.findByStatusOptional(status, minPrice, maxPrice, pageable);
		}
	}

	public List<Item> getAllItems() {
		return itemRepository.findAll();
	}

	public Optional<Item> getItemById(Long id) {
		return itemRepository.findById(id);
	}

	public Item saveItem(Item item, MultipartFile imageFile) throws IOException {
		if (imageFile != null && !imageFile.isEmpty()) {
			String imageUrl = cloudinaryService.uploadFile(imageFile);
			item.setImageUrl(imageUrl);
		}
		return itemRepository.save(item);
	}

	public void deleteItem(Long id) {
		itemRepository.findById(id).ifPresent(item -> {
			if (item.getImageUrl() != null) {
				// CloudinaryService側でエラーハンドリングを完結させたので、
				// ItemService側での try-catch は不要になりました。
				cloudinaryService.deleteFile(item.getImageUrl());
			}
			itemRepository.deleteById(id);
		});
	}

	public List<Item> getItemsBySeller(User Seller) {
		return itemRepository.findBySeller(Seller);
	}

	public void markItemAsSold(Long itemId) {
		itemRepository.findById(itemId).ifPresent(item -> {
			item.setStatus("売却済");
			itemRepository.save(item);
		});
	}
}