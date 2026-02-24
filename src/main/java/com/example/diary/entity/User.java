package com.example.diary.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nickname;

    // 소셜 로그인 정보
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SocialProvider provider;  // APPLE, KAKAO, LOCAL

    @Column(unique = true, length = 200)
    private String socialId;  // 소셜 로그인 고유 ID

    @Column()
    private String fcmToken;

    @Column(length = 100)
    private String password;  // PIN 번호 (4자리 숫자, 암호화 저장, nullable)

    @Builder
    public User(String nickname, SocialProvider provider, String socialId, String fcmToken, String password) {
        super();
        this.nickname = nickname;
        this.provider = provider;
        this.socialId = socialId;
        this.fcmToken = fcmToken;
        this.password = password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

}