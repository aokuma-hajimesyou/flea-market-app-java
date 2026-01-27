package com.example.flea_market_app.service;

import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.entity.UserFollow;
import com.example.flea_market_app.repository.UserFollowRepository;
import com.example.flea_market_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleFollow(User follower, Long followedId) {
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userFollowRepository.existsByFollowerAndFollowed(follower, followed)) {
            UserFollow userFollow = userFollowRepository.findByFollowerAndFollowed(follower, followed)
                    .orElseThrow(() -> new IllegalArgumentException("Follow relationship not found"));
            userFollowRepository.delete(userFollow);
            return false; // フォロー解除
        } else {
            UserFollow userFollow = new UserFollow();
            userFollow.setFollower(follower);
            userFollow.setFollowed(followed);
            userFollowRepository.save(userFollow);
            return true; // フォロー登録
        }
    }

    public boolean isFollowing(User follower, Long followedId) {
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userFollowRepository.existsByFollowerAndFollowed(follower, followed);
    }

    public List<User> getFollowingList(User user) {
        return userFollowRepository.findByFollower(user).stream()
                .map(UserFollow::getFollowed)
                .collect(Collectors.toList());
    }

    public List<User> getFollowerList(User user) {
        return userFollowRepository.findByFollowed(user).stream()
                .map(UserFollow::getFollower)
                .collect(Collectors.toList());
    }

    public long getFollowerCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userFollowRepository.countByFollowed(user);
    }
}
