package com.mh.planit.holidayapi.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class HolidayRequest {

    private String name;

    private String localName;

    private String date;

    private String type;

    private int holidayYear;

    private String countryCode;

    public static HolidayRequest of(HolidayResponse response, String countryCode) {
        return HolidayRequest.builder()
                .name(response.getName())
                .localName(response.getLocalName())
                .date(response.getDate())
                .type(
                        (response.getTypes() != null && !response.getTypes().isEmpty())
                                ? String.join(",", response.getTypes())
                                : "UNKNOWN"
                )
                .holidayYear(LocalDate.parse(response.getDate()).getYear())
                .countryCode(countryCode)
                .build();
    }
}