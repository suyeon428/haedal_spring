package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.UserDetailResponseDto;
import com.example.demo.dto.UserSimpleResponseDto;
import com.example.demo.dto.UserUpdateRequestDto;
import com.example.demo.repository.FollowRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    @Autowired
    public UserService(UserRepository userRepository, ImageService imageService, PostRepository postRepository, FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.postRepository = postRepository;
        this.followRepository = followRepository;
    }

    public UserSimpleResponseDto saveUser(User newUser) {
        // 중복 회원 검증
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new IllegalStateException("중복되는 username입니다.");
        }

        userRepository.save(newUser);
        return convertUserToSimpleDto(newUser, newUser);
    }

    public UserSimpleResponseDto convertUserToSimpleDto(User currentUser, User targetUser){ // User -> dto 이후 수정예정
        String imageUrl = targetUser.getImageUrl();
        String imageData = imageService.encodeImageToBase64
                (System.getProperty("user.dir") + "/src/main/resources/static/" + imageUrl);
        return new UserSimpleResponseDto(
                targetUser.getId(),
                targetUser.getUsername(),
                targetUser.getName(),
                imageData,
                followRepository.existsByFollowerAndFollowing(currentUser, targetUser)
        );
    }
    public UserDetailResponseDto convertUserToDetailDto(User currentUser, User targetUser) {
        String imageUrl = targetUser.getImageUrl();
        String imageData = imageService.encodeImageToBase64
                (System.getProperty("user.dir") + "/src/main/resources/static/" + imageUrl);
        return new UserDetailResponseDto(
                targetUser.getId(),
                targetUser.getUsername(),
                targetUser.getName(),
                imageData,
                false,
                targetUser.getBio(),
                targetUser.getJoinedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")),
                postRepository.countByUser(targetUser),
                followRepository.countByFollowing(targetUser),
                followRepository.countByFollower(targetUser)
        );
    }

    public List<UserSimpleResponseDto> getAllUsers(User currentUser){
        List<User> users = userRepository.findAll();
        users.remove(currentUser);
        return users.stream().map(user -> convertUserToSimpleDto(user, user)).toList();
    }

    public List<UserSimpleResponseDto> getUserByUsername(User currentUser, String username){
        List<UserSimpleResponseDto> user = new ArrayList<>();
        User targetUser = userRepository.findByUsername(username).orElse(null);
        if(targetUser != null){
            UserSimpleResponseDto userSimpleResponseDto = convertUserToSimpleDto(targetUser, targetUser);
            user.add(userSimpleResponseDto);
        }

        return user;
    }

    public UserDetailResponseDto updateUser(User currentUser, UserUpdateRequestDto userUpdateRequestDto) {
        if (userUpdateRequestDto.getUsername() != null) {
            currentUser.setUsername(userUpdateRequestDto.getUsername());
        }
        if (userUpdateRequestDto.getPassword() != null) {
            currentUser.setPassword(userUpdateRequestDto.getPassword());
        }
        if (userUpdateRequestDto.getName() != null) {
            currentUser.setName(userUpdateRequestDto.getName());
        }
        if (userUpdateRequestDto.getBio() != null) {
            currentUser.setBio(userUpdateRequestDto.getBio());
        }

        userRepository.save(currentUser);

        return convertUserToDetailDto(currentUser, currentUser);
    }
    public UserDetailResponseDto getUserDetail(User currentUser, Long targetUserId) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return convertUserToDetailDto(currentUser, targetUser);
    }

}