package com.example.diary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class YesDiarySpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(YesDiarySpringApplication.class, args);
    }
}
