package com.example.flea_market_app.controller;

import com.example.flea_market_app.entity.User;
import com.example.flea_market_app.service.FollowService;
import com.example.flea_market_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    @PostMapping("/{userId}/toggle-follow")
    public ResponseEntity<?> toggleFollow(@PathVariable Long userId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        User currentUser = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isFollowing = followService.toggleFollow(currentUser, userId);

        return ResponseEntity.ok(Map.of("following", isFollowing));
    }
}
