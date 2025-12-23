package com.example.diary.controller;

import com.example.diary.dto.*;
import com.example.diary.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ========== Kakao Login ==========

    /**
     * 1단계: 카카오 유저 체크
     * - 기존 유저: 바로 로그인 (토큰 반환)
     * - 신규 유저: 카카오 socialId 반환
     *
     * POST /api/auth/kakao/check
     */
    @PostMapping("/kakao/check")
    public ResponseEntity<KakaoLoginCheckResponse> checkKakaoUser(
            @RequestBody KakaoLoginCheckRequest request) {

        KakaoLoginCheckResponse response = authService.checkKakaoUser(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 2단계: 신규 카카오 유저 회원가입 + 로그인
     *
     * POST /api/auth/kakao/register
     */
    @PostMapping("/kakao/register")
    public ResponseEntity<TokenResponse> registerKakaoUser(
            @RequestBody KakaoRegisterRequest request) {

        TokenResponse response = authService.registerAndLoginKakaoUser(request);
        return ResponseEntity.ok(response);
    }

    // ========== Apple Login ==========

    /**
     * 1단계: 애플 유저 체크
     * - 기존 유저: 바로 로그인 (토큰 반환)
     * - 신규 유저: Apple ID 반환
     *
     * POST /api/auth/apple/check
     */
    @PostMapping("/apple/check")
    public ResponseEntity<AppleLoginCheckResponse> checkAppleUser(
            @RequestBody AppleLoginCheckRequest request) {

        AppleLoginCheckResponse response = authService.checkAppleUser(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 2단계: 신규 애플 유저 회원가입 + 로그인
     *
     * POST /api/auth/apple/register
     */
    @PostMapping("/apple/register")
    public ResponseEntity<TokenResponse> registerAppleUser(
            @RequestBody AppleRegisterRequest request) {

        TokenResponse response = authService.registerAndLoginAppleUser(request);
        return ResponseEntity.ok(response);
    }
}
