# Holiday API

공공 API를 활용해 국가별 연도별 공휴일 정보를 저장하고 조회할 수 있는 Spring Boot 기반 REST API입니다.

## 기술 스택

- Java 21
- Spring Boot 3.4.4
- JPA (Hibernate)
- H2
- QueryDSL
- Spring Scheduler
- Swagger
- Gradle



## 실행 방법
```bash
# 1. GitHub에서 프로젝트 클론
git clone https://github.com/your-username/holiday-api.git
cd holiday-api

# 2. 애플리케이션 실행 (로컬 환경 기준)
./gradlew bootRun
```
- 실행 후 Swagger UI 주소로 접속하면 API 명세 확인 가능 : http://localhost:8080/swagger-ui/index.html

## REST API 명세 요약

### 1. 국가 및 공휴일 초기 데이터 적재
- **URL** `/holidays/init/data`
- **Method**: `POST`
- **설명**: 외부 API를 통해 국가 목록 및 연도별 공휴일 데이터를 2020~2025년까지 일괄 저장합니다.
- **응답 예시**
```json
"전체 초기화 완료"
```

### 2. 공휴일 조건 검색
- **URL** `/holidays/init/`
- **Method**: `GET`
- **설명**: 나라 코드, 연도, 월, 타입, 날짜 범위 등의 조건으로 공휴일을 검색합니다.
- **Query Parameters**
  | 파라미터     | 타입     | 설명                       | 필수 여부 |
  |------------|--------|--------------------------|--------|
  | countryCode | String | 국가 코드 (예: `KR`)         | 선택    |
  | year        | int    | 연도 (예: `2025`)           | 선택    |
  | month       | int    | 월 (예: `1`)               | 선택    |
  | type        | String | 공휴일 타입 (예: `Public`)   | 선택    |
  | startDate   | String | 시작일 (예: `2025-01-01`)   | 선택    |
  | endDate     | String | 종료일 (예: `2025-12-31`)   | 선택    |
- **응답 예시**
```json
{
  "content": [
    {
      "name": "크리스마스",
      "localName": "성탄절",
      "holidayDate": "2025-12-25",
      "type": "Public",
      "country": {
        "code": "KR",
        "name": "South Korea"
      }
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

### 3. 공휴일 재동기화 (Refresh)

- **URL**: `/holidays/refresh`
- **Method**: `PUT`
- **설명**: 해당 국가와 연도에 대해 외부 API에서 공휴일 정보를 재조회하고, DB에 덮어쓰기(Upsert)합니다.
- **Query Parameters**

  | 파라미터     | 타입     | 설명                         | 필수 여부 |
  |--------------|----------|------------------------------|-----------|
  | countryCode  | String   | 국가 코드 (예: `KR`)         | ✔️        |
  | year         | int      | 연도 (예: `2025`)            | ✔️        |


- **응답 예시**
  ```json
  "공휴일이 성공적으로 재동기화되었습니다."
  ```

### 4. 공휴일 삭제

- **URL**: `/holidays/delete`
- **Method**: `DELETE`
- **설명**: 해당 국가와 연도에 대해 외부 API에서 공휴일 정보를 재조회하고, DB에 덮어쓰기(Upsert)합니다.
- **Query Parameters**

  | 파라미터     | 타입     | 설명                         | 필수 여부 |
  |--------------|----------|------------------------------|-----------|
  | countryCode  | String   | 국가 코드 (예: `KR`)         | ✔️        |
  | year         | int      | 연도 (예: `2025`)            | ✔️        |

- **응답 예시**
```json
"공휴일이 성공적으로 삭제되었습니다. [KR - 2025]"
```


### 4-1. 수동 배치 실행

- **URL**: `/holidays/run`
- **Method**: `POST`
- **설명**: 배치 로직을 수동으로 실행하며, 로컬 개발 환경이나 테스트 환경에서 확인할 때 유용합니다.
- **응답 예시**
```json
"배치 실행 완료"
```

### 4-2. 자동 배치 실행
- **스케줄**: `매년 1월 2일 오전 1시 (KST)`
- **설명**: 전년도 및 금년도에 해당하는 공휴일 데이터를 자동으로 동기화합니다.
- **예시 로그**
```
[배치] 2025년도 공휴일 재동기화 시작 (국가: KR)
[배치] 전년도·금년도 공휴일 재동기화 완료
```
