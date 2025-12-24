package com.example.diary.repository;

import com.example.diary.entity.Diary;
import com.example.diary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    // 특정 사용자의 모든 일기 조회
    List<Diary> findByUserOrderByDateDesc(User user);

    // 특정 사용자의 특정 날짜 일기 조회
    Optional<Diary> findByUserAndDate(User user, LocalDate date);

    // 특정 사용자의 특정 기간 일기 조회
    List<Diary> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);

    // 특정 사용자의 일기인지 확인
    boolean existsByIdAndUser(Long id, User user);
}
