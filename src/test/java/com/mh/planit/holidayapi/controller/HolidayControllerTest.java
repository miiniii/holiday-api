package com.mh.planit.holidayapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mh.planit.holidayapi.domain.Holiday;
import com.mh.planit.holidayapi.dto.HolidaySearchCondition;
import com.mh.planit.holidayapi.service.HolidayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(HolidayControllerTest.TestHolidayConfig.class)
class HolidayControllerTest {

    @Resource
    private MockMvc mockMvc;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private HolidayService holidayService;

    @TestConfiguration
    static class TestHolidayConfig {
        @Bean
        public HolidayService holidayService() {
            return org.mockito.Mockito.mock(HolidayService.class);
        }
    }

    @Test
    @DisplayName("공휴일 검색 API - 조건 필터로 검색 성공")
    void searchHoliday() throws Exception {
        // given
        Holiday holiday = Holiday.builder()
                .name("New Year")
                .localName("신정")
                .date(LocalDate.of(2025, 1, 1))
                .holidayYear(2025)
                .holidayMonth(1)
                .holidayday(1)
                .type("National")
                .build();

        Page<Holiday> result = new PageImpl<>(List.of(holiday), PageRequest.of(0, 10), 1);

        when(holidayService.searchHolidays(any(HolidaySearchCondition.class), any())).thenReturn(result);

        // when & then
        mockMvc.perform(get("/holidays/search")
                        .param("year", "2025")
                        .param("month", "1")
                        .param("type", "National")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("New Year"));
    }
}