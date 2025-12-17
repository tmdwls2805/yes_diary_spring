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

    @Column(length = 500)
    private String profileImageUrl;  // 프로필 이미지 URL

    @Builder
    public User(String nickname,
                SocialProvider provider,
                String socialId,
                String profileImageUrl) {
        super();
        this.nickname = nickname;
        this.provider = provider;
        this.socialId = socialId;
        this.profileImageUrl = profileImageUrl;
    }

    // 업데이트 메서드
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}