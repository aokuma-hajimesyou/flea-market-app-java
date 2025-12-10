package com.example.flea_market_app.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.flea_market_app.entity.Category;
import com.example.flea_market_app.entity.Item;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.service.CategoryService;
import com.example.flea_market_app.service.ChatService;
import com.example.flea_market_app.service.FavoriteService;
import com.example.flea_market_app.service.ItemService;
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

	public ItemController(
			ItemService itemService,
			CategoryService categoryService,
			UserService userService,
			ChatService chatService,
			FavoriteService favoriteService,
			ReviewService reviewService) {
		this.itemService = itemService;
		this.categoryService = categoryService;
		this.userService = userService;
		this.chatService = chatService;
		this.favoriteService = favoriteService;
		this.reviewService = reviewService;
	}

	@GetMapping
	public String listItem(
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			Model model) {
		Page<Item> items = itemService.searchItems(keyword, categoryId, page, size);
		List<Category> categories = categoryService.getAllCategories();

		model.addAttribute("items", items);
		model.addAttribute("categories", categories);
		return "item_list";
	}

	@GetMapping("/{id}")
	public String showItemDetail(
			@PathVariable("id") Long id,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model) {
		Optional<Item> item = itemService.getItemById(id);
		if (item.isEmpty()) {
			return "redirect:/items";
		}
		model.addAttribute("item", item.get());
		model.addAttribute("chats", chatService.getChatMessageByItem(id));
		reviewService.getAverageRatingForSeller(item.get().getSeller())
				.ifPresent(avg -> model.addAttribute("sellerAverageRating",
						String.format("%.1f", avg)));
		if (userDetails != null) {
			User currenUser = userService.getUserByEmail(userDetails.getUsername())
					.orElseThrow(() -> new RuntimeException("user not fonund"));
			model.addAttribute("isFavorited", favoriteService.isFavorited(currenUser, id));
		}
		return "item_detail";
	}

	@GetMapping("/new")
	public String showAddItemForm(Model model) {
		model.addAttribute("item", new Item());
		model.addAttribute("categories", categoryService.getAllCategories());
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
		model.addAttribute("categories", categoryService.getAllCategories());
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
	}

}