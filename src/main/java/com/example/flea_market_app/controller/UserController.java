package com.example.flea_market_app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.service.AppOrderService;
import com.example.flea_market_app.service.FavoriteService;
import com.example.flea_market_app.service.ItemService;
import com.example.flea_market_app.service.NotificationService;
import com.example.flea_market_app.service.ReviewService;
import com.example.flea_market_app.service.UserService;

@Controller
@RequestMapping("/my-page")
public class UserController {
	private final UserService userService;
	private final AppOrderService appOrderService;
	private final ItemService itemService;
	private final FavoriteService favoriteService;
	private final ReviewService reviewService;
	private final NotificationService notificationService;

	public UserController(
			UserService userService,
			AppOrderService appOrderService,
			ItemService itemService,
			FavoriteService favoriteService,
			ReviewService reviewService,
			NotificationService notificationService) {
		this.userService = userService;
		this.appOrderService = appOrderService;
		this.itemService = itemService;
		this.favoriteService = favoriteService;
		this.reviewService = reviewService;
		this.notificationService = notificationService;
	}

	@GetMapping
	public String myPage(
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(value = "includeSold", required = false) Boolean includeSold,
			@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		// --- 通知情報の追加 ---
		model.addAttribute("user", currentUser);
		model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
		model.addAttribute("notifications", notificationService.getNotificationsForUser(currentUser));

		// 絞り込み条件をmodelに追加
		model.addAttribute("keyword", keyword);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("page", page);
		model.addAttribute("size", size);
		model.addAttribute("includeSold", includeSold);

		return "my-page";
	}

	@GetMapping("/selling")
	public String mySellingItems(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		model.addAttribute("sellingItems", itemService.getItemsBySeller(currentUser));
		return "seller_items";
	}

	@GetMapping("/orders")
	public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		model.addAttribute("myOrders", appOrderService.getOrdersByBuyer(currentUser));
		return "buyer_app_orders";
	}

	@GetMapping("/sales")
	public String mySales(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		model.addAttribute("mySales", appOrderService.getOrdersBySeller(currentUser));
		return "seller_app_orders";
	}

	@GetMapping("/favorites")
	public String myFavorites(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		model.addAttribute("myFavorites", favoriteService.getFavoriteItemsByUser(currentUser));
		return "my_favorites";
	}

	@GetMapping("/reviews")
	public String myReviews(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		model.addAttribute("myReviews", reviewService.getReviewsByReviewer(currentUser));
		model.addAttribute("reviewsForMe", reviewService.getReviewsBySeller(currentUser));
		return "user_reviews";
	}
}
