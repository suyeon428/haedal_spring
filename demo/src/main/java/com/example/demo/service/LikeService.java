package com.example.demo.service;

import com.example.demo.domain.Like;
import com.example.demo.domain.Post;
import com.example.demo.domain.User;
import com.example.demo.dto.UserSimpleResponseDto;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Autowired
    public LikeService(LikeRepository likeRepository, PostRepository postRepository, UserService userService) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public void likePost(User currentUser, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
        if (likeRepository.existsByUserAndPost(currentUser, post)) { // 이미 좋아요를 누른 게시물
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }

        Like like = new Like(currentUser, post); // 라이크 엔티티 추가
        likeRepository.save(like); // 최종적으로 라이크 레파지토리에 저장
    }

    public void unlikePost(User currentUser, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        Like like = likeRepository.findByUserAndPost(currentUser, post)
                .orElseThrow(() -> new IllegalArgumentException("좋아요누르지 않은 게시물입니다."));
        likeRepository.delete(like);
    }

    public List<UserSimpleResponseDto> getUsersWhoLikedPost(User currentUser, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        List<Like> likes = likeRepository.findByPost(post);
        return likes.stream()
                .map(like -> userService.convertUserToSimpleDto(currentUser, like.getUser()))
                .toList();
    }
}

