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
dd
<hr>

# Memo

## 개요

메모를 생성, 조회, 수정, 삭제할 수 있는 CRUD 서비스입니다.
키워드 검색과 페이징을 지원합니다.

## 기술 스택

| 기술 | 용도 |
|---|---|
| MySQL | 메모 데이터 영구 저장 |

## 저장소 설계

**MySQL**

| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | BIGINT | 기본키 |
| title | VARCHAR | 제목 |
| content | VARCHAR | 내용 |
| author | VARCHAR | 작성자 |
| created_at | DATETIME | 생성일 |
| updated_at | DATETIME | 수정일 |

## API 명세

### 메모 생성

```
POST /api/memos
```

**Request Body**
```json
{
    "title": "testTitle",
    "content": "testContent",
    "author": "test"
}
```

**Response**
```json
{
    "code": "SUCCESS",
    "message": "성공"
}
```

---

### 메모 목록 조회

```
GET /api/memos?keyword=xxx&page=0&size=10
```

**Query Parameter**

| 파라미터 | 필수 여부 | 설명 |
|---|---|---|
| keyword | 선택 | 제목 검색 키워드 |
| page | 선택 | 페이지 번호 (기본값 0) |
| size | 선택 | 페이지 크기 (기본값 10) |

**Response**
```json
{
    "code": "SUCCESS",
    "message": "성공",
    "data": {
        "content": [
            {
                "id": 1,
                "title": "testTitle",
                "author": "test",
                "createdAt": "2026-05-27T00:00:00"
            }
        ],
        "totalElements": 1,
        "totalPages": 1,
        "size": 10,
        "number": 0
    }
}
```

---

### 메모 단건 조회

```
GET /api/memos/{id}
```

**Response**
```json
{
    "code": "SUCCESS",
    "message": "성공",
    "data": {
        "id": 1,
        "title": "testTitle",
        "content": "testContent",
        "author": "test",
        "createdAt": "2026-05-27T00:00:00",
        "updatedAt": "2026-05-27T00:00:00"
    }
}
```

---

### 메모 전체 수정

```
PUT /api/memos/{id}
```

**Request Body**
```json
{
    "title": "newTitle",
    "content": "newContent"
}
```

**Response**
```json
{
    "code": "SUCCESS",
    "message": "성공"
}
```

---

### 메모 부분 수정

```
PATCH /api/memos/{id}
```

**Request Body** (변경할 필드만 포함)
```json
{
    "title": "patchTitle"
}
```

**Response**
```json
{
    "code": "SUCCESS",
    "message": "성공"
}
```

---

### 메모 삭제

```
DELETE /api/memos/{id}
```

**Response**
```json
{
    "code": "SUCCESS",
    "message": "성공"
}
```

---

## curl 테스트

```bash
# 메모 생성
curl -X POST http://localhost:8080/api/memos \
  -H "Content-Type: application/json" \
  -d '{"title": "testTitle", "content": "testContent", "author": "test"}'

# 메모 목록 조회
curl http://localhost:8080/api/memos

# 메모 목록 조회 (페이징)
curl "http://localhost:8080/api/memos?page=0&size=10"

# 메모 검색
curl "http://localhost:8080/api/memos?keyword=testTitle"

# 메모 단건 조회
curl http://localhost:8080/api/memos/1

# 메모 전체 수정
curl -X PUT http://localhost:8080/api/memos/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "newTitle", "content": "newContent"}'

# 메모 부분 수정
curl -X PATCH http://localhost:8080/api/memos/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "patchTitle"}'

# 메모 삭제
curl -X DELETE http://localhost:8080/api/memos/1

# 존재하지 않는 메모 조회 (예외 처리 확인)
curl http://localhost:8080/api/memos/9999
```

## 주요 구현 사항

- JPA Auditing을 이용한 생성일, 수정일 자동 관리
- `findByTitleContaining`을 이용한 키워드 검색
- PUT / PATCH 메서드 분리로 전체 수정 / 부분 수정 구현
- 더티 체킹을 이용한 수정 처리 (save() 호출 불필요)
- Spring Data JPA Pageable을 이용한 페이징 처리