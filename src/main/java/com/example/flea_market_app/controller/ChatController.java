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

import com.example.flea_market_app.entity.Item;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.service.ChatService;
import com.example.flea_market_app.service.ItemService;
import com.example.flea_market_app.service.NotificationService;
import com.example.flea_market_app.service.UserService;

@Controller
@RequestMapping("/chat")
public class ChatController {

	private final ChatService chatService;
	private final ItemService itemService;
	private final UserService userService;
	private final NotificationService notificationService;

	public ChatController(ChatService chatService, ItemService itemService, UserService userService,
			NotificationService notificationService) {
		this.chatService = chatService;
		this.itemService = itemService;
		this.userService = userService;
		this.notificationService = notificationService;
	}

	@GetMapping("/{itemId}")
	public String showChatScreen(@PathVariable("itemId") Long itemId, Model model) {
		model.addAttribute("item", itemService.getItemById(itemId)
				.orElseThrow(() -> new RuntimeException("Item not found")));
		model.addAttribute("chats", chatService.getChatMessageByItem(itemId));
		return "item_detail";
	}

	@PostMapping("/{itemId}")
	public String sendMessage(
			@PathVariable("itemId") Long itemId,
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("message") String message) {

		User sender = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("Sender not found"));

		Item item = itemService.getItemById(itemId)
				.orElseThrow(() -> new RuntimeException("Item not found"));

		// 1. メッセージを保存
		chatService.sendMessage(itemId, sender, message);

		// 2. 通知の宛先（相手）を判定
		// 送信者が「出品者」なら、通知先は「商品の購入を検討している人（チャット相手）」になりますが、
		// フリマアプリの仕様として、まずは「出品者以外が送ったら出品者に通知」という形が一般的です。
		// ※より厳密には、直前のメッセージ送信者に送るなどのロジックがありますが、シンプルに実装します。

		User recipient;
		if (sender.getId().equals(item.getSeller().getId())) {
			// 送信者が出品者の場合：チャット履歴から出品者以外の直近の送信者を探す等の処理が必要ですが、
			// 簡易的に「出品者以外」への通知ロジックをここに挟みます
			// 今回は一旦、出品者宛の通知を例にします。
			return "redirect:/chat/{itemId}";
		} else {
			// 送信者が出品者以外の場合：宛先は「出品者」
			recipient = item.getSeller();
		}

		notificationService.createNotification(
				recipient,
				"新着メッセージ",
				"「" + item.getName() + "」に新しいチャットが届きました。",
				"/items/" + itemId // チャットが見れる詳細画面へ
		);

		return "redirect:/chat/{itemId}";
	}
}