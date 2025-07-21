package com.mh.planit.holidayapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HolidaySearchRequest {

    private String countryCode;

    private String type;

    private String from;
    private String to;
}
