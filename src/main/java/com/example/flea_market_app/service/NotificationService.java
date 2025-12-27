package com.example.flea_market_app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.flea_market_app.entity.Notification;
import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.repository.NotificationRepository;

@Service
@Transactional
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public NotificationService(NotificationRepository notificationRepository) {
		this.notificationRepository = notificationRepository;
	}

	/**
	 * 新しい通知を作成・保存する
	 */
	public void createNotification(User user, String title, String message, String linkUrl) {
		Notification notification = new Notification();
		notification.setUser(user);
		notification.setTitle(title);
		notification.setMessage(message);
		notification.setLinkUrl(linkUrl);
		notificationRepository.save(notification);
	}

	/**
	 * ユーザーの通知一覧を取得する（新しい順）
	 */
	@Transactional(readOnly = true)
	public List<Notification> getNotificationsForUser(User user) {
		return notificationRepository.findByUserOrderByCreatedAtDesc(user);
	}

	/**
	 * 未読の通知件数を取得する（ベルマークのバッジ用）
	 */
	@Transactional(readOnly = true)
	public int getUnreadCount(User user) {
		return notificationRepository.countByUserAndIsReadFalse(user);
	}

	/**
	 * 特定の通知を既読にする
	 * 戻り値として通知オブジェクトを返し、遷移先URLを取得しやすくする
	 */
	public Notification markAsRead(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new IllegalArgumentException("通知が見つかりません ID: " + notificationId));

		notification.setRead(true);
		return notificationRepository.save(notification);
	}

	/**
	 * 通知を削除し、そのURLを返す
	 */
	@Transactional
	public String deleteAndGetUrl(Long id) {
		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Notification not found"));

		String url = notification.getLinkUrl();

		// 既読更新ではなく、物理削除を実行
		notificationRepository.delete(notification);

		return url;
	}

	/**
	 * 指定したユーザーの通知をすべて削除する
	 */
	@Transactional
	public void deleteAllByUserId(Long userId) {
		notificationRepository.deleteByUserId(userId);
	}
}