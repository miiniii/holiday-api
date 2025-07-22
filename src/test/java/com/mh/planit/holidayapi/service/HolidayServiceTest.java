package com.mh.planit.holidayapi.service;

import com.mh.planit.holidayapi.client.HolidayApiClient;
import com.mh.planit.holidayapi.domain.Country;
import com.mh.planit.holidayapi.domain.Holiday;
import com.mh.planit.holidayapi.repository.HolidayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    @InjectMocks
    private HolidayService holidayService;

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private HolidayApiClient holidayApiClient;

    @Mock
    private CountryService countryService;

    @Test
    void refreshHolidays_shouldDeleteAndSaveNewData() {
        // given
        String countryCode = "KR";
        int year = 2025;

        Country country = Country.builder()
                .code(countryCode)
                .name("South Korea")
                .build();

        List<Holiday> holidays = List.of(
                Holiday.builder()
                        .name("Test Day 1")
                        .country(country)
                        .date(LocalDate.of(2025, 1, 1))
                        .build(),
                Holiday.builder()
                        .name("Test Day 2")
                        .country(country)
                        .date(LocalDate.of(2025, 12, 25))
                        .build()
        );

        // mocking
        when(holidayApiClient.fetchHolidays(countryCode, year)).thenReturn(holidays);
        when(countryService.getByCode(countryCode)).thenReturn(country);

        // when
        holidayService.refreshHolidays(countryCode, year);

        // then
        verify(holidayRepository).deleteByCountry_CodeAndHolidayYear(countryCode, year);
        verify(holidayApiClient).fetchHolidays(countryCode, year);
        verify(countryService).getByCode(countryCode);
        verify(holidayRepository).saveAll(holidays);

        // country가 holidays에 설정되었는지도 확인
        for (Holiday holiday : holidays) {
            assertEquals(country, holiday.getCountry());
        }
    }

    @Test
    void deleteHolidays_shouldDeleteByCountryCodeAndYear() {
        // given
        String countryCode = "KR";
        int year = 2025;

        // when
        holidayService.deleteHolidays(countryCode, year);

        // then
        verify(holidayRepository).deleteByCountry_CodeAndHolidayYear(countryCode, year);
    }


}
