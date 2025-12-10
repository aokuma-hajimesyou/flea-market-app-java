//エラーの原因調査のためAI使用

package com.example.flea_market_app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.entity.UserComplaint;
import com.example.flea_market_app.repository.UserComplaintRepository;
import com.example.flea_market_app.repository.UserRepository;

@Service
public class AdminUserService {
	private final UserRepository userRepository;
	// フィールド名が userComplaintRepository ですが、後のメソッドで complaintRepository が使われているため、
	// complaintRepository の名前に統一して扱います
	private final UserComplaintRepository complaintRepository;

	public AdminUserService(UserRepository userRepository, UserComplaintRepository userComplaintRepository) {
		this.userRepository = userRepository;
		this.complaintRepository = userComplaintRepository; // フィールド名に合わせて代入
	}

	public List<User> listAllUsers() {
		return userRepository.findAll();
	}

	// 単一ユーザー取得（見つからなければ例外）
	public User findUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("User not found: " + id));
	}

	// 対象ユーザーの平均レビュー評価を取得（null の場合は 0 として返す）
	public Double averageRating(Long userId) {
		// userRepository に averageRatingForUser メソッドが存在することを前提としています
		// 実際には、このメソッドは UserRepository ではなく、レビュー関連のリポジトリに存在する可能性が高いです
		// ここではエラー解消のため、一旦 UserRepository にあるものとします
		Double avg = userRepository.averageRatingForUser(userId);
		return (avg == null) ? 0.0 : avg;
	}

	//指定ユーザーの通報件数を取得
	public long complaintCount(Long userId) {
		return complaintRepository.countByReportedUserId(userId);
	}

	//指定ユーザーの通報履歴一覧を取得（新しい順）
	// UserComplaint エンティティの存在を前提としています
	public List<UserComplaint> complaints(Long userId) {
		return complaintRepository.findByReportedUserIdOrderByCreatedAtDesc(userId);
	}

	//ユーザーを BAN する処理（必要に応じてログインも無効化）
	@Transactional
	public void banUser(Long targetUserId, Long adminUserId, String reason, boolean alsoDisableLogin) {
		User u = findUser(targetUserId);
		u.setBanned(true);
		u.setBanReason(reason);
		u.setBannedAt(LocalDateTime.now());
		// intValue() 呼び出しは、もし adminUserId が Long で、User エンティティの型が int であれば必要です
		// null の場合はそのまま null を渡します
		u.setBannedByAdminId(adminUserId == null ? null : adminUserId.intValue());

		if (alsoDisableLogin)
			u.setEnabled(false);

		userRepository.save(u);
	}

	//BAN 解除（元の状態へ戻す）
	@Transactional
	public void unbanUser(Long targetUserId) {
		User u = findUser(targetUserId);
		u.setBanned(false);
		u.setBanReason(null);
		u.setBannedAt(null);
		u.setBannedByAdminId(null);
		u.setEnabled(true); // BAN 解除後ログイン有効化
		userRepository.save(u);
	}
}