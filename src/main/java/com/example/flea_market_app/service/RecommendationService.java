package com.example.flea_market_app.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.flea_market_app.entity.Item;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.repository.FavoriteItemRepository;
import com.example.flea_market_app.repository.ItemRepository;
import com.example.flea_market_app.repository.ItemViewHistoryRepository;

@Service
public class RecommendationService {
	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private ItemViewHistoryRepository itemViewHistoryRepository;
	@Autowired
	private FavoriteItemRepository favoriteItemRepository;

	public List<Item> getRecommendedItems(User user) {
		// 1. お気に入りから「孫カテゴリ」一致で取得（優先度：最高）
		List<Long> favCatIds = favoriteItemRepository.findCategoryIdsByUser(user);
		List<Item> favBased = new ArrayList<>();
		if (!favCatIds.isEmpty()) {
			favBased = itemRepository.findByCategoryIdInAndStatusAndUserNot(favCatIds, user.getId(), 4);
			favBased.forEach(i -> i.setRecommendReason("お気に入りに登録した商品に関連"));
		}

		// 2. 閲覧履歴から「親カテゴリ」へ遡って取得（優先度：中）
		List<Long> viewCatIds = itemViewHistoryRepository.findViewedCategoryIdsByUser(user);
		List<Item> viewBased = new ArrayList<>();
		if (!viewCatIds.isEmpty()) {
			viewBased = itemRepository.findRecommendedItems(viewCatIds, user.getId());
			viewBased.forEach(i -> {
				if (i.getRecommendReason() == null) {
					i.setRecommendReason("最近閲覧した商品に関連");
				}
			});
		}

		// 3. 重複を排除して結合（お気に入り由来を優先）
		Set<Item> combined = new LinkedHashSet<>();
		combined.addAll(favBased);
		combined.addAll(viewBased);

		return combined.stream().limit(8).collect(Collectors.toList());
	}
}