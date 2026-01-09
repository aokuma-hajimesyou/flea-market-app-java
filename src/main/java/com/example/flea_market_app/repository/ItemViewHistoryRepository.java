package com.example.flea_market_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.flea_market_app.entity.Item;
import com.example.flea_market_app.entity.ItemViewHistory;
import com.example.flea_market_app.entity.User;

@Repository
public interface ItemViewHistoryRepository extends JpaRepository<ItemViewHistory, Integer> {
	// 特定のユーザーが特定の商品を閲覧した履歴があるか探す
	Optional<ItemViewHistory> findByUserAndItem(User user, Item item);

	// マイページなどで「最近チェックした商品」を表示するために、閲覧順に上位を取得
	List<ItemViewHistory> findTop10ByUserOrderByViewedAtDesc(User user);

	@Query("""
			    SELECT h.item.category.id
			    FROM ItemViewHistory h
			    WHERE h.user = :user
			      AND h.item.category IS NOT NULL
			    GROUP BY h.item.category.id
			    ORDER BY MAX(h.viewedAt) DESC
			""")
	List<Long> findViewedCategoryIdsByUser(User user);
}