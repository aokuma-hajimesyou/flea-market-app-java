package com.example.flea_market_app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.flea_market_app.entity.Item;
import com.example.flea_market_app.entity.User;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	Page<Item> findByNameContainingIgnoreCaseAndStatus(String name, String staus, Pageable pageable);

	Page<Item> findByCategoryIdAndStatus(Long categoryId, String status, Pageable pageable);

	Page<Item> findByNameContainingIgnoreCaseAndCategoryIdAndStatus(String name, Long categoryId, String status,
			Pageable pageable);

	@Query("SELECT i FROM Item i JOIN FETCH i.seller WHERE i.status = :status")
	Page<Item> findByStatus(String status, Pageable pageable);

	List<Item> findBySeller(User seller);

	@Query(value = """
			SELECT * FROM item i
			WHERE i.category_id IN (:categoryIds)
			  AND i.status = '出品中'
			  AND i.user_id <> :userId
			ORDER BY RANDOM()
			LIMIT :limit
			""", nativeQuery = true)
	List<Item> findByCategoryIdInAndStatusAndUserNot(
			@Param("categoryIds") List<Long> categoryIds,
			@Param("userId") Long userId,
			@Param("limit") int limit);

	@Query(value = """
			WITH RECURSIVE category_path AS (
			    -- 1. 閲覧履歴にあるカテゴリーから親を遡ってルートを特定する
			    SELECT id, parent_id, id as origin_id
			    FROM category
			    WHERE id IN (:categoryIds)
			    UNION ALL
			    SELECT c.id, c.parent_id, cp.origin_id
			    FROM category c
			    JOIN category_path cp ON c.id = cp.parent_id
			),
			root_categories AS (
			    -- 2. 各閲覧履歴に対する最上位の親IDのみを抽出
			    SELECT DISTINCT id FROM category_path WHERE parent_id IS NULL
			),
			all_related_categories AS (
			    -- 3. その最上位親に属するすべての配下カテゴリー(子・孫)を取得
			    SELECT id FROM category WHERE id IN (SELECT id FROM root_categories)
			    UNION ALL
			    SELECT c.id FROM category c
			    WHERE c.parent_id IN (SELECT id FROM root_categories)
			    OR c.parent_id IN (SELECT id FROM category WHERE parent_id IN (SELECT id FROM root_categories))
			)
			-- 4. 特定されたカテゴリー群に属する商品をランダムに取得
			SELECT i.*
			FROM item i
			WHERE i.category_id IN (SELECT id FROM all_related_categories)
			  AND i.status = '出品中'
			  AND i.user_id <> :userId
			ORDER BY RANDOM()
			LIMIT 8
			""", nativeQuery = true)
	List<Item> findRecommendedItems(
			@Param("categoryIds") List<Long> categoryIds,
			@Param("userId") Long userId);

	@Query("SELECT i FROM Item i WHERE i.status = :status AND (" +
			"i.category.id = :categoryId OR " + // 孫が一致
			"i.category.parent.id = :categoryId OR " + // 子が一致
			"i.category.parent.parent.id = :categoryId)") // 親が一致
	Page<Item> findByHierarchyCategoryAndStatus(
			@Param("categoryId") Long categoryId,
			@Param("status") String status,
			Pageable pageable);

	// 2. キーワード ＋ 階層カテゴリー対応の絞り込み
	@Query("SELECT i FROM Item i WHERE i.status = :status " +
			"AND LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
			"AND (i.category.id = :categoryId OR " +
			"i.category.parent.id = :categoryId OR " +
			"i.category.parent.parent.id = :categoryId)")
	Page<Item> findByNameAndHierarchyCategoryAndStatus(
			@Param("name") String name,
			@Param("categoryId") Long categoryId,
			@Param("status") String status,
			Pageable pageable);

}
