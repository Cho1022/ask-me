# Ask Me Voice Kiosk

터치와 음성을 함께 지원하는 카페 주문 키오스크입니다. 기존 Python·Flask 프로토타입의 기능 요구사항과 회귀 사례만 참고해 Vue 3, TypeScript, Spring Boot, MySQL로 새로 구축했습니다.

## 현재 아키텍처

![Ask Me Voice Kiosk 현재 아키텍처](docs/architecture/current-architecture.png)

현재 구성은 AWS에 배포된 구조가 아니라 Docker Compose에서 실행되는 3개 컨테이너 구조입니다.

- 브라우저는 Nginx가 제공하는 Vue 3 정적 파일을 내려받고 터치 입력과 최대 20초 음성 녹음을 처리합니다.
- Nginx는 `/api` 요청을 Spring Boot 백엔드의 `8080` 포트로 프록시합니다.
- Spring Boot는 메뉴·옵션 검증, Java 주문 문장 파싱, 서버 가격 재계산과 주문 트랜잭션을 담당합니다.
- MySQL은 메뉴, 별칭, 옵션, 주문과 주문 시점의 가격 스냅샷을 저장합니다.
- Google Cloud Speech-to-Text는 음성 기능이 활성화된 경우에만 호출되며 인식된 텍스트는 주문 파서로 전달됩니다.

> 다이어그램의 AWS 서비스 표기는 [AWS Architecture Icons 2026 Q2](https://aws.amazon.com/architecture/icons/)를, GitHub·Docker·Vue·TypeScript·Java·Spring Boot·MySQL·Google Cloud 등의 제품 로고는 [Devicon](https://devicon.dev/) SVG를 사용합니다. 아이콘은 사용 기술 또는 배포 목표를 나타내며, EC2·ECS·RDS 등 AWS 리소스가 현재 배포되어 있다는 의미는 아닙니다.

## AWS 배포 목표 구조

![Ask Me Voice Kiosk AWS 배포 목표 구조](docs/architecture/aws-deployment-reference.png)

참고 이미지처럼 실제 AWS 서비스 아이콘, Cloud·Region·VPC·subnet 경계를 사용해 앞으로 배포할 구조를 정리했습니다. CI/CD와 각 서비스 안에는 실제 기술 아이콘을 함께 배치했습니다.

- GitHub → GitHub Actions → Docker 파이프라인이 Vue·TypeScript·Vite 정적 결과물을 Amazon S3에 배포하고, Spring Boot 이미지를 Amazon ECR에 푸시합니다.
- CloudFront는 Vue 정적 파일을 제공하고, ALB는 `/api` 요청을 ECS Fargate의 Spring Boot 컨테이너로 전달합니다.
- MySQL은 Amazon RDS for MySQL에 저장하고, Secrets Manager로 DB와 외부 API 자격 증명을 관리합니다.
- Google Speech-to-Text V1 호출은 private subnet의 ECS 서비스에서 NAT Gateway를 통한 아웃바운드 HTTPS 통신으로 유지합니다.

## 핵심 흐름

```text
Vue 3 + TypeScript
  ├─ 터치 메뉴 선택
  └─ 20초 음성 녹음
          ↓
Spring Boot REST API
  ├─ Google Speech-to-Text V1
  ├─ Java 주문 문장 파서
  ├─ 메뉴·옵션 검증
  └─ 서버 가격 재계산 + 트랜잭션
          ↓
MySQL
```

음성 인식 결과는 주문으로 바로 저장되지 않습니다. 인식된 텍스트를 주문 초안으로 변환하고 사용자가 장바구니와 결제 수단을 확인한 뒤 `POST /api/orders`를 호출합니다.

## 기술 스택

- Frontend: Vue 3, TypeScript, Vite, Pinia, Vue Router, Vitest
- Backend: Java 21, Spring Boot 4.1, Spring Web MVC, Spring Data JPA, Bean Validation, Flyway
- Database: MySQL 8.4
- Voice: Google Cloud Speech-to-Text V1, 20초 동기 인식, `webm/opus`
- Test: JUnit 5, Testcontainers MySQL, Vitest

## 빠른 실행

사전 조건은 Docker Desktop입니다.

```powershell
docker compose up --build
```

- 키오스크: `http://localhost:5173`
- 백엔드 헬스체크: `http://localhost:8080/api/health`
- MySQL: `localhost:3306`

기본 설정에서는 Google STT가 비활성화됩니다. 음성 모달의 텍스트 입력으로 주문 파서를 독립적으로 확인할 수 있습니다.

## 개발 모드

MySQL만 실행합니다.

```powershell
docker compose up mysql -d
```

백엔드:

```powershell
cd backend
.\gradlew.bat bootRun
```

프론트엔드:

```powershell
cd frontend
npm install
npm run dev
```

Vite가 `/api` 요청을 `http://localhost:8080`으로 전달합니다.

## Google Speech-to-Text 설정

1. Google Cloud 프로젝트에서 결제와 Speech-to-Text API를 활성화합니다.
2. Speech-to-Text 호출 권한이 있는 서비스 계정 인증 파일을 준비합니다.
3. `.env.example`을 참고해 `.env`를 만들고 인증 파일 경로를 설정합니다.

```dotenv
GOOGLE_STT_ENABLED=true
GOOGLE_CREDENTIALS_FILE=C:/absolute/path/google-credentials.json
```

STT Compose 오버레이와 함께 실행합니다.

```powershell
docker compose -f docker-compose.yml -f docker-compose.stt.yml up --build
```

인증 파일은 컨테이너의 `/run/secrets/google-credentials.json`에 읽기 전용으로 연결되며 프론트엔드에 노출되지 않습니다.

Google STT V1은 데이터 로깅을 사용하지 않아도 계정당 월 60분 무료 구간이 있지만 무제한 무료 서비스가 아니며 결제 계정이 필요합니다. 이 프로젝트는 Google Cloud 프로젝트에서 데이터 로깅을 별도로 활성화하지 않는 구성을 전제로 합니다. 운영에서는 Cloud 예산 알림과 애플리케이션 사용량 제한을 별도로 설정해야 합니다. 기준은 [Google Cloud 공식 가격표](https://cloud.google.com/speech-to-text/pricing)와 [데이터 사용 FAQ](https://cloud.google.com/speech-to-text/docs/v1/data-usage-faq)에서 확인할 수 있습니다.

## 주요 API

| Method | Endpoint | 역할 |
|---|---|---|
| `GET` | `/api/health` | 서버 상태 확인 |
| `GET` | `/api/menus` | 판매 중인 메뉴와 옵션 조회 |
| `POST` | `/api/voice/transcriptions` | 최대 20초 음성을 텍스트로 변환 |
| `POST` | `/api/voice/orders/parse` | 텍스트를 주문 초안으로 변환 |
| `POST` | `/api/orders` | 확인된 주문의 가격을 재계산하고 저장 |

## 검증

프론트엔드:

```powershell
cd frontend
npm run test:run
npm run build
```

백엔드 테스트는 Docker Desktop이 실행 중이어야 합니다.

```powershell
.\scripts\test-backend.ps1
cd backend
.\gradlew.bat build -x test
```

테스트 스크립트는 Windows의 한글 경로에서 Gradle 테스트 워커가 클래스를 찾지 못하는 문제를 피하기 위해 백엔드 폴더에 빈 드라이브 문자를 잠시 연결하고, 종료 시 항상 해제합니다. 영문 경로와 CI에서는 `backend/gradlew test`를 직접 실행해도 됩니다.

전체 Compose 구성 확인:

```powershell
docker compose config
```

## 현재 범위

구현된 범위:

- 메뉴 조회·검색·카테고리 필터
- 사이즈 및 메뉴 옵션
- 장바구니 수량 관리
- 매장 식사·포장 선택
- 카드·카카오페이·PAYCO 선택 데모
- 한국어 메뉴 별칭·수량·사이즈·옵션·취소·정정 파싱
- 서버 가격 재계산과 주문 스냅샷 저장
- Google STT V1 동기 인식 어댑터

초기 범위에서 제외한 항목:

- 실제 PG 결제
- 실시간 스트리밍 자막과 WebSocket
- 관리자 페이지와 회원 기능
- LLM 주문 분석
- 쿠폰·포인트·재고 시스템
