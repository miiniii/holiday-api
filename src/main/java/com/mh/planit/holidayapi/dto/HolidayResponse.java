package com.mh.planit.holidayapi.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HolidayResponse {

    private String date;

    private String localName;

    private String name;

    private String countryCode;

    private List<String> types;
}
