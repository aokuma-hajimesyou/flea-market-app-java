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
import com.example.flea_market_app.service.ReviewService;
import com.example.flea_market_app.service.UserService;

@Controller
@RequestMapping("/review")
public class ReviewController {
	private final ReviewService reviewService;
	private final AppOrderService appOrderService;
	private final UserService userService;

	public ReviewController(ReviewService reviewService, AppOrderService appOrderService, UserService userService) {
		this.reviewService = reviewService;
		this.appOrderService = appOrderService;
		this.userService = userService;
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
			reviewService.submitReview(orderId, reviewer, rating, comment);
			redirectAttributes.addFlashAttribute("successMessage", "評価を送信しました");
		} catch (IllegalStateException | IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/my-page/orders";
	}
}
