package com.example.flea_market_app.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

@Service
public class StripeService {
	public StripeService(@Value("${stripe.api.secretKey}") String secretKey) {
		Stripe.apiKey = secretKey;
	}

	public PaymentIntent createPaymentIntent(BigDecimal amount, String currency, String description)
			throws StripeException {

	}

}
