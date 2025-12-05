package com.example.flea_market_app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.service.AppOrderService;
import com.example.flea_market_app.service.ItemService;
import com.example.flea_market_app.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

@Controller
@RequestMapping("/orders")
public class AppOrderController {
	private final AppOrderService appOrderService;
	private final UserService userService;
	private final ItemService itemService;

	@Value("${stripe.public.key}")
	private String stripePublicKey;

	public AppOrderController(AppOrderService appOrderService,
			com.example.flea_market_app.controller.UserService userService,
			com.example.flea_market_app.controller.ItemService itemService) {
		super();
		this.appOrderService = appOrderService;
		this.userService = userService;
		this.itemService = itemService;
	}

	@PostMapping("/initiate-purchase")
	public String initiatePurchase(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("itemId") Long itemId, RedirectAttributes redirectAttributes) {
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

}
