package com.mh.planit.holidayapi.config;

import com.mh.planit.holidayapi.client.CountryApiClient;
import com.mh.planit.holidayapi.dto.CountryResponse;
import com.mh.planit.holidayapi.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final CountryApiClient countryApiClient;
    private final CountryService countryService;

    public static List<CountryResponse> countryList; // 전역에서 접근 가능하게 할 경우

    @EventListener(ApplicationReadyEvent.class)
    public void initAfterStartup() {
        List<CountryResponse> countryList = countryApiClient.getAvailableCountries();
        countryService.upsertCountries(countryList);
        log.info("국가 리스트 초기화 완료: " + countryList.size() + "개");
    }
}