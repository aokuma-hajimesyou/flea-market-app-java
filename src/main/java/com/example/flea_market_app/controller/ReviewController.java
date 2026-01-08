package com.example.flea_market_app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.flea_market_app.entity.AppOrder;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.service.AppOrderService;
import com.example.flea_market_app.service.NotificationService;
import com.example.flea_market_app.service.ReviewService;
import com.example.flea_market_app.service.UserService;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
	private final ReviewService reviewService;
	private final AppOrderService appOrderService;
	private final UserService userService;
	private final NotificationService notificationService;

	public ReviewController(ReviewService reviewService, AppOrderService appOrderService, UserService userService,
			NotificationService notificationService) {
		this.reviewService = reviewService;
		this.appOrderService = appOrderService;
		this.userService = userService;
		this.notificationService = notificationService;
	}

	@GetMapping("/new/{orderId}")
	public String showReviewForm(@PathVariable("orderId") Long orderId, Model model) {
		AppOrder order = appOrderService.getOrderById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("order not found"));
		model.addAttribute("order", order);
		return "review_form";
	}

	@PostMapping
	public String submitReview(
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("orderId") Long orderId,
			@RequestParam("rating") int rating,
			@RequestParam("comment") String comment,
			RedirectAttributes redirectAttributes) {

		User reviewer = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("user not found"));

		try {
			// ★ ここで order を取得する（これがないと下の if 文で order が使えません）
			AppOrder order = appOrderService.getOrderById(orderId)
					.orElseThrow(() -> new IllegalArgumentException("order not found"));

			// 1. レビューを保存
			reviewService.submitReview(orderId, reviewer, rating, comment);

			// 2. 通知の宛先（評価された人）を判定
			User reviewee;
			// 購入者がレビューを書いた場合
			if (reviewer.getId().equals(order.getBuyer().getId())) {
				reviewee = order.getItem().getSeller();
			} else {
				// 出品者がレビューを書いた場合
				reviewee = order.getBuyer();
			}

			notificationService.createNotification(
					reviewee,
					"評価（レビュー）受領通知",
					"「" + order.getItem().getName() + "」の取引について評価が届きました。",
					"/my-page/reviews");

			redirectAttributes.addFlashAttribute("successMessage", "評価を送信しました");
		} catch (IllegalStateException | IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/my-page/orders";
	}
}
