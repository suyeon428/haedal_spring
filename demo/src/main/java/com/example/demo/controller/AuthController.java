package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.UserRegistrationRequestDto;
import com.example.demo.dto.UserSimpleResponseDto;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final UserService userService;
    private final AuthService authservice;
    private final AuthService authService;

    @Autowired
    public AuthController(UserService userService, AuthService authservice, AuthService authService){
        this.userService = userService;
        this.authservice = authservice;
        this.authService = authService;
    }

@PostMapping("/auth/register")
public ResponseEntity<UserSimpleResponseDto> registerUser(@RequestBody UserRegistrationRequestDto userRegistrationRequestDto) {
    User user = new User(
            userRegistrationRequestDto.getUsername(),
            userRegistrationRequestDto.getPassword(),
            userRegistrationRequestDto.getName()
    );
    UserSimpleResponseDto savedUser = userService.saveUser(user);

    return ResponseEntity.ok(savedUser);
}

    @PostMapping("/auth/login")
    public ResponseEntity<UserSimpleResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        UserSimpleResponseDto userSimpleResponseDto = authService.login(loginRequestDto, request);
        return ResponseEntity.ok(userSimpleResponseDto);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/auth/me")
    public ResponseEntity<UserSimpleResponseDto> me(HttpServletRequest request) {
        User currentUser = authService.getCurrentUser(request);

        UserSimpleResponseDto userSimpleResponseDto = userService.convertUserToSimpleDto(currentUser, currentUser);
        return ResponseEntity.ok(userSimpleResponseDto);
    }
}

