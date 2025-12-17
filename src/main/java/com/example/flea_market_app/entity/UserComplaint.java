// src/main/java/com/example/fleamarketsystem/entity/UserComplaint.java
package com.example.flea_market_app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // JPA エンティティとして管理
@Table(name = "user_complaint") // 通報履歴テーブル
@Data // Getter/Setter, toString 自動生成
@NoArgsConstructor
@AllArgsConstructor
public class UserComplaint {
	@Id // 主キー
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment
	private Long id; // 通報 ID（PK）
	@Column(name = "reported_user_id", nullable = false)
	private Long reportedUserId; // 通報されたユーザーの ID（対象者）
	@Column(name = "reporter_user_id", nullable = false)
	private Long reporterUserId; // 通報を行ったユーザーID（提出者）
	@Column(nullable = false)
	private String reason; // 通報理由（自由記述）
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now(); // 通報日時
}