# 📝 Yes Diary Spring

Spring Boot, Gradle, JPA를 사용한 일기 관리 애플리케이션입니다.

## 🛠️ 기술 스택

- **Java 8** - 프로그래밍 언어
- **Spring Boot 2.7.18** - 애플리케이션 프레임워크
- **Spring Data JPA** - 데이터 접근 계층
- **H2 Database** - 인메모리 데이터베이스
- **Gradle** - 빌드 도구
- **Lombok** - 코드 간소화
- **Validation** - 입력 검증

## 🏗️ 프로젝트 구조

### 📁 전체 디렉토리 구조
```
yes-diary-spring/
├── 📄 build.gradle                    # Gradle 빌드 설정
├── 📄 settings.gradle                 # Gradle 프로젝트 설정
├── 📄 gradlew.bat                     # Windows용 Gradle Wrapper
├── 📄 .gitignore                      # Git 무시 파일 설정
├── 📄 README.md                       # 프로젝트 문서
├── 📁 gradle/wrapper/                 # Gradle Wrapper 파일들
│   ├── gradle-wrapper.properties
│   └── gradle-wrapper.jar
└── 📁 src/
    ├── 📁 main/                       # 메인 소스 코드
    │   ├── 📁 java/com/example/diary/
    │   │   ├── 🚀 YesDiarySpringApplication.java
    │   │   ├── 📁 controller/         # 웹 계층
    │   │   ├── 📁 dto/               # 데이터 전송 객체
    │   │   ├── 📁 entity/            # JPA 엔티티
    │   │   ├── 📁 repository/        # 데이터 접근 계층
    │   │   └── 📁 service/           # 비즈니스 로직 계층
    │   └── 📁 resources/
    │       └── 📄 application.yml     # 애플리케이션 설정
    └── 📁 test/                       # 테스트 코드
        ├── 📁 java/com/example/diary/
        │   ├── 📁 controller/         # 컨트롤러 테스트
        │   ├── 📁 entity/            # 엔티티 테스트
        │   ├── 📁 repository/        # 리포지토리 테스트
        │   └── 📁 service/           # 서비스 테스트
        └── 📁 resources/
            └── 📄 application-test.yml # 테스트 설정
```

## 🎯 계층별 구조 및 역할

### 1️⃣ **Entity 계층** (`entity/`)
**역할**: 데이터베이스 테이블과 매핑되는 객체
```java
📄 Diary.java
├── @Entity, @Table          # JPA 매핑 어노테이션
├── @Id, @GeneratedValue     # 기본키 설정
├── @Column                  # 컬럼 매핑
├── @CreatedDate             # 생성일시 자동 관리
├── @LastModifiedDate        # 수정일시 자동 관리
└── 비즈니스 메서드          # updateTitle(), updateContent()
```

### 2️⃣ **Repository 계층** (`repository/`)
**역할**: 데이터베이스 접근 및 쿼리 처리
```java
📄 DiaryRepository.java
├── JpaRepository<Diary, Long> 상속
├── 기본 CRUD 메서드 자동 제공
├── findByAuthor()           # 작성자별 조회
└── findByTitleContaining()  # 제목 검색
```

### 3️⃣ **Service 계층** (`service/`)
**역할**: 비즈니스 로직 처리 및 트랜잭션 관리
```java
📄 DiaryService.java
├── @Service                 # 서비스 계층 표시
├── @Transactional           # 트랜잭션 관리
├── createDiary()           # 일기 생성
├── getDiary()              # 일기 조회
├── getAllDiaries()         # 전체 조회
├── updateDiary()           # 일기 수정
├── deleteDiary()           # 일기 삭제
└── getDiariesByAuthor()    # 작성자별 조회
```

### 4️⃣ **DTO 계층** (`dto/`)
**역할**: 계층 간 데이터 전송 및 검증

#### 📄 DiaryCreateRequest.java
```java
├── @NotBlank, @Size        # 입력 검증
├── title                   # 제목
├── content                 # 내용
└── author                  # 작성자
```

#### 📄 DiaryUpdateRequest.java
```java
├── @Size                   # 입력 검증
├── title                   # 수정할 제목 (선택)
└── content                 # 수정할 내용 (선택)
```

#### 📄 DiaryResponse.java
```java
├── from() 메서드           # Entity → DTO 변환
├── id                      # 일기 ID
├── title                   # 제목
├── content                 # 내용
├── author                  # 작성자
├── createdAt               # 생성일시
└── updatedAt               # 수정일시
```

### 5️⃣ **Controller 계층** (`controller/`)
**역할**: HTTP 요청/응답 처리 및 API 엔드포인트 제공
```java
📄 DiaryController.java
├── @RestController         # REST API 컨트롤러
├── @RequestMapping("/api/diaries") # 기본 경로
├── POST /api/diaries       # 일기 생성
├── GET /api/diaries        # 전체 조회
├── GET /api/diaries/{id}   # 단건 조회
├── PUT /api/diaries/{id}   # 일기 수정
├── DELETE /api/diaries/{id} # 일기 삭제
└── GET /api/diaries/author/{author} # 작성자별 조회
```

## 🔄 데이터 흐름 (Request → Response)

```
1. HTTP Request
   ↓
2. Controller (@RestController)
   ↓
3. DTO (Request Validation)
   ↓
4. Service (Business Logic)
   ↓
5. Repository (Data Access)
   ↓
6. Entity (Database Mapping)
   ↓
7. DTO (Response Mapping)
   ↓
8. HTTP Response
```

## 🧪 테스트 구조

### 📁 테스트 계층별 구조
```
test/
├── 📁 controller/          # 웹 계층 테스트
│   └── DiaryControllerTest.java
│       ├── @SpringBootTest
│       ├── @AutoConfigureTestMvc
│       ├── MockMvc 테스트
│       └── JSON 직렬화/역직렬화 테스트
├── 📁 service/            # 비즈니스 로직 테스트
│   └── DiaryServiceTest.java
│       ├── @SpringBootTest
│       ├── @Transactional
│       └── 통합 테스트
├── 📁 repository/         # 데이터 접근 테스트
│   └── DiaryRepositoryTest.java
│       ├── @DataJpaTest
│       ├── TestEntityManager
│       └── 데이터베이스 쿼리 테스트
└── 📁 entity/             # 엔티티 단위 테스트
    └── DiaryTest.java
        ├── 단위 테스트
        └── AssertJ 검증
```

## 🚀 API 엔드포인트

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/diaries` | 일기 생성 | DiaryCreateRequest | DiaryResponse |
| GET | `/api/diaries` | 모든 일기 조회 | - | List<DiaryResponse> |
| GET | `/api/diaries/{id}` | 특정 일기 조회 | - | DiaryResponse |
| PUT | `/api/diaries/{id}` | 일기 수정 | DiaryUpdateRequest | DiaryResponse |
| DELETE | `/api/diaries/{id}` | 일기 삭제 | - | 204 No Content |
| GET | `/api/diaries/author/{author}` | 작성자별 조회 | - | List<DiaryResponse> |

## 🏃‍♂️ 실행 방법

### 1. 프로젝트 클론
```bash
git clone https://github.com/tmdwls2805/yes_diary_spring.git
cd yes_diary_spring
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 3. 접속 확인
- **애플리케이션**: http://localhost:8080
- **H2 콘솔**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: `password`

## 🧪 테스트 실행

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests DiaryServiceTest

# 테스트 리포트 확인
./gradlew test jacocoTestReport
```

## 🎯 개발 방식 (TDD)

이 프로젝트는 **Test-Driven Development** 방식으로 개발되었습니다:

```
1. 🔴 Red    - 실패하는 테스트 작성
2. 🟢 Green  - 테스트를 통과하는 최소한의 코드 구현
3. 🔵 Refactor - 코드 개선 및 리팩토링
4. 🔄 Repeat - 다음 기능으로 반복
```

### 테스트 우선순위
1. **Entity 테스트** - 도메인 로직 검증
2. **Repository 테스트** - 데이터 접근 검증
3. **Service 테스트** - 비즈니스 로직 검증
4. **Controller 테스트** - API 엔드포인트 검증

## ⚙️ 설정 정보

### Gradle 설정 (`build.gradle`)
- Spring Boot 2.7.18 (Java 8 호환)
- JPA, Web, Validation 스타터
- H2 데이터베이스 (인메모리)
- Lombok 지원

### JPA 설정 (`application.yml`)
- Hibernate DDL: `create-drop`
- SQL 로깅 활성화
- H2 방언 사용
- JPA Auditing 활성화

## 📊 프로젝트 특징

- ✅ **계층형 아키텍처** - 관심사 분리
- ✅ **TDD 개발** - 높은 테스트 커버리지
- ✅ **RESTful API** - 표준 HTTP 메서드 사용
- ✅ **입력 검증** - Bean Validation 활용
- ✅ **자동 시간 관리** - JPA Auditing
- ✅ **코드 간소화** - Lombok 활용
