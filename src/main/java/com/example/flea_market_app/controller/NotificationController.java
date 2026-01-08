package com.example.flea_market_app.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.flea_market_app.entity.Notification;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.service.NotificationService;
import com.example.flea_market_app.service.UserService;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

	private final NotificationService notificationService;
	private final UserService userService;

	public NotificationController(NotificationService notificationService, UserService userService) {
		this.notificationService = notificationService;
		this.userService = userService;
	}

	/**
	 * 通知一覧画面を表示
	 */
	@GetMapping
	public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User user = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		List<Notification> notifications = notificationService.getNotificationsForUser(user);
		model.addAttribute("notifications", notifications);

		return "notifications/index";
	}

	/**
	 * 通知を既読にしてから、リンク先へ遷移する
	 */
	/**
	 * 通知を削除してから、リンク先へ遷移する（履歴を残さない）
	 */
	@GetMapping("/{id}/read")
	public String deleteAndRedirect(@PathVariable("id") Long id) {
		// Service側で通知を削除し、削除前に保持していた遷移先URLを取得する
		String targetUrl = notificationService.deleteAndGetUrl(id);

		// 保存されていた遷移先URLへリダイレクト
		return "redirect:" + (targetUrl != null ? targetUrl : "/items");
	}

	/**
	 * すべての通知を削除する
	 */
	@GetMapping("/mark-all-read")
	public String deleteAll(@AuthenticationPrincipal UserDetails userDetails) {
		User user = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		notificationService.deleteAllByUserId(user.getId());

		// 削除後は元のページ（今回は商品一覧）へ戻す
		return "redirect:/items";
	}
}