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
### N+1 문제 발생
#### **문제 상황**
- /holidays/init/data API를 통해 연도별 공휴일 데이터를 저장하는 과정에서 N+1 문제 발생
- 콘솔에 select country where code = ? 쿼리가 반복적으로 출력됨
```
Hibernate: select null,c1_0.name from country c1_0 where c1_0.code=?
Hibernate: insert into holiday (country_code,date,holiday_year,local_name,name,type,id) values (?,?,?,?,?,?,default)
Hibernate: select c1_0.code,c1_0.name from country c1_0 where c1_0.code=?
```
#### **원인**
- 국가 정보를 미리 캐싱한 Map<String, Country>에서 꺼내 사용했지만, 트랜잭션이 분리되어 있어 JPA 1차 캐시(Level 1 Cache)가 적용되지 않음
- 결국 같은 국가임에도 매번 DB에서 SELECT 발생

#### **해결 방법**
- @Transactional 어노테이션을 HolidayInitializer 메서드에 추가
```java
@Transactional
public void initHolidayData() {
    ...
}
```
- 트랜잭션 범위 안에서 JPA가 같은 식별자의 엔티티는 1차 캐시에서 재사용하게 되어 중복 쿼리 제거
  
#### **효과**
- DB 쿼리 횟수 감소 -> 반복적인 SELECT 쿼리 제거 

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
### QueryDSL 도입
#### **문제 상황**
- /holidays/search API에서 나라 코드, 연도, 월, 타입 등 다양한 조건으로 공휴일 검색이 필요
- 기존 방식으로는 조건별로 매번 Repository 메서드와 Service 로직, controller를 따로 작성해야 했음
```java
List<Holiday> findByCountryCodeAndYear(String code, int year);
List<Holiday> findByTypeAndMonth(String type, int month);
...
```
- 조건이 많아질수록 코드가 지저분하고 중복되어 유지보수 어려움 발생

#### **해결 방법**
- QueryDSL을 도입해 조건을 조립식으로 처리
- HolidayRepositoryCustom + HolidayRepositoryImpl 구현
```java
BooleanBuilder builder = new BooleanBuilder();
if (condition.getCountryCode() != null) {
    builder.and(holiday.country.code.eq(condition.getCountryCode()));
}
if (condition.getYear() != null) {
    builder.and(holiday.holidayYear.eq(condition.getYear()));
}
...
```

#### **효과**
- 동적 조건 쿼리를 하나의 메서드로 관리할 수 있어 유지보수가 쉬워짐
- 코드량 감소 및 중복 제거


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
