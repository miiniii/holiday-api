package com.mh.planit.holidayapi.controller;


import com.mh.planit.holidayapi.dto.CountryResponse;
import com.mh.planit.holidayapi.dto.HolidayResponse;
import com.mh.planit.holidayapi.client.HolidayApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestApiController {

    private final HolidayApiClient holidayApiClient;

    @GetMapping("/countries")
    public List<CountryResponse> getAllCountries() {
        List<CountryResponse> countries = holidayApiClient.getAvailableCountries();
        countries.forEach(c -> System.out.println(c.getCountryCode() + " - " + c.getName()));
        return countries;
    }

    @GetMapping("/holidays")
    public List<HolidayResponse> getHolidaysByYearAndCountry(
            @RequestParam int year,
            @RequestParam String country
    ) {
        List<HolidayResponse> holidays = holidayApiClient.getHolidaysByYearAndCountry(year, country);
        holidays.forEach(h -> System.out.println(h.getDate() + " - " + h.getName()));
        return holidays;
    }
}
