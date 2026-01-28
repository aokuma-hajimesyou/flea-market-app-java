package com.example.flea_market_app.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 出品者（users テーブルへの外部キー）。NULL 禁止
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User seller;

	// 商品名。NULL 禁止
	@Column(nullable = false)
	private String name;

	// 商品説明。長文想定で TEXT
	@Column(columnDefinition = "TEXT")
	private String description;

	// 価格。NULL 禁止（小数を扱うため BigDecimal）
	@Column(nullable = false)
	private BigDecimal price;

	// カテゴリ（外部キー）。NULL 可（未分類を許容）
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	// 出品ステータス。初期値は「出品中」
	private String status = "出品中";

	// 画像 URL（Cloudinary にアップロードした結果を格納）
	private String imageUrl;

	// 作成日時。列名を created_at に固定、初期値は現在時刻
	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt = OffsetDateTime.now();

	@OneToMany(mappedBy = "item")
	private List<FavoriteItem> favoritedBy;

	@Transient
	private Integer favoriteCount = 0;

	@Transient
	private boolean isFavorited;

	@Transient
	private String recommendReason;
}
