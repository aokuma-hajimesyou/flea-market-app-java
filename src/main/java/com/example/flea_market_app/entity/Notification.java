package com.example.flea_market_app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "notification")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 通知を受け取るユーザー
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// 通知のタイトル（例：商品が購入されました）
	@Column(nullable = false)
	private String title;

	// 通知の本文
	@Column(nullable = false, columnDefinition = "TEXT")
	private String message;

	// クリック時の遷移先URL（例：/items/10）
	@Column(name = "link_url")
	private String linkUrl;

	// 既読フラグ（初期値は false）
	@Column(name = "is_read")
	private boolean isRead = false;

	// 通知作成日時（自動設定）
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}