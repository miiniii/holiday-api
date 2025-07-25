package com.mh.planit.holidayapi.controller;


import com.mh.planit.holidayapi.batch.HolidayBatchService;
import com.mh.planit.holidayapi.config.CountriesInitializer;
import com.mh.planit.holidayapi.config.HolidayInitializer;
import com.mh.planit.holidayapi.domain.Holiday;
import com.mh.planit.holidayapi.dto.HolidaySearchCondition;
import com.mh.planit.holidayapi.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/holidays")
public class HolidayController {

    private final HolidayInitializer holidayInitializer;
    private final CountriesInitializer countriesInitializer;
    private final HolidayService holidayService;
    private final HolidayBatchService holidayBatchService;


    // 공휴일 일괄 적재하는 API
    @PostMapping("/init/data")
    @Operation(summary = "공휴일 일괄 적재", description = "외부 API를 통해 국가 목록과 연도별 공휴일 데이터를 수집하고 저장합니다.")
    public ResponseEntity<String> initAll() {
        countriesInitializer.initCountries();
        holidayInitializer.initHolidayData();
        return ResponseEntity.ok("전체 초기화 완료");
    }


    // 공휴일 목록 조건 검색
    @GetMapping("/search")
    @Operation(summary = "공휴일 검색", description = "나라 코드, 연도, 월, 타입, 날짜 범위 등의 조건으로 공휴일을 검색합니다.")
    public Page<Holiday> searchHolidays(HolidaySearchCondition condition, Pageable pageable) {
        return holidayService.searchHolidays(condition, pageable);
    }

    @PutMapping("/refresh")
    @Operation(summary = "공휴일 재동기화", description = "특정 연도와 국가 코드 기준으로 외부 API를 통해 공휴일을 다시 불러와 DB에 반영합니다.")
    public ResponseEntity<String> refreshHolidays(@RequestParam String countryCode,
                                                  @RequestParam int year) {
        holidayService.refreshHolidays(countryCode, year);
        return ResponseEntity.ok("공휴일이 성공적으로 재동기화되었습니다.");
    }

    @DeleteMapping("/delete")
    @Operation(summary = "공휴일 삭제", description = "특정 연도 및 국가의 공휴일 데이터를 삭제합니다.")
    public ResponseEntity<String> deleteHolidays(
            @RequestParam String countryCode,
            @RequestParam int year) {
        holidayService.deleteHolidays(countryCode, year);
        return ResponseEntity.ok("공휴일이 성공적으로 삭제되었습니다. [" + countryCode + " - " + year + "]");
    }


    @PostMapping("/run")
    @Operation(summary = "공휴일 배치 수동 실행", description = "전년도와 올해의 공휴일 데이터를 외부 API로부터 재동기화합니다.")
    public ResponseEntity<String> runBatch() {
        holidayBatchService.refreshHolidayBatch();
        return ResponseEntity.ok("배치 실행 완료");
    }

}
