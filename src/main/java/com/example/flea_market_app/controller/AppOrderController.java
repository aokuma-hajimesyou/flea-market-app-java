package com.example.flea_market_app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.flea_market_app.entity.AppOrder;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.service.AppOrderService;
import com.example.flea_market_app.service.ItemService;
import com.example.flea_market_app.service.NotificationService;
import com.example.flea_market_app.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

@Controller
@RequestMapping("/orders")
public class AppOrderController {

	private final AppOrderService appOrderService;
	private final UserService userService;
	private final ItemService itemService;
	private final NotificationService notificationService;

	@Value("${stripe.public.key}")
	private String stripePublicKey;

	public AppOrderController(AppOrderService appOrderService, UserService userService,
			ItemService itemService, NotificationService notificationService) {
		this.appOrderService = appOrderService;
		this.userService = userService;
		this.itemService = itemService;
		this.notificationService = notificationService;
	}

	@PostMapping("/initiate-purchase")
	public String initiatePurchase(
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("itemId") Long itemId,
			RedirectAttributes redirectAttributes) {

		User buyer = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("Buyer not found"));

		try {
			PaymentIntent paymentIntent = appOrderService.initiatePurchase(itemId, buyer);
			redirectAttributes.addFlashAttribute("clientSecret", paymentIntent.getClientSecret());
			redirectAttributes.addFlashAttribute("itemId", itemId);
			return "redirect:/orders/confirm-payment";
		} catch (IllegalStateException | IllegalArgumentException | StripeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/items/" + itemId;
		}
	}

	@GetMapping("/confirm-payment")
	public String confirmPayment(
			@ModelAttribute("clientSecret") String clientSecret,
			@ModelAttribute("itemId") Long itemId,
			Model model) {

		if (clientSecret == null || itemId == null) {
			return "redirect:/items";
		}

		model.addAttribute("clientSecret", clientSecret);
		model.addAttribute("itemId", itemId);
		model.addAttribute("stripePublicKey", stripePublicKey);
		return "payment_confirmation";
	}

	@GetMapping("/complete-purchase")
	public String completePurchase(
			@RequestParam("paymentIntentId") String paymentIntentId,
			RedirectAttributes redirectAttributes) {

		try {
			// 1. 購入確定処理を実行（戻り値として注文情報を取得するように変更されている前提）
			// もし戻り値が void の場合は、appOrderService 内で注文情報を取得するメソッドを別途呼ぶ必要があります
			AppOrder order = appOrderService.completePurchase(paymentIntentId);

			// 1. 出品者へ：売れたことを知らせる
			notificationService.createNotification(
					order.getItem().getSeller(),
					"商品購入通知",
					"「" + order.getItem().getName() + "」が購入されました。発送準備をお願いします。",
					"/my-page/sales");

			// 2. 購入者へ：購入成功を知らせる
			notificationService.createNotification(
					order.getBuyer(),
					"購入完了通知",
					"「" + order.getItem().getName() + "」の購入が完了しました！発送をお待ちください。",
					"/my-page/orders");

			redirectAttributes.addFlashAttribute("successMessage", "商品を購入しました！");
			return "item_buy_success";

		} catch (StripeException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"決済処理中にエラーが発生しました: " + e.getMessage());
			return "redirect:/items";
		}
	}

	@PostMapping("/stripe-webhook")
	public void handleStripeWebhook(
			@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String sigHeader) {

		System.out.println("Received Stripe Webhook: " + payload);
	}

	@PostMapping("/{id}/ship")
	public String shipOrder(
			@PathVariable("id") Long orderId,
			RedirectAttributes redirectAttributes) {

		try {
			// ★ 1. まず DB から注文情報を取得する（これで order が使えるようになります）
			AppOrder order = appOrderService.getOrderById(orderId)
					.orElseThrow(() -> new IllegalArgumentException("注文が見つかりません"));

			// 2. 発送処理を実行
			appOrderService.markOrderAsShipped(orderId);

			// 3. 購入者へ通知を送る
			User buyer = order.getBuyer();
			String title = "商品発送通知";
			String message = "「" + order.getItem().getName() + "」が発送されました！到着までしばらくお待ちください。";
			String linkUrl = "/my-page/orders";

			notificationService.createNotification(buyer, title, message, linkUrl);

			redirectAttributes.addFlashAttribute("successMessage", "商品を発送済みにしました。");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}

		return "redirect:/my-page/sales";
	}

	@PostMapping("/{id}/arrive")
	public String arriveOrder(
			@PathVariable("id") Long orderId,
			RedirectAttributes redirectAttributes) {

		try {
			// ★ 1. まず DB から注文情報を取得する
			AppOrder order = appOrderService.getOrderById(orderId)
					.orElseThrow(() -> new IllegalArgumentException("注文が見つかりません"));

			// 2. 到着処理を実行 (メソッド名はサービスに合わせてください。一般的には markOrderAsArrived など)
			// ※ 提示されたコードでは markOrderAsShipped になっていましたが、到着なので更新が必要です。
			appOrderService.markOrderAsArrived(orderId);

			// 3. 出品者へ通知を送る（メモの要件：到着済にされた時 -> 出品者宛）
			User seller = order.getItem().getSeller();
			notificationService.createNotification(
					seller,
					"商品到着通知",
					"「" + order.getItem().getName() + "」が購入者に到着しました。評価を行って取引を完了させてください。",
					"/my-page/sales");

			redirectAttributes.addFlashAttribute("successMessage", "商品を到着済みにしました。");

			// 取引完了・評価へリダイレクト
			return "redirect:/reviews/new/" + orderId;

		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/my-page/orders";
		}
	}
}