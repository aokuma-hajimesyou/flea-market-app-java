
package com.example.flea_market_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.entity.UserFollow;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    Optional<UserFollow> findByFollowerAndFollowed(User follower, User followed);
    boolean existsByFollowerAndFollowed(User follower, User followed);
    List<UserFollow> findByFollower(User follower);
    List<UserFollow> findByFollowed(User followed);
    long countByFollowed(User followed);
}
