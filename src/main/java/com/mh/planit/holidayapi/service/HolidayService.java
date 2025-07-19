package com.mh.planit.holidayapi.service;

import com.mh.planit.holidayapi.client.HolidayApiClient;
import com.mh.planit.holidayapi.domain.Country;
import com.mh.planit.holidayapi.domain.Holiday;
import com.mh.planit.holidayapi.dto.CountryResponse;
import com.mh.planit.holidayapi.dto.HolidayResponse;
import com.mh.planit.holidayapi.repository.CountryRepository;
import com.mh.planit.holidayapi.repository.HolidayRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayApiClient holidayApiClient;
    private final CountryRepository countryRepository;
    private final HolidayRepository holidayRepository;

    public List<CountryResponse> getCountriesOnly() {
        return holidayApiClient.getAvailableCountries();
    }

    public List<HolidayResponse> getHolidaysOnly(int year, String countryCode) {
        return holidayApiClient.getHolidaysByYearAndCountry(year, countryCode);
    }


    /**
     * 1. 외부 API로부터 전체 국가 목록 받아 저장
     */
    @Transactional
    public void loadAllCountries() {
        List<CountryResponse> countries = holidayApiClient.getAvailableCountries();

        for (CountryResponse country : countries) {
            if (!countryRepository.existsById(country.getCountryCode())) {
                countryRepository.save(new Country(country.getCountryCode(), country.getName()));
            }
        }
    }


    /**
     * 2. 특정 연도, 특정 국가의 공휴일 받아 저장
     */
    @Transactional
    public void loadHolidays(int year, String countryCode) {
        Country country = countryRepository.findById(countryCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 국가 코드: " + countryCode));

        List<HolidayResponse> holidays = holidayApiClient.getHolidaysByYearAndCountry(year, countryCode);

        for (HolidayResponse response : holidays) {
            // 중복 체크는 생략하거나 key 제약 조건으로도 가능
            Holiday holiday = Holiday.builder()
                    .date(response.getDate()) // yyyy-MM-dd
                    .name(response.getName())
                    .localName(response.getLocalName())
                    .type(getFirstType(response))
                    .holidayYear(LocalDate.parse(response.getDate()).getYear())
                    .country(country)
                    .build();

            holidayRepository.save(holiday);
        }
    }

    private String getFirstType(HolidayResponse response) {
        List<String> types = response.getTypes();
        return (types != null && !types.isEmpty()) ? types.get(0) : null;
    }
}