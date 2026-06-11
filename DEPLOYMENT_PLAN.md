# yes_diary 배포 및 인프라 구축 계획

> Cloudflare + AWS + Firebase 조합으로 비용 최소화 + 성능 최대화
> **목표**: 1년차 월 $1, 2년차 월 $23로 실서비스 운영

---

## 📋 목차

1. [최종 아키텍처](#-최종-아키텍처)
2. [전체 비용 계획](#-전체-비용-계획)
3. [STEP 1: 도메인 구매](#step-1-도메인-구매-10분-10년)
4. [STEP 2: Cloudflare 셋업](#step-2-cloudflare-셋업-30분-0)
5. [STEP 3: AWS 셋업](#step-3-aws-셋업-2시간)
6. [STEP 4: Nginx 설정](#step-4-nginx-설정-1시간)
7. [STEP 5: Spring Boot 운영 설정](#step-5-spring-boot-운영-설정-1일)
8. [STEP 6: 배포](#step-6-배포-2시간)
9. [STEP 7: Firebase GA4 셋업](#step-7-firebase-ga4-셋업-1시간-0)
10. [STEP 8: 모니터링 통합](#step-8-모니터링-통합-1시간)
11. [STEP 9: CI/CD 자동화](#step-9-cicd-자동화-1일)
12. [실행 우선순위](#-실행-우선순위)
13. [출시 전 최종 체크리스트](#-출시-전-최종-체크리스트)

---

## 🎯 최종 아키텍처

```
[iOS 앱]
   ↓
[Firebase Analytics (GA4)] ← 사용자 행동 분석
   ↓
[Cloudflare DNS + CDN + WAF]  ← 무료
   ↓ HTTPS (Cloudflare SSL)
[EC2 t2.micro 프리티어]
   ├── Nginx (리버스 프록시)
   └── Docker: Spring Boot
       ↓
[RDS db.t2.micro 프리티어]
   └── MySQL 8.0
       ↓
[S3] 백업 + 이미지 저장
```

### 왜 이 조합인가

- **Cloudflare**: 무료 CDN/SSL/WAF/DDoS 방어 → AWS 트래픽 비용↓, 보안↑
- **Firebase GA4**: 무료 사용자 분석, iOS 앱 최적화
- **AWS**: 프리티어로 1년 거의 무료 운영
- **Nginx**: EC2에서 HTTPS 종료 + 리버스 프록시

---

## 💰 전체 비용 계획

### 1년차 (프리티어 활용)

| 항목 | 월 비용 |
|---|---|
| 도메인 (.com) | $1 (연 $12) |
| Cloudflare Free | $0 |
| AWS 프리티어 (EC2 + RDS + S3) | $0 |
| Firebase Spark (GA4) | $0 |
| Route 53 | $0 (Cloudflare DNS 사용) |
| **합계** | **월 $1** |

### 2년차 (프리티어 종료 후)

| 항목 | 월 비용 |
|---|---|
| 도메인 | $1 |
| Cloudflare Free | $0 |
| EC2 t3.micro Reserved | $5 |
| RDS db.t3.micro Reserved | $9 |
| EBS + 스토리지 | $5 |
| 데이터 전송 (Cloudflare로 절감) | $3 |
| Firebase | $0 |
| **합계** | **월 $23** |

> Cloudflare 덕분에 AWS 트래픽 비용 약 70% 절감

---

## STEP 1: 도메인 구매 (10분, $10/년)

### 등록기관 비교

| 등록기관 | .com 가격 | 추천도 | 이유 |
|---|---|---|---|
| **Cloudflare Registrar** | 연 $10 | ⭐⭐⭐⭐⭐ | 마진 0%, 자동 DNS 연결 |
| **가비아** | 연 $10 | ⭐⭐⭐⭐ | 한국 회사, 결제 편리 |
| **Namecheap** | 연 $11 | ⭐⭐⭐ | 글로벌 표준 |
| **Route 53** | 연 $14 | ⭐⭐ | AWS 통합 좋지만 비쌈 |
| GoDaddy | 연 $20+ | ⭐ | 비쌈 |

### 추천: Cloudflare Registrar

- 마진 없이 원가 ($9.15 for .com)
- Cloudflare DNS와 자동 연결
- 갱신 가격 동일 (다른 곳은 첫해만 싸고 갱신 시 비쌈)
- WHOIS 보호 무료

### 도메인명 후보

- `yes-diary.com`
- `yesdiary.app` (.app은 HTTPS 강제)
- `yes-diary.co.kr` (가비아에서 구매)

---

## STEP 2: Cloudflare 셋업 (30분, $0)

### Cloudflare 무료 제공 기능

| 기능 | 무료 | 대체 시 비용 |
|---|---|---|
| 글로벌 CDN | ✅ | AWS CloudFront $30+/월 |
| 무제한 SSL 인증서 | ✅ | - |
| DDoS 방어 | ✅ | AWS Shield $3,000/월 |
| WAF (방화벽) | ✅ 기본 | AWS WAF $5+/월 |
| DNS 관리 | ✅ | Route 53 $0.5/월 |
| 트래픽 분석 | ✅ | - |
| 봇 방어 | ✅ | - |
| 캐싱 | ✅ | - |

### 셋업 단계

#### 1. Cloudflare 계정 생성
- cloudflare.com 가입
- 무료 플랜 선택

#### 2. 도메인 추가
- "Add a Site" → `yes-diary.com` 입력
- Free 플랜 선택

#### 3. 네임서버 변경
가비아/Namecheap 대시보드에서 네임서버 변경:
```
daniel.ns.cloudflare.com
sarah.ns.cloudflare.com
```
(Cloudflare Registrar로 샀으면 이 단계 스킵)

#### 4. DNS 레코드 설정

```
Type   Name        Content              Proxy    TTL
A      @           [EC2 Elastic IP]     🟧 ON    Auto
A      api         [EC2 Elastic IP]     🟧 ON    Auto
A      www         [EC2 Elastic IP]     🟧 ON    Auto
CNAME  *           yes-diary.com        🟧 ON    Auto
```

Proxy(🟧) 켜기 = Cloudflare 거쳐서 트래픽 → CDN/WAF 적용

#### 5. SSL/TLS 설정

```
SSL/TLS → Overview → Full (strict)
SSL/TLS → Edge Certificates
  ├── Always Use HTTPS: ON
  ├── HSTS: ON (6 months)
  ├── Minimum TLS Version: 1.2
  └── Automatic HTTPS Rewrites: ON
```

#### 6. 성능 최적화

```
Speed → Optimization
  ├── Auto Minify: JS, CSS, HTML 다 ON
  ├── Brotli: ON
  └── Early Hints: ON

Caching → Configuration
  ├── Browser Cache TTL: 4 hours
  └── Always Online: ON
```

#### 7. 보안 설정

```
Security → Settings
  ├── Security Level: Medium
  ├── Bot Fight Mode: ON
  └── Challenge Passage: 30 min

Security → WAF
  └── Managed Rules: 기본 ON
```

#### 8. 페이지 룰 (3개 무료)

```
Rule 1: api.yes-diary.com/*
  Cache Level: Bypass (API는 캐시 안 함)
  SSL: Full (strict)

Rule 2: yes-diary.com/static/*
  Cache Level: Cache Everything
  Edge Cache TTL: 1 month

Rule 3: yes-diary.com/*
  Always Use HTTPS
```

---

## STEP 3: AWS 셋업 (2시간)

### 3-1. AWS 계정 생성

#### 필수 작업

- [ ] 신용카드 등록
- [ ] **root MFA 활성화** (즉시)
- [ ] IAM 사용자 생성 (root 직접 사용 금지)
- [ ] Billing 알림 활성화

#### AWS Budgets 설정 (필수)

```
Budget 1: 월 $1 도달 → 이메일 알림
Budget 2: 월 $10 도달 → 이메일 알림
Budget 3: 월 $30 도달 → SMS 알림
```

### 3-2. 리전 선택

**서울 리전 (ap-northeast-2)** 사용
- iOS 앱이 한국 대상이면 필수
- 지연시간 10ms 이하

### 3-3. VPC 구성

```
VPC: yes-diary-vpc (10.0.0.0/16)
├── Public Subnet (10.0.1.0/24) - ap-northeast-2a → EC2
├── Private Subnet (10.0.2.0/24) - ap-northeast-2a → RDS
└── Private Subnet (10.0.3.0/24) - ap-northeast-2c → RDS (서브넷그룹용)

Internet Gateway: yes-diary-igw
Route Table:
  - Public: 0.0.0.0/0 → IGW
  - Private: 로컬만
```

### 3-4. Security Group

Cloudflare IP 범위만 EC2에 접근 허용 (보안 핵심)

```
sg-ec2 (EC2)
인바운드:
  - 22 (SSH): 내 IP만
  - 80 (HTTP): Cloudflare IP 범위
  - 443 (HTTPS): Cloudflare IP 범위
```

#### Cloudflare IPv4 범위

```
173.245.48.0/20
103.21.244.0/22
103.22.200.0/22
103.31.4.0/22
141.101.64.0/18
108.162.192.0/18
190.93.240.0/20
188.114.96.0/20
197.234.240.0/22
198.41.128.0/17
162.158.0.0/15
104.16.0.0/13
104.24.0.0/14
172.64.0.0/13
131.0.72.0/22
```

```
sg-rds (RDS)
인바운드:
  - 3306: sg-ec2에서만
```

> 핵심: 일반 인터넷에서 EC2 직접 접근 불가 → Cloudflare 통해서만 접근 가능

### 3-5. RDS 생성

```
엔진: MySQL 8.0
템플릿: 프리티어
인스턴스: db.t2.micro
스토리지: 20GB gp2
Multi-AZ: 비활성화 (프리티어 아님)
VPC: yes-diary-vpc
서브넷 그룹: Private subnet들
퍼블릭 액세스: 아니요 ⚠️
Security Group: sg-rds
DB 이름: diary_db
마스터: admin / 강력한_비밀번호
백업 보관: 7일
삭제 보호: 활성화
```

### 3-6. EC2 생성

```
AMI: Amazon Linux 2023
인스턴스: t2.micro (프리티어)
키페어: yes-diary-key.pem 생성 (안전 보관)
VPC: yes-diary-vpc
서브넷: Public Subnet
퍼블릭 IP: 자동 할당
Security Group: sg-ec2
스토리지: 30GB gp2
```

#### User Data (초기 스크립트)

```bash
#!/bin/bash
yum update -y
yum install -y docker git
systemctl start docker
systemctl enable docker
usermod -aG docker ec2-user

# Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" \
  -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Nginx
yum install -y nginx
systemctl enable nginx
```

### 3-7. Elastic IP 할당

- Elastic IP 할당 → EC2에 연결
- **반드시 연결 상태 유지** (미연결 시 시간당 $0.005 과금)
- 이 IP를 Cloudflare DNS에 등록

---

## STEP 4: Nginx 설정 (1시간)

### Nginx 역할

```
[Cloudflare]
   ↓ HTTPS (Cloudflare ↔ EC2)
[Nginx :443]
   ├── HTTPS 종료
   ├── 리버스 프록시
   ├── Real IP 복원 (Cloudflare → 원래 사용자 IP)
   └── Rate Limiting
   ↓ HTTP :8080
[Spring Boot Docker]
```

### `/etc/nginx/nginx.conf`

```nginx
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log;
pid /run/nginx.pid;

events {
    worker_connections 1024;
}

http {
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;

    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    gzip on;

    # Cloudflare Real IP 복원
    set_real_ip_from 173.245.48.0/20;
    set_real_ip_from 103.21.244.0/22;
    set_real_ip_from 103.22.200.0/22;
    set_real_ip_from 103.31.4.0/22;
    set_real_ip_from 141.101.64.0/18;
    set_real_ip_from 108.162.192.0/18;
    set_real_ip_from 190.93.240.0/20;
    set_real_ip_from 188.114.96.0/20;
    set_real_ip_from 197.234.240.0/22;
    set_real_ip_from 198.41.128.0/17;
    set_real_ip_from 162.158.0.0/15;
    set_real_ip_from 104.16.0.0/13;
    set_real_ip_from 104.24.0.0/14;
    set_real_ip_from 172.64.0.0/13;
    set_real_ip_from 131.0.72.0/22;
    real_ip_header CF-Connecting-IP;

    # Rate Limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req_zone $binary_remote_addr zone=auth:10m rate=5r/m;

    include /etc/nginx/conf.d/*.conf;
}
```

### `/etc/nginx/conf.d/yes-diary.conf`

```nginx
# HTTP → HTTPS 리다이렉트
server {
    listen 80;
    server_name yes-diary.com api.yes-diary.com;
    return 301 https://$host$request_uri;
}

# API 서버
server {
    listen 443 ssl http2;
    server_name api.yes-diary.com;

    # Cloudflare Origin Certificate
    ssl_certificate /etc/ssl/cloudflare/origin.pem;
    ssl_certificate_key /etc/ssl/cloudflare/origin.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Cloudflare 외 직접 접근 차단
    if ($http_cf_connecting_ip = "") {
        return 403;
    }

    # 보안 헤더
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Strict-Transport-Security "max-age=31536000" always;

    # 인증 엔드포인트 (브루트포스 방지)
    location ~ ^/api/auth/(login|signup) {
        limit_req zone=auth burst=3 nodelay;
        proxy_pass http://127.0.0.1:8080;
        include /etc/nginx/proxy_params;
    }

    # 일반 API
    location / {
        limit_req zone=api burst=20 nodelay;
        proxy_pass http://127.0.0.1:8080;
        include /etc/nginx/proxy_params;
    }

    # 헬스체크
    location /actuator/health {
        proxy_pass http://127.0.0.1:8080;
        access_log off;
    }

    client_max_body_size 10M;
}
```

### `/etc/nginx/proxy_params`

```nginx
proxy_set_header Host $host;
proxy_set_header X-Real-IP $http_cf_connecting_ip;
proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
proxy_set_header X-Forwarded-Proto https;
proxy_http_version 1.1;
proxy_set_header Connection "";
proxy_read_timeout 60s;
proxy_connect_timeout 5s;
```

### Cloudflare Origin Certificate (15년 무료)

1. Cloudflare 대시보드 → SSL/TLS → Origin Server
2. "Create Certificate" → 15년 선택
3. EC2에 저장:

```bash
sudo mkdir -p /etc/ssl/cloudflare
sudo nano /etc/ssl/cloudflare/origin.pem  # 인증서 붙여넣기
sudo nano /etc/ssl/cloudflare/origin.key  # 키 붙여넣기
sudo chmod 600 /etc/ssl/cloudflare/origin.key
```

**장점**: 15년 갱신 안 함, Let's Encrypt cron 자동화 불필요

---

## STEP 5: Spring Boot 운영 설정 (1일)

### 5-1. `application-prod.yml` 새로 생성

```yaml
spring:
  application:
    name: yes-diary-spring
  datasource:
    url: jdbc:mysql://${DB_HOST}:3306/diary_db?characterEncoding=UTF-8&useUnicode=true&serverTimezone=Asia/Seoul
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: false
        dialect: org.hibernate.dialect.MySQL8Dialect

jwt:
  secret: ${JWT_SECRET}
  access-token-validity: 3600000
  refresh-token-validity: 1209600000

apple:
  client-id: ${APPLE_CLIENT_ID}

server:
  port: 8080
  shutdown: graceful
  forward-headers-strategy: native
  tomcat:
    remoteip:
      remote-ip-header: X-Forwarded-For
      protocol-header: X-Forwarded-Proto

logging:
  level:
    root: INFO
    com.example: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: never
      probes:
        enabled: true
```

### 5-2. `build.gradle`에 Actuator 추가

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    // ... 기존 의존성
}
```

### 5-3. Dockerfile 수정

```dockerfile
FROM gradle:7.6.4-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true
COPY src ./src
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
```

### 5-4. CORS 설정

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(
        "https://yes-diary.com",
        "https://api.yes-diary.com"
    ));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

---

## STEP 6: 배포 (2시간)

### 6-1. `/opt/yes-diary/.env`

> EC2에 SCP로 업로드, Git 절대 X

```bash
DB_HOST=yes-diary-db.xxx.ap-northeast-2.rds.amazonaws.com
DB_USERNAME=admin
DB_PASSWORD=강력한_비밀번호_32자이상
JWT_SECRET=랜덤_64자_이상_secret
APPLE_CLIENT_ID=com.silverslab.yesdiary
```

### 6-2. `/opt/yes-diary/docker-compose.prod.yml`

```yaml
version: '3.8'
services:
  app:
    image: yes-diary:latest
    container_name: yes-diary
    restart: always
    ports:
      - "127.0.0.1:8080:8080"
    env_file:
      - .env
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

### 6-3. 배포 명령

```bash
cd /opt/yes-diary
docker build -t yes-diary:latest .
docker-compose -f docker-compose.prod.yml up -d
sudo nginx -s reload
```

---

## STEP 7: Firebase GA4 셋업 (1시간, $0)

### Firebase Spark (무료) 제공

| 기능 | 무료 한도 |
|---|---|
| GA4 분석 | 무제한 |
| 이벤트 추적 | 500개 이벤트 타입 |
| 사용자 속성 | 무제한 |
| Crashlytics | 무료 |
| Remote Config | 무료 |
| A/B Testing | 무료 |
| Cloud Messaging (푸시) | 무료 |
| 동적 링크 | 무료 |

### 7-1. Firebase 프로젝트 생성

- firebase.google.com → "프로젝트 추가"
- 이름: `yes-diary`
- GA4 활성화

### 7-2. iOS 앱 추가

- 번들 ID: `com.silverslab.yesdiary`
- `GoogleService-Info.plist` 다운로드 → Xcode 프로젝트에 추가

### 7-3. Flutter 앱 통합

```yaml
# pubspec.yaml
dependencies:
  firebase_core: ^2.24.0
  firebase_analytics: ^10.7.0
  firebase_crashlytics: ^3.4.0
  firebase_messaging: ^14.7.0
```

```dart
// main.dart
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_analytics/firebase_analytics.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  FirebaseAnalytics.instance.logAppOpen();
  runApp(MyApp());
}
```

### 7-4. 추적할 이벤트 설계

| 이벤트 | 시점 |
|---|---|
| `diary_created` | 일기 작성 완료 |
| `diary_viewed` | 일기 열람 |
| `emotion_selected` | 감정 선택 |
| `login` | 로그인 성공 |
| `signup` | 회원가입 완료 |
| `subscription_started` | 구독 시작 |
| `share_diary` | 일기 공유 |
| `app_open` | 앱 실행 (자동) |
| `screen_view` | 화면 진입 (자동) |

```dart
await FirebaseAnalytics.instance.logEvent(
  name: 'diary_created',
  parameters: {
    'emotion': selectedEmotion,
    'word_count': content.length,
    'has_image': hasImage ? 1 : 0,
  },
);
```

### 7-5. Crashlytics 활성화

```dart
FlutterError.onError = FirebaseCrashlytics.instance.recordFlutterFatalError;
```

### 7-6. Remote Config

```dart
final remoteConfig = FirebaseRemoteConfig.instance;
await remoteConfig.setDefaults({
  'maintenance_mode': false,
  'min_app_version': '1.0.0',
  'force_update_message': '',
});
await remoteConfig.fetchAndActivate();
```

> 앱 업데이트 없이 점검 모드 ON/OFF 가능

---

## STEP 8: 모니터링 통합 (1시간)

### 무료 모니터링 스택

| 도구 | 용도 | 무료 한도 |
|---|---|---|
| Cloudflare Analytics | 트래픽, 봇, WAF | 무제한 |
| Firebase Analytics | 앱 사용자 행동 | 무제한 |
| Firebase Crashlytics | 앱 크래시 | 무제한 |
| AWS CloudWatch | EC2/RDS 메트릭 | 기본 메트릭 무료 |
| UptimeRobot | 외부 가용성 | 50개 모니터 무료 |
| Sentry | 백엔드 에러 추적 | 월 5,000 에러 무료 |

### 8-1. UptimeRobot 설정

- `https://api.yes-diary.com/actuator/health` 5분마다 체크
- 다운 시 이메일/SMS 알림

### 8-2. CloudWatch 알람 (10개 무료)

```
1. EC2 CPU > 80% (5분 연속) → 이메일
2. EC2 메모리 > 85% → 이메일
3. RDS CPU > 80% → 이메일
4. RDS Freeable Memory < 100MB → 이메일
5. RDS Free Storage < 5GB → 이메일
6. RDS Connection > 80% → 이메일
```

### 8-3. Sentry 통합 (Spring Boot)

```gradle
dependencies {
    implementation 'io.sentry:sentry-spring-boot-starter:7.0.0'
}
```

```yaml
sentry:
  dsn: ${SENTRY_DSN}
  environment: production
  traces-sample-rate: 0.1
```

---

## STEP 9: CI/CD 자동화 (1일)

### GitHub Actions 워크플로우

`.github/workflows/deploy.yml`

```yaml
name: Deploy to EC2

on:
  push:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
      - run: ./gradlew test

  deploy:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          java-version: '17'

      - name: Build JAR
        run: ./gradlew bootJar

      - name: Copy to EC2
        uses: appleboy/scp-action@master
        with:
          host: ${{ secrets.EC2_HOST }}
          username: ec2-user
          key: ${{ secrets.EC2_SSH_KEY }}
          source: "build/libs/*.jar,Dockerfile,docker-compose.prod.yml"
          target: "/opt/yes-diary/"
          strip_components: 0

      - name: Deploy
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.EC2_HOST }}
          username: ec2-user
          key: ${{ secrets.EC2_SSH_KEY }}
          script: |
            cd /opt/yes-diary
            docker build -t yes-diary:latest .
            docker-compose -f docker-compose.prod.yml down
            docker-compose -f docker-compose.prod.yml up -d
            docker image prune -f

            # 헬스체크 (60초 대기)
            for i in {1..12}; do
              if curl -f http://localhost:8080/actuator/health; then
                echo "Deploy successful"
                exit 0
              fi
              sleep 5
            done
            echo "Deploy failed - health check timeout"
            exit 1
```

### GitHub Secrets 등록

| Secret | 값 |
|---|---|
| `EC2_HOST` | EC2 Elastic IP |
| `EC2_SSH_KEY` | SSH 키 (.pem 내용) |

---

## 🎯 실행 우선순위

### Week 1: 인프라 기본

| Day | 작업 |
|---|---|
| Day 1 | 도메인 구매 + Cloudflare 셋업 |
| Day 2 | AWS 계정 + VPC + RDS + EC2 |
| Day 3 | Nginx + 첫 배포 |
| Day 4-5 | Spring Boot 운영 설정 + 테스트 |

### Week 2: 자동화 + 분석

| Day | 작업 |
|---|---|
| Day 6 | GitHub Actions CI/CD |
| Day 7 | Firebase GA4 + Crashlytics |
| Day 8 | 모니터링 (CloudWatch + UptimeRobot) |
| Day 9 | 보안 점검 + 백업 검증 |

### Week 3: 출시 준비

- 베타 테스트
- 부하 테스트
- 백업/복구 테스트
- 앱스토어 제출

---

## ✅ 출시 전 최종 체크리스트

### 보안

- [ ] AWS root MFA 활성화
- [ ] EC2 SSH 22번 포트 내 IP만
- [ ] RDS 퍼블릭 액세스 차단
- [ ] EC2 Security Group: Cloudflare IP만 허용
- [ ] `.env` 파일 Git 미커밋
- [ ] JWT secret 64자 이상
- [ ] HTTPS 강제 (Cloudflare Always Use HTTPS)
- [ ] Cloudflare WAF 활성화

### 안정성

- [ ] RDS 자동 백업 7일
- [ ] RDS 삭제 보호 ON
- [ ] EC2 Elastic IP 연결
- [ ] Nginx Rate Limiting
- [ ] Docker `restart: always`

### 모니터링

- [ ] AWS Budgets 알람 ($5, $10, $30)
- [ ] CloudWatch 알람 6개
- [ ] UptimeRobot
- [ ] Firebase Crashlytics
- [ ] Cloudflare Analytics

### 코드

- [ ] `ddl-auto: validate`
- [ ] 시크릿 환경변수 분리
- [ ] CORS 설정
- [ ] 로그 레벨 INFO
- [ ] Actuator health 엔드포인트
- [ ] Graceful shutdown

### 배포

- [ ] GitHub Actions 동작 확인
- [ ] 무중단 배포 테스트
- [ ] 롤백 절차 문서화

---

## ⚠️ 비용 폭탄 방지 - 절대 하지 말 것

| 실수 | 결과 | 방지법 |
|---|---|---|
| Elastic IP 미연결 | 시간당 $0.005 ($3.6/월) | 항상 EC2에 연결 |
| RDS Multi-AZ 켜기 | 비용 2배 | 초기엔 Single-AZ |
| EBS 큰 볼륨 | 월 $0.1/GB | 30GB 이내 |
| 로그 무한 보관 | 매월 누적 과금 | 7일 자동 삭제 |
| NAT Gateway 사용 | 월 $30+ | 안 씀 |
| 사용 안 하는 리소스 방치 | 계속 과금 | 매주 Billing 확인 |
| t3 unlimited 모드 | 버스트 시 과금 | t2 또는 t3 standard |
| 다른 리전에 리소스 생성 | 깜빡하고 방치 | 서울 리전만 사용 |

---

## 📊 한눈에 보는 요약

| 항목 | 값 |
|---|---|
| **1년차 월 비용** | $1 |
| **2년차 월 비용** | $23 |
| **총 구축 시간** | 약 3~4일 (주말 1~2회) |
| **사용 서비스** | Cloudflare + AWS + Firebase |
| **예상 DAU 수용** | 1,000 ~ 10,000 |
| **확장 한계** | DAU 5만 (EC2 업그레이드로 대응) |

---

## 🚀 다음 단계 (선택)

확장 시점에 추가 검토:

- **DAU 5,000+**: ALB 도입, RDS Multi-AZ 전환
- **DAU 10,000+**: ECS Fargate로 이전
- **앱 3개 이상**: 공통 인증 서버, 공유 인프라
- **앱 10개 이상**: GKE Autopilot 또는 EKS 검토

---

## 📝 핵심 요약

> **도메인($10) + Cloudflare(무료) + AWS 프리티어(무료) + Firebase(무료) = 1년 $12로 실서비스 출시**

핵심 포인트:
1. **Cloudflare가 게임 체인저** — CDN/SSL/WAF/DDoS를 무료로 → AWS 비용 절감 + 보안 강화
2. **AWS는 EC2 + RDS만** — ALB, CloudFront 다 Cloudflare가 대신
3. **Firebase가 모든 분석/알림 해결** — GA4 + Crashlytics + Push 무료
4. **2년차 월 $23** — Reserved 약정으로 최소화
