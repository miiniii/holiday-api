package com.mh.planit.holidayapi.config;

import com.mh.planit.holidayapi.client.HolidayApiClient;
import com.mh.planit.holidayapi.domain.Country;
import com.mh.planit.holidayapi.dto.HolidayRequest;
import com.mh.planit.holidayapi.dto.HolidayResponse;
import com.mh.planit.holidayapi.repository.CountryRepository;
import com.mh.planit.holidayapi.service.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class HolidayInitializer {

    private final HolidayApiClient holidayApiClient;
    private final HolidayService holidayService;
    private final CountryRepository countryRepository;


    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initHolidayData() {
        // 1. 국가 목록 조회
        List<Country> countries = countryRepository.findAll();

        // 2. List → Map<String, Country> 변환 (code → entity)
        Map<String, Country> countryMap = countries.stream()
                .collect(Collectors.toMap(Country::getCode, Function.identity()));

        // 3. HolidayService에 캐시 초기화
        holidayService.initCountryMap(countryMap);

        // 4. 연도별 공휴일 적재
        for (Country country : countries) {
            for (int year = 2020; year <= 2025; year++) {
                List<HolidayResponse> holidays = holidayApiClient.getHolidays(country.getCode(), year);

                for (HolidayResponse response : holidays) {
                    try {
                        HolidayRequest request = HolidayRequest.of(response, country.getCode());
                        holidayService.save(request);  // 저장
                    } catch (Exception e) {
                        log.error("저장 실패: {} - {}", response.getName(), e.getMessage());
                    }
                }
            }
        }

        log.info("Holiday 초기 데이터 적재 완료");
    }

}
