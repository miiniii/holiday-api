package com.mh.planit.holidayapi.client;

import com.mh.planit.holidayapi.dto.HolidayResponse;
//import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HolidayApiClient {

    @Qualifier("holidayWebClient")
    private final WebClient webClient;

    public List<HolidayResponse> getHolidays(String countryCode, int year) {
        try {
            return webClient.get()
                    .uri("/api/v3/PublicHolidays/{year}/{countryCode}", year, countryCode)
                    .retrieve()
                    .bodyToFlux(HolidayResponse.class)
                    .collectList()
                    .block(); // 비동기 처리 권장, 초기 적재 시만 block 사용
        } catch (Exception e) {
            log.error("공휴일 API 호출 실패: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

}
