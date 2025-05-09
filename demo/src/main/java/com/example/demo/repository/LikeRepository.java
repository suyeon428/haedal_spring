package com.example.demo.repository;

import com.example.demo.domain.Like;
import com.example.demo.domain.Post;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface LikeRepository extends JpaRepository<Like, Long>{

        Long countByPost(Post post);
        Optional<Like> findByUserAndPost(User user, Post post);
        boolean existsByUserAndPost(User user, Post post);
        List<Like> findByPost(Post post);
}
