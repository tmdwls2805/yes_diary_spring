# 1단계: Gradle로 빌드
FROM gradle:7.6.4-jdk17 AS build
WORKDIR /app

# Gradle 파일 복사 (의존성 캐싱)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성 다운로드
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사
COPY src ./src

# JAR 파일 빌드
RUN gradle bootJar --no-daemon

# 2단계: 실행 이미지
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
