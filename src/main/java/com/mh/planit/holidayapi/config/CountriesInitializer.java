package com.mh.planit.holidayapi.config;

import com.mh.planit.holidayapi.client.CountryApiClient;
import com.mh.planit.holidayapi.dto.CountryResponse;
import com.mh.planit.holidayapi.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CountriesInitializer {

    private final CountryApiClient countryApiClient;
    private final CountryService countryService;

    public void initCountries() {
        List<CountryResponse> countryList = countryApiClient.getAvailableCountries();
        countryService.upsertCountries(countryList);
        log.info("국가 리스트 초기화 완료: " + countryList.size() + "개");
    }
}