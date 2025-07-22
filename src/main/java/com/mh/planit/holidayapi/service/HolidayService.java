package com.mh.planit.holidayapi.service;

import com.mh.planit.holidayapi.client.HolidayApiClient;
import com.mh.planit.holidayapi.domain.Country;
import com.mh.planit.holidayapi.domain.Holiday;
import com.mh.planit.holidayapi.dto.HolidayRequest;
import com.mh.planit.holidayapi.dto.HolidaySearchCondition;
import com.mh.planit.holidayapi.repository.HolidayRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final CountryService countryService;
    private final HolidayApiClient holidayApiClient;

    // 영속 상태의 Country 객체들을 캐시
    private Map<String, Country> countryMap = new HashMap<>();

    //캐시 초기화
    public void initCountryMap(Map<String, Country> countryMap) {
        this.countryMap.clear();
        this.countryMap.putAll(countryMap);
    }

    public void save(HolidayRequest request) {
        Country country = countryMap.get(request.getCountryCode());

        if (country == null) {
            // 영속 상태의 Country가 없는 경우는 저장하지 않고 로그만
            // (정상적으로 초기화되었으면 이 경우는 거의 없음)
            log.warn("존재하지 않는 국가 코드: {}", request.getCountryCode());
            return; // 저장 안 함
        }

        String type = request.getType() != null ? request.getType() : "UNKNOWN"; // 기본값 지정

        LocalDate date = LocalDate.parse(request.getDate());

        Holiday holiday = Holiday.builder()
                .name(request.getName())
                .localName(request.getLocalName())
                .date(date)
                .type(type)
                .holidayYear(date.getYear())
                .holidayMonth(date.getMonthValue())
                .holidayday(date.getDayOfMonth())
                .country(country)
                .build();


        holidayRepository.save(holiday);
    }

    public Page<Holiday> searchHolidays(HolidaySearchCondition condition, Pageable pageable) {
        return holidayRepository.search(condition, pageable);
    }

    @Transactional
    public void refreshHolidays(String countryCode, int year) {
        // 1. 기존 데이터 삭제
        holidayRepository.deleteByCountry_CodeAndHolidayYear(countryCode, year);

        // 2. 외부 API에서 데이터 조회
        List<Holiday> newHolidays = holidayApiClient.fetchHolidays(countryCode, year);

        // 3. CountryService 통해 조회
        Country country = countryService.getByCode(countryCode);

        // 4. Holiday 객체에 country 설정 후 저장
        for (Holiday holiday : newHolidays) {
            holiday.setCountry(country);  // 연관관계 설정
        }
        holidayRepository.saveAll(newHolidays);
    }


}
