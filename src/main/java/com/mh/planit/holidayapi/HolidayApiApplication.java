package com.mh.planit.holidayapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HolidayApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolidayApiApplication.class, args);
    }

}
