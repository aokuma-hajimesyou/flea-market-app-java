package com.example.flea_market_app.controller;

import java.util.List;
import java.util.OptionalDouble;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.flea_market_app.entity.Item;
import com.example.flea_market_app.entity.Review;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.service.ItemService;
import com.example.flea_market_app.service.ReviewService;
import com.example.flea_market_app.service.UserService;

@Controller
public class SellerController {

	private final UserService userService;
	private final ReviewService reviewService;
	private final ItemService itemService;

	public SellerController(UserService userService, ReviewService reviewService, ItemService itemService) {
		this.userService = userService;
		this.reviewService = reviewService;
		this.itemService = itemService;
	}

	@GetMapping("/users/{id}")
	public String sellerDetail(@PathVariable("id") Long id, Model model) {
		// Userを取得。取得できない場合はRuntimeExceptionを投げる
		User seller = userService.getUserById(id)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// 出品者情報をモデルに追加
		model.addAttribute("seller", seller);

		// レビュー一覧と平均評価を取得
		List<Review> reviews = reviewService.getReviewsBySeller(seller);
		OptionalDouble averageRatingOpt = reviewService.getAverageRatingForSeller(seller);
		double averageRating = averageRatingOpt.orElse(0.0); // isPresentのチェックを簡略化

		// 出品中の商品一覧を取得
		List<Item> items = itemService.getItemsBySeller(seller);

		// 各データをモデルに追加
		model.addAttribute("reviews", reviews);
		model.addAttribute("averageRating", averageRating);
		model.addAttribute("items", items);

		return "seller_detail";
	}
}