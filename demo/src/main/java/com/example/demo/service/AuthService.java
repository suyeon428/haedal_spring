package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.UserSimpleResponseDto;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    private AuthService(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public UserSimpleResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) {
        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!user.getPassword().equals(loginRequestDto.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        HttpSession session = request.getSession(); // session이 존재하지 않으면 새로운 세션 생성
        session.setAttribute("user", user);

        return userService.convertUserToSimpleDto(user, user);
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false를 전달하면, 현재 session이 존재하지 않으면 null을 반환
        if (session != null) {
            session.invalidate();  // 세션 비활성화
        }
    }

    public User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            throw new IllegalStateException("로그인되지 않았습니다.");  // unAuthorized
        }
        return (User) session.getAttribute("user");
    }

}
