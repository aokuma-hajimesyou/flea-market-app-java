package com.example.flea_market_app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 一つの注文に対してレビューは1件
	@OneToOne
	@JoinColumn(name = "order_id", nullable = false, unique = true)
	private AppOrder order;

	// レビューワ（購入者）
	@ManyToOne
	@JoinColumn(name = "reviewer_id", nullable = false)
	private User reviewer;

	// 出品者（被評価者）
	@ManyToOne
	@JoinColumn(name = "seller_id", nullable = false)
	private User seller;

	// 対象商品
	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	// 評価点（1〜5）
	@Column(nullable = false)
	private Integer rating;

	// コメント本文（任意）
	@Column(columnDefinition = "TEXT")
	private String comment;

	// 作成日時
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
}
