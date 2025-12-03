package com.example.flea_market_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flea_market_app.entity.FavoriteItem;
import com.example.flea_market_app.entity.Item;
import com.example.flea_market_app.entity.User;

@Repository
public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Long> {
	Optional<FavoriteItem> findByUserAndItem(UserRepository user, Item item);

	List<FavoriteItem> findByUser(User user);

	boolean existsByUserAndItem(User user, Item item);
}
