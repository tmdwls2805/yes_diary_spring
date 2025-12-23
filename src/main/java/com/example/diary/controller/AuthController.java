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

    /**
     * FCM 토큰 업데이트
     * Authorization 헤더에서 JWT 토큰으로 현재 로그인한 유저 식별
     *
     * POST /api/auth/fcm
     * Header: Authorization: Bearer {accessToken}
     */
    @PostMapping("/fcm")
    public ResponseEntity<FcmTokenResponse> updateFcmToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody FcmTokenRequest request) {

        FcmTokenResponse response = authService.updateFcmToken(authHeader, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃
     * Authorization 헤더에서 JWT 토큰으로 현재 로그인한 유저 식별
     *
     * POST /api/auth/logout
     * Header: Authorization: Bearer {accessToken}
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @RequestHeader("Authorization") String authHeader   
    ) {
        authService.logout(authHeader);
        return ResponseEntity.ok().build();
    }

    /**
     * 토큰 갱신
     * Authorization 헤더에서 JWT 토큰으로 현재 로그인한 유저 식별
     *
     * POST /api/auth/token/refresh
     * Header: Authorization: Bearer {accessToken}
     */
    @PostMapping("/token/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody TokenRefreshRequest request
    ) {
        TokenRefreshResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 토큰 검증
     * Authorization 헤더에서 JWT 토큰으로 현재 로그인한 유저 식별
     *
     * POST /api/auth/token/verify
     * Header: Authorization: Bearer {accessToken}
     */
    @PostMapping("/token/verify")
    public ResponseEntity<TokenVerifyResponse> verifyToken(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody TokenVerifyRequest request
    ) {
        TokenVerifyResponse response = authService.verifyToken(request);
        return ResponseEntity.ok(response);
    }
}
