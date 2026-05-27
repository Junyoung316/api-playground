# URL Shortener

## 개요

긴 URL을 단축 코드로 변환하고, 단축 코드로 원본 URL로 리다이렉트하는 서비스입니다.

## 기술 스택

| 기술 | 용도 |
|---|---|
| MySQL | 단축 URL 정보 영구 저장 |
| Redis | 단축 코드 → 원본 URL 캐싱 (TTL 적용) |

## 저장소 설계

**MySQL**

| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | BIGINT | 기본키 |
| original_url | VARCHAR | 원본 URL |
| short_code | VARCHAR | 단축 코드 (unique) |
| view_count | BIGINT | 조회수 |
| created_at | DATETIME | 생성일 |
| expires_at | DATETIME | 만료일 |

**Redis**
```
key   : shortCode
value : originalUrl
TTL   : 설정값 기준 (기본 30일)
```

## API 명세

### 단축 URL 생성

```
POST /api/shorten
```

**Request Body**
```json
{
    "originalUrl": "https://www.naver.com"
}
```

**Response**
```json
{
    "code": "SUCCESS",
    "message": "성공",
    "data": {
        "shortCode": "TYT4CFsd"
    }
}
```

---

### 단축 URL 리다이렉트

```
GET /api/s/{code}
```

**Response**
```
HTTP/1.1 302 Found
Location: https://www.naver.com
```

---

### 단축 URL 통계 조회

```
GET /api/stats/{code}
```

**Response**
```json
{
    "code": "SUCCESS",
    "message": "성공",
    "data": {
        "shortCode": "TYT4CFsd",
        "originalUrl": "https://www.naver.com",
        "viewCount": 10,
        "createdAt": "2026-05-27T00:00:00"
    }
}
```

---

### 단축 URL 삭제

```
DELETE /api/shorten/{code}
```

**Response**
```json
{
    "code": "SUCCESS",
    "message": "성공"
}
```

## curl 테스트

```bash
# 단축 URL 생성
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"originalUrl": "https://www.naver.com"}'

# 리다이렉트 헤더 확인
curl -I http://localhost:8080/api/s/{code}

# 리다이렉트 따라가기
curl -L http://localhost:8080/api/s/{code}

# 통계 조회
curl http://localhost:8080/api/stats/{code}

# 삭제
curl -X DELETE http://localhost:8080/api/shorten/{code}
```

## 주요 구현 사항

- `SecureRandom`을 사용한 8자리 단축 코드 생성
- 단축 코드 중복 시 재생성 로직
- Redis TTL을 이용한 만료 처리
- 리다이렉트 요청 시 조회수 자동 증가