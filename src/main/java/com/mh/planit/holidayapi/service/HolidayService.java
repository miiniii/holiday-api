package com.mh.planit.holidayapi.service;

import com.mh.planit.holidayapi.domain.Country;
import com.mh.planit.holidayapi.domain.Holiday;
import com.mh.planit.holidayapi.dto.HolidayRequest;
import com.mh.planit.holidayapi.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                .year(date.getYear())
                .month(date.getMonthValue())
                .day(date.getDayOfMonth())
                .country(country)
                .build();


        holidayRepository.save(holiday);
    }

}
