package com.mh.planit.holidayapi.client;

import com.mh.planit.holidayapi.dto.CountryResponse;
import com.mh.planit.holidayapi.dto.HolidayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Component
@RequiredArgsConstructor
public class HolidayApiClient {

    private final WebClient webClient;

    private static final String BASE_URL = "https://date.nager.at/api/v3";

    // 1. 국가 목록 조회
    public List<CountryResponse> getAvailableCountries() {
        return webClient.get()
                .uri(BASE_URL + "/AvailableCountries")
                .retrieve()
                .bodyToFlux(CountryResponse.class)
                .collectList()
                .block();  // 동기 처리
    }

    // 2. 특정 연도·국가 공휴일 조회
    public List<HolidayResponse> getHolidaysByYearAndCountry(int year, String countryCode) {
        return webClient.get()
                .uri(BASE_URL + "/PublicHolidays/{year}/{countryCode}", year, countryCode)
                .retrieve()
                .bodyToFlux(HolidayResponse.class)
                .collectList()
                .block();  // 동기 처리
    }
}