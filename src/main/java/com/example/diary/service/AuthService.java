package com.example.diary.service;

import com.example.diary.dto.*;
import com.example.diary.entity.SocialProvider;
import com.example.diary.entity.User;
import com.example.diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final KakaoApiService kakaoApiService;
    private final AppleApiService appleApiService;
    private final JwtService jwtService;

    /**
     * 카카오 유저 체크 (기존/신규 구분)
     * - 기존 유저: 바로 로그인 처리 (토큰 발급)
     * - 신규 유저: 카카오 socialId만 반환
     */
    public KakaoLoginCheckResponse checkKakaoUser(KakaoLoginCheckRequest request) {
        // 1. 카카오 API 호출
        Map<String, Object> kakaoUserInfo = kakaoApiService.getKakaoUserInfo(request.getAccessToken());

        String socialId = kakaoApiService.extractSocialId(kakaoUserInfo);

        if (socialId == null) {
            throw new IllegalArgumentException("카카오 socialId를 가져올 수 없습니다");
        }

        // 2. 기존 유저 확인
        User user = userRepository.findBySocialIdAndProvider(socialId, SocialProvider.KAKAO)
                .orElse(null);

        if (user != null) {
            // 기존 유저 - 바로 로그인 (토큰 발급)
            String accessToken = jwtService.generateAccessToken(user.getId());
            String refreshToken = jwtService.generateRefreshToken(user.getId());

            KakaoLoginCheckResponse.UserInfo userInfo = new KakaoLoginCheckResponse.UserInfo(
                user.getId(),
                user.getNickname(),
                user.getProvider().name(),
                user.getCreatedAt().toString(),
                user.getUpdatedAt().toString()
            );

            KakaoLoginCheckResponse.TokenInfo tokenInfo = new KakaoLoginCheckResponse.TokenInfo(
                accessToken,
                refreshToken,
                userInfo
            );

            log.info("기존 카카오 유저 로그인: userId={}, nickname={}", user.getId(), user.getNickname());

            return new KakaoLoginCheckResponse(true, null, tokenInfo);
        } else {
            // 신규 유저 - 카카오 socialId만 반환 (닉네임은 사용자가 직접 입력)
            KakaoLoginCheckResponse.KakaoUserInfo kakaoInfo =
                new KakaoLoginCheckResponse.KakaoUserInfo(socialId);

            log.info("신규 카카오 유저 확인: socialId={}", socialId);

            return new KakaoLoginCheckResponse(false, kakaoInfo, null);
        }
    }

    /**
     * 카카오 회원가입 + 로그인
     */
    @Transactional
    public TokenResponse registerAndLoginKakaoUser(KakaoRegisterRequest request) {
        // 1. 카카오 access_token 재검증
        Map<String, Object> kakaoUserInfo = kakaoApiService.getKakaoUserInfo(request.getAccessToken());
        String socialId = kakaoApiService.extractSocialId(kakaoUserInfo);

        if (socialId == null) {
            throw new IllegalArgumentException("유효하지 않은 카카오 토큰입니다");
        }

        // 2. 중복 체크
        if (userRepository.existsBySocialIdAndProvider(socialId, SocialProvider.KAKAO)) {
            throw new IllegalArgumentException("이미 가입된 사용자입니다");
        }

        // 3. User 생성
        User user = User.builder()
                .nickname(request.getNickname())
                .provider(SocialProvider.KAKAO)
                .socialId(socialId)
                .build();

        User savedUser = userRepository.save(user);

        log.info("신규 카카오 유저 가입 완료: userId={}, nickname={}", savedUser.getId(), savedUser.getNickname());

        // 4. JWT 토큰 발급
        String accessToken = jwtService.generateAccessToken(savedUser.getId());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getId());

        // 5. 응답 생성
        TokenResponse.UserInfo userInfo = new TokenResponse.UserInfo(savedUser);
        return new TokenResponse(accessToken, refreshToken, userInfo);
    }

    /**
     * 애플 유저 체크 (기존/신규 구분)
     * - 기존 유저: 바로 로그인 처리 (토큰 발급)
     * - 신규 유저: Apple ID만 반환
     */
    public AppleLoginCheckResponse checkAppleUser(AppleLoginCheckRequest request) {
        // 1. Apple Identity Token 검증
        Map<String, Object> appleUserInfo = appleApiService.verifyIdentityToken(request.getIdentityToken());

        String appleId = appleApiService.extractAppleId(appleUserInfo);
        String email = appleApiService.extractEmail(appleUserInfo);

        if (appleId == null) {
            throw new IllegalArgumentException("Apple ID를 가져올 수 없습니다");
        }

        // 2. 기존 유저 확인
        User user = userRepository.findBySocialIdAndProvider(appleId, SocialProvider.APPLE)
                .orElse(null);

        if (user != null) {
            // 기존 유저 - 바로 로그인 (토큰 발급)
            String accessToken = jwtService.generateAccessToken(user.getId());
            String refreshToken = jwtService.generateRefreshToken(user.getId());

            AppleLoginCheckResponse.UserInfo userInfo = new AppleLoginCheckResponse.UserInfo(
                user.getId(),
                user.getNickname(),
                user.getProvider().name(),
                user.getCreatedAt().toString(),
                user.getUpdatedAt().toString()
            );

            AppleLoginCheckResponse.TokenInfo tokenInfo = new AppleLoginCheckResponse.TokenInfo(
                accessToken,
                refreshToken,
                userInfo
            );

            log.info("기존 애플 유저 로그인: userId={}, nickname={}", user.getId(), user.getNickname());

            return new AppleLoginCheckResponse(true, null, tokenInfo);
        } else {
            // 신규 유저 - Apple ID와 email만 반환
            AppleLoginCheckResponse.AppleUserInfo appleInfo =
                new AppleLoginCheckResponse.AppleUserInfo(appleId, email);

            log.info("신규 애플 유저 확인: appleId={}, email={}", appleId, email);

            return new AppleLoginCheckResponse(false, appleInfo, null);
        }
    }

    /**
     * 애플 회원가입 + 로그인
     */
    @Transactional
    public TokenResponse registerAndLoginAppleUser(AppleRegisterRequest request) {
        // 1. Apple Identity Token 재검증
        Map<String, Object> appleUserInfo = appleApiService.verifyIdentityToken(request.getIdentityToken());
        String appleId = appleApiService.extractAppleId(appleUserInfo);

        if (appleId == null) {
            throw new IllegalArgumentException("유효하지 않은 Apple 토큰입니다");
        }

        // 2. 중복 체크
        if (userRepository.existsBySocialIdAndProvider(appleId, SocialProvider.APPLE)) {
            throw new IllegalArgumentException("이미 가입된 사용자입니다");
        }

        // 3. User 생성
        User user = User.builder()
                .nickname(request.getNickname())
                .provider(SocialProvider.APPLE)
                .socialId(appleId)
                .build();

        User savedUser = userRepository.save(user);

        log.info("신규 애플 유저 가입 완료: userId={}, nickname={}", savedUser.getId(), savedUser.getNickname());

        // 4. JWT 토큰 발급
        String accessToken = jwtService.generateAccessToken(savedUser.getId());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getId());

        // 5. 응답 생성
        TokenResponse.UserInfo userInfo = new TokenResponse.UserInfo(savedUser);
        return new TokenResponse(accessToken, refreshToken, userInfo);
    }

    /**
     * FCM 토큰 업데이트
     * Authorization 헤더에서 JWT 토큰으로 현재 로그인한 유저 식별
     */
    @Transactional
    public FcmTokenResponse updateFcmToken(String authHeader, FcmTokenRequest request) {
        // 1. JWT 토큰에서 userId 추출
        Long userId = jwtService.getUserIdFromAuthHeader(authHeader);

        // 2. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        // 3. FCM 토큰 업데이트
        user.updateFcmToken(request.getFcmToken());
        User savedUser = userRepository.save(user);

        log.info("FCM 토큰 업데이트 완료: userId={}, fcmToken={}", userId, request.getFcmToken());

        return new FcmTokenResponse(savedUser.getFcmToken());
    }

    @Transactional
    public void logout(String authHeader){
        // 1. JWT 토큰에서 userId 추출
        Long userId = jwtService.getUserIdFromAuthHeader(authHeader);

        // 2. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));
    
        // 3. FCM 토큰 초기화
        user.updateFcmToken(null);
        userRepository.save(user);

        log.info("로그아웃 완료: userId={}", userId);
    }
}
