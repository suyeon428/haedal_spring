package com.example.demo.controller;


import com.example.demo.domain.Post;
import com.example.demo.domain.User;
import com.example.demo.dto.PostResponseDto;
import com.example.demo.dto.UserSimpleResponseDto;
import com.example.demo.service.AuthService;
import com.example.demo.service.ImageService;
import com.example.demo.service.LikeService;
import com.example.demo.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class PostController {
    private final AuthService authService;

    private final LikeService likeService;
    private final ImageService imageService;
    private final PostService postService;


    @Autowired
    public PostController(AuthService authService, LikeService likeService, ImageService imageService, PostService postService) {
        this.authService = authService;
        this.likeService = likeService;
        this.imageService = imageService;
        this.postService = postService;
    }

    @PostMapping("/posts") // 게시글 만들기
    public ResponseEntity<Void> createPost(@RequestParam("image") MultipartFile image, @RequestParam("content") String content,
                                           HttpServletRequest request) throws IOException {
        User currentUser = authService.getCurrentUser(request);
        String imageUrl = imageService.savePostImage(image);
        Post post = new Post(currentUser, content, imageUrl);
        postService.savePost(post);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/user/{userId}") // 유저별 게시글 가져오기
    public ResponseEntity<List<PostResponseDto>> getPostsByUser(@PathVariable Long userId) {
        List<PostResponseDto> posts = postService.getPostsByUser(userId);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/posts/{postId}/like") // 좋아요 달기
    public ResponseEntity<Void> likePost(@PathVariable Long postId, HttpServletRequest request) {
        User currentUser = authService.getCurrentUser(request);

        likeService.likePost(currentUser, postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}/like") // 좋아요 삭제
    public ResponseEntity<Void> unlikePost(HttpServletRequest request, @PathVariable Long postId) {
        User currentUser = authService.getCurrentUser(request);

        likeService.unlikePost(currentUser, postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/{postId}/like") // 좋아요 한 사람 목록
    public ResponseEntity<List<UserSimpleResponseDto>> getUsersWhoLikedPost(@PathVariable Long postId, HttpServletRequest request) {
        User currentUser = authService.getCurrentUser(request);

        List<UserSimpleResponseDto> usersWhoLikedPost = likeService.getUsersWhoLikedPost(currentUser, postId);
        return ResponseEntity.ok(usersWhoLikedPost);
    }
}
