package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.dto.UserSimpleResponseDto;
import com.example.demo.service.AuthService;
import com.example.demo.service.FollowService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FollowController {
    private final FollowService followService;
    private final AuthService authService;

    @Autowired
    public FollowController(AuthService authService, FollowService followService) {
        this.authService = authService;
        this.followService = followService;
    }

    @PostMapping("/follows/{followingId}") // 팔로우 하기
    public ResponseEntity<Void> followUser(@PathVariable Long followingId, HttpServletRequest request) {
        User currentUser = authService.getCurrentUser(request);
        followService.followUser(currentUser, followingId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/follows/{followingId}") // 팔로우 취소
    public ResponseEntity<Void> unfollowUser(@PathVariable Long followingId, HttpServletRequest request) {
        User currentUser = authService.getCurrentUser(request);
        followService.unfollowUser(currentUser, followingId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/follows/{userId}/following") // 유저 팔로워 목록 조회
    public ResponseEntity<List<UserSimpleResponseDto>> getFollowingUsers(@PathVariable Long userId, HttpServletRequest request) {
        User currentUser = authService.getCurrentUser(request);
        List<UserSimpleResponseDto> followingUsers = followService.getFollowingUsers(currentUser, userId);
        return ResponseEntity.ok(followingUsers);
    }

    @GetMapping("/follows/{userId}/follower") // 유저 팔로잉 목록 조회
    public ResponseEntity<List<UserSimpleResponseDto>> getFollowerUsers(@PathVariable Long userId, HttpServletRequest request) {
        User currentUser = authService.getCurrentUser(request);
        List<UserSimpleResponseDto> followerUsers = followService.getFollowerUsers(currentUser, userId);
        return ResponseEntity.ok(followerUsers);
    }
}