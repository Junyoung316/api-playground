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

<hr>

# Lotto

## 개요

1~45 사이의 숫자 중 중복 없이 6개를 랜덤으로 추출하는 로또 번호 생성 서비스입니다.
생성된 번호는 데이터베이스에 저장되며 이력 조회를 지원합니다.

## 기술 스택

| 기술 | 용도 |
|---|---|
| MySQL | 로또 번호 생성 이력 영구 저장 |

## 저장소 설계

**MySQL**

| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | BIGINT | 기본키 |
| numbers | VARCHAR | 로또 번호 (문자열로 저장, 예: "3,7,15,23,38,41") |
| created_at | DATETIME | 생성일 |

## 주요 구현 사항

- `Collections.shuffle()`을 이용한 랜덤 번호 추출
- `@Converter`를 이용한 `List<Integer>` ↔ 문자열 변환
- Spring Data JPA Pageable을 이용한 페이징 처리

### IntegerListConverter

`List<Integer>`를 MySQL에 문자열로 저장하고 조회 시 다시 `List<Integer>`로 변환합니다.

```
저장 시 : [3, 7, 15, 23, 38, 41] → "3,7,15,23,38,41"
조회 시 : "3,7,15,23,38,41" → [3, 7, 15, 23, 38, 41]
```

## API 명세

### 로또 번호 생성

```
POST /api/lotto/generate
```

**Response**
```json
{
    "code": "SUCCESS",
    "message": "성공",
    "data": {
        "numbers": [3, 7, 15, 23, 38, 41]
    }
}
```

---

### 로또 번호 생성 이력 조회

```
GET /api/lotto/history?page=0&size=10
```

**Query Parameter**

| 파라미터 | 필수 여부 | 설명 |
|---|---|---|
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
                "numbers": [3, 7, 15, 23, 38, 41],
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

## curl 테스트

```bash
# 로또 번호 생성
curl -X POST http://localhost:8080/api/lotto/generate

# 이력 조회
curl http://localhost:8080/api/lotto/history

# 이력 조회 (페이징)
curl "http://localhost:8080/api/lotto/history?page=0&size=10"
```

<hr>

# BMI Calculator

## 개요

키와 몸무게를 입력받아 BMI를 계산하고 비만 단계를 반환하는 서비스입니다.
계산 결과는 데이터베이스에 저장되며 이력 조회를 지원합니다.

## 기술 스택

| 기술 | 용도 |
|---|---|
| MySQL | BMI 계산 이력 영구 저장 |

## 저장소 설계

**MySQL**

| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | BIGINT | 기본키 |
| height | DOUBLE | 키 (cm) |
| weight | DOUBLE | 몸무게 (kg) |
| bmi | DOUBLE | BMI 수치 |
| status | VARCHAR | 비만 단계 (UNDERWEIGHT, NORMAL, OVERWEIGHT, OBESE) |
| created_at | DATETIME | 생성일 |

## BMI 계산 공식

```
BMI = 체중(kg) / (키(m) * 키(m))
```

## 비만 단계

| 단계 | BMI 범위 | 설명 |
|---|---|---|
| 저체중 | BMI < 18.5 | UNDERWEIGHT |
| 정상 | 18.5 <= BMI < 23 | NORMAL |
| 과체중 | 23 <= BMI < 25 | OVERWEIGHT |
| 비만 | BMI >= 25 | OBESE |

## API 명세

### BMI 계산

```
POST /api/bmi/calculate
```

**Request Body**
```json
{
    "height": 175.0,
    "weight": 70.0
}
```

**Response**
```json
{
    "code": "SUCCESS",
    "message": "성공",
    "data": {
        "bmi": 22.86,
        "status": "정상"
    }
}
```

---

### BMI 이력 조회

```
GET /api/bmi/history?page=0&size=10
```

**Query Parameter**

| 파라미터 | 필수 여부 | 설명 |
|---|---|---|
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
                "height": 175.0,
                "weight": 70.0,
                "bmi": 22.86,
                "status": "정상",
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

## curl 테스트

```bash
# BMI 계산
curl -X POST http://localhost:8080/api/bmi/calculate \
  -H "Content-Type: application/json" \
  -d '{"height": 175.0, "weight": 70.0}'

# 저체중 케이스
curl -X POST http://localhost:8080/api/bmi/calculate \
  -H "Content-Type: application/json" \
  -d '{"height": 175.0, "weight": 50.0}'

# 과체중 케이스
curl -X POST http://localhost:8080/api/bmi/calculate \
  -H "Content-Type: application/json" \
  -d '{"height": 175.0, "weight": 80.0}'

# 비만 케이스
curl -X POST http://localhost:8080/api/bmi/calculate \
  -H "Content-Type: application/json" \
  -d '{"height": 175.0, "weight": 100.0}'

# 이력 조회
curl http://localhost:8080/api/bmi/history

# 이력 조회 (페이징)
curl "http://localhost:8080/api/bmi/history?page=0&size=10"
```

## 주요 구현 사항

- BMI 계산 공식을 이용한 수치 계산
- `BmiStatus` enum의 정적 팩토리 메서드 `from()`을 이용한 비만 단계 판별
- enum에 한글 description 필드를 추가해 사용자 친화적인 응답 반환
- `@Enumerated(EnumType.STRING)`을 이용한 enum 문자열 저장
- Spring Data JPA Pageable을 이용한 페이징 처리