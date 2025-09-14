# Yes Diary Spring

Spring Boot, Gradle, JPA를 사용한 일기 관리 애플리케이션입니다.

## 기술 스택

- Java 8
- Spring Boot 2.7.18
- Spring Data JPA
- H2 Database (인메모리)
- Gradle
- Lombok
- Validation

## 프로젝트 구조

```
src/
├── main/
│   ├── java/
│   │   └── com/example/diary/
│   │       ├── YesDiarySpringApplication.java
│   │       ├── controller/
│   │       │   └── DiaryController.java
│   │       ├── dto/
│   │       │   ├── DiaryCreateRequest.java
│   │       │   ├── DiaryUpdateRequest.java
│   │       │   └── DiaryResponse.java
│   │       ├── entity/
│   │       │   └── Diary.java
│   │       ├── repository/
│   │       │   └── DiaryRepository.java
│   │       └── service/
│   │           └── DiaryService.java
│   └── resources/
│       └── application.yml
└── test/
    ├── java/
    │   └── com/example/diary/
    │       ├── controller/
    │       │   └── DiaryControllerTest.java
    │       ├── entity/
    │       │   └── DiaryTest.java
    │       ├── repository/
    │       │   └── DiaryRepositoryTest.java
    │       └── service/
    │           └── DiaryServiceTest.java
    └── resources/
        └── application-test.yml
```

## API 엔드포인트

### 일기 관리
- `POST /api/diaries` - 일기 생성
- `GET /api/diaries` - 모든 일기 조회
- `GET /api/diaries/{id}` - 특정 일기 조회
- `PUT /api/diaries/{id}` - 일기 수정
- `DELETE /api/diaries/{id}` - 일기 삭제
- `GET /api/diaries/author/{author}` - 작성자별 일기 조회

## 실행 방법

1. 프로젝트 클론
```bash
git clone <repository-url>
cd yes-diary-spring
```

2. 애플리케이션 실행
```bash
./gradlew bootRun
```

3. 브라우저에서 접속
- 애플리케이션: http://localhost:8080
- H2 콘솔: http://localhost:8080/h2-console

## 테스트 실행

```bash
./gradlew test
```

## 개발 방식

이 프로젝트는 TDD(Test-Driven Development) 방식으로 개발되었습니다:
1. 테스트 코드 작성
2. 테스트 실행 (실패)
3. 기능 구현
4. 테스트 실행 (성공)
5. 리팩토링

## 설정 확인

### Gradle 설정
- Spring Boot 2.7.18 (Java 8 호환)
- JPA, Web, Validation 스타터 포함
- H2 데이터베이스 (인메모리)
- Lombok 지원

### JPA 설정
- Hibernate DDL: create-drop
- SQL 로깅 활성화
- H2 방언 사용
- JPA Auditing 활성화 (@CreatedDate, @LastModifiedDate)
