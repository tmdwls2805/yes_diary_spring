package com.example.diary.repository;

import com.example.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    
    /**
     * 작성자별 일기 목록 조회
     * @param author 작성자명
     * @return 해당 작성자의 일기 목록
     */
    List<Diary> findByAuthor(String author);
    
    /**
     * 제목에 특정 키워드가 포함된 일기 목록 조회
     * @param keyword 검색 키워드
     * @return 키워드가 포함된 일기 목록
     */
    List<Diary> findByTitleContaining(String keyword);
}
