package com.example.flea_market_app.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.flea_market_app.entity.Category;
import com.example.flea_market_app.entity.Item;
import com.example.flea_market_app.entity.ItemViewHistory;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.service.CategoryService;
import com.example.flea_market_app.service.ChatService;
import com.example.flea_market_app.service.FavoriteService;
import com.example.flea_market_app.service.ItemService;
import com.example.flea_market_app.service.ItemViewHistoryService;
import com.example.flea_market_app.service.NotificationService;
import com.example.flea_market_app.service.RecommendationService;
import com.example.flea_market_app.service.ReviewService;
import com.example.flea_market_app.service.UserService;

@Controller
@RequestMapping("/items")
public class ItemController {

	private final ItemService itemService;
	private final CategoryService categoryService;
	private final UserService userService;
	private final ChatService chatService;
	private final FavoriteService favoriteService;
	private final ReviewService reviewService;
	private final NotificationService notificationService;
	private final RecommendationService recommendationService;

	@Autowired
	ItemViewHistoryService itemViewHistoryService;

	public ItemController(
			ItemService itemService,
			CategoryService categoryService,
			UserService userService,
			ChatService chatService,
			FavoriteService favoriteService,
			ReviewService reviewService,
			NotificationService notificationService,
			RecommendationService recommendationService) {
		this.itemService = itemService;
		this.categoryService = categoryService;
		this.userService = userService;
		this.chatService = chatService;
		this.favoriteService = favoriteService;
		this.reviewService = reviewService;
		this.notificationService = notificationService;
		this.recommendationService = recommendationService;
	}

	@GetMapping
	public String listItem(
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model) {

		// 階層対応した検索サービスを呼び出す
		Page<Item> items = itemService.searchItems(keyword, categoryId, page, size);

		// 修正：全カテゴリーではなく、第1階層（親なし）のカテゴリーのみを取得して画面に渡す
		List<Category> categories = categoryService.getRootCategories();

		items.forEach(item -> {
			item.setFavoriteCount((int) favoriteService.getFavoriteCount(item.getId()));
			if (userDetails != null) {
				User currentUser = userService.getUserByEmail(userDetails.getUsername())
						.orElseThrow(() -> new RuntimeException("user not found"));
				boolean isFav = favoriteService.isFavorited(currentUser, item.getId());
				item.setFavorited(isFav);
			}
		});

		if (userDetails != null) {
			User user = userService.getUserByEmail(userDetails.getUsername()).orElse(null);
			if (user != null) {
				List<ItemViewHistory> itemViewHistories = itemViewHistoryService.getRecordView(user);
				List<Item> recommendedItems = recommendationService.getRecommendedItems(user);
				model.addAttribute("itemViewHistories", itemViewHistories);
				model.addAttribute("recommendedItems", recommendedItems);
				model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
				model.addAttribute("notifications", notificationService.getNotificationsForUser(user));
			}
		}

		model.addAttribute("items", items);
		model.addAttribute("categories", categories);

		return "item_list";
	}

	// 追加：子カテゴリーをAjaxで取得するためのAPI
	@GetMapping("/categories/{parentId}/children")
	@ResponseBody
	public List<Category> getChildren(@PathVariable("parentId") Long parentId) {
		return categoryService.getChildCategories(parentId);
	}

	@GetMapping("/{id}")
	public String showItemDetail(
			@PathVariable("id") Long id,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model) {

		Optional<Item> itemOpt = itemService.getItemById(id);
		if (itemOpt.isEmpty()) {
			return "redirect:/items";
		}
		Item item = itemOpt.get();

		model.addAttribute("item", item);
		model.addAttribute("chats", chatService.getChatMessageByItem(id));

		reviewService.getAverageRatingForSeller(item.getSeller())
				.ifPresent(avg -> model.addAttribute("sellerAverageRating", String.format("%.1f", avg)));

		if (userDetails != null) {
			User currentUser = userService.getUserByEmail(userDetails.getUsername())
					.orElseThrow(() -> new RuntimeException("user not found"));
			model.addAttribute("isFavorited", favoriteService.isFavorited(currentUser, id));
			itemViewHistoryService.recordView(currentUser, item);
			List<Item> recommendedItems = recommendationService.getRecommendedItems(currentUser);
			model.addAttribute("recommendedItems", recommendedItems);
		}

		model.addAttribute("keyword", keyword);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("page", page);
		model.addAttribute("size", size);

		return "item_detail";
	}

	@GetMapping("/new")
	public String showAddItemForm(Model model) {
		model.addAttribute("item", new Item());
		// 修正：第1階層のカテゴリーのみを初期値として渡す
		model.addAttribute("categories", categoryService.getRootCategories());
		return "item_form";
	}

	@PostMapping
	public String addItem(
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("name") String name,
			@RequestParam("description") String description,
			@RequestParam("price") BigDecimal price,
			@RequestParam("categoryId") Long categoryId,
			@RequestParam(value = "image", required = false) MultipartFile imageFile,
			RedirectAttributes redirectAttributes) {
		User seller = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("seller not found"));
		Category category = categoryService.getCategoryById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("category not found"));

		Item item = new Item();
		item.setSeller(seller);
		item.setName(name);
		item.setDescription(description);
		item.setPrice(price);
		item.setCategory(category);
		try {
			itemService.saveItem(item, imageFile);
			redirectAttributes.addFlashAttribute("successMessage", "商品を出品しました");
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "画像のアップロードに失敗しました" + e.getMessage());
			return "redirect:/items/new";
		}
		return "redirect:/items";
	}

	@GetMapping("/{id}/edit")
	public String showEditItemForm(@PathVariable("id") Long id, Model model) {
		Optional<Item> item = itemService.getItemById(id);
		if (item.isEmpty()) {
			return "redirect:/items";
		}

		model.addAttribute("item", item.get());
		// 修正：第1階層のカテゴリーのみを渡す
		model.addAttribute("categories", categoryService.getRootCategories());
		return "item_form";
	}

	@PostMapping("/{id}")
	public String updateItem(
			@PathVariable("id") long id,
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("name") String name,
			@RequestParam("description") String description,
			@RequestParam("price") BigDecimal price,
			@RequestParam("categoryId") Long categoryId,
			@RequestParam(value = "image", required = false) MultipartFile imageFile,
			RedirectAttributes redirectAttributes) {
		Item existingItem = itemService.getItemById(id)
				.orElseThrow(() -> new RuntimeException("item not found"));
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("user not found"));
		if (!existingItem.getSeller().getId().equals(currentUser.getId())) {
			redirectAttributes.addFlashAttribute("errorMessage", "この商品は編集できません");
			return "redirect:/items";
		}

		Category category = categoryService.getCategoryById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("Category not found"));
		existingItem.setName(name);
		existingItem.setDescription(description);
		existingItem.setPrice(price);
		existingItem.setCategory(category);

		try {
			itemService.saveItem(existingItem, imageFile);
			redirectAttributes.addFlashAttribute("successMessage", "商品を更新しました");
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "画像のアップロードのに失敗しました" + e.getMessage());
			return "redirect:/items/{id}/edit";
		}

		return "redirect:/items/{id}";
	}

	@PostMapping("/{id}/delete")
	public String deleteItem(
			@PathVariable("id") Long id,
			@AuthenticationPrincipal UserDetails userDetails,
			RedirectAttributes redirectAttributes) {
		Item itemToDelete = itemService.getItemById(id)
				.orElseThrow(() -> new RuntimeException("Item not found"));
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		if (!itemToDelete.getSeller().getId().equals(currentUser.getId())) {
			redirectAttributes.addFlashAttribute("errorMessage", "この商品は削除できません");
			return "redirect:/items";
		}
		itemService.deleteItem(id);
		redirectAttributes.addFlashAttribute("successMessage", "商品を削除しました");
		return "redirect:/items";
	}

	@PostMapping("/{id}/favorite")
	public String addFavorite(
			@PathVariable("id") Long itemId,
			@AuthenticationPrincipal UserDetails userDetails,
			RedirectAttributes redirectAttributes) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("user not found"));
		try {
			favoriteService.addFavorite(currentUser, itemId);
			redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加しました");
		} catch (IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/items/{id}";
	}

	@PostMapping("/{id}/unfavorite")
	public String removeFavorite(
			@PathVariable("id") Long itemId,
			@AuthenticationPrincipal UserDetails userDetails,
			RedirectAttributes redirectAttributes) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("user not found"));
		try {
			favoriteService.removeFavorite(currentUser, itemId);
			redirectAttributes.addFlashAttribute("successMessage", "お気に入りから削除しました");
		} catch (IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/items/{id}";
	}
}