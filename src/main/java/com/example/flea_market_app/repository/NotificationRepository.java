package com.example.flea_market_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flea_market_app.entity.Notification;
import com.example.flea_market_app.entity.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	// ユーザーごとの通知を、新しい日付順に取得する（一覧表示用）
	List<Notification> findByUserOrderByCreatedAtDesc(User user);

	// ユーザーの未読通知のみを取得する
	List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

	// ベルマークに表示する「未読の件数」を取得する
	int countByUserAndIsReadFalse(User user);

	// 一括既読用（必要であれば）
	void deleteByUserId(Long user);
}