package com.example.flea_market_app.entity;

import java.time.LocalDateTime; // LocalDateTime のインポートを追加

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String role;

	@Column(name = "line_notify_token")
	private String lineNotifyToken;

	@Column(nullable = false)
	private boolean enabled = true;

	@Column(nullable = false)
	private boolean banned = false;

	@Column(name = "ban_reason")
	private String banReason; // BAN理由

	@Column(name = "banned_at")
	private LocalDateTime bannedAt; // BAN日時

	@Column(name = "banned_by_admin_id")
	private Integer bannedByAdminId; // BANを実施した管理者ID (intValue()に合わせてInteger型を推奨)

}