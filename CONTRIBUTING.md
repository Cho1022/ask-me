# Contributing

## 작업 전 확인

```powershell
git branch --show-current
git status --short
```

기능 브랜치는 최신 `main`에서 생성하고 기존 작업자의 변경을 되돌리지 않습니다.

## 로컬 검증

```powershell
cd frontend
npm run test:run
npm run build
```

```powershell
.\scripts\test-backend.ps1
cd backend
.\gradlew.bat build -x test
```

백엔드 통합 테스트는 Testcontainers MySQL을 사용하므로 Docker Desktop이 필요합니다. 테스트 스크립트는 Windows 한글 경로의 Gradle 테스트 워커 문제를 피하기 위해 임시 드라이브 문자를 사용하고 자동으로 해제합니다.

## 변경 원칙

- API DTO와 DB 엔티티를 분리합니다.
- 주문 가격은 클라이언트 값을 신뢰하지 않고 서버에서 계산합니다.
- Flyway 이외의 방법으로 운영 스키마를 변경하지 않습니다.
- Google 인증 정보와 `.env`를 커밋하지 않습니다.
- 음성 인식, 주문 파싱, 주문 저장을 독립적으로 테스트할 수 있게 유지합니다.
