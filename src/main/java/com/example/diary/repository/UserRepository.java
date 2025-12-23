package com.example.diary.repository;

import com.example.diary.entity.SocialProvider;
import com.example.diary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // socialId와 provider로 유저 조회
    Optional<User> findBySocialIdAndProvider(String socialId, SocialProvider provider);

    // 중복 체크
    boolean existsBySocialIdAndProvider(String socialId, SocialProvider provider);
}
