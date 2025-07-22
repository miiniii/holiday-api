package com.mh.planit.holidayapi.repository;

import com.mh.planit.holidayapi.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayRepositoryCustom {

    // 국가 코드와 연도 기준 삭제
    void deleteByCountry_CodeAndHolidayYear(String countryCode, int holidayYear);

    List<Holiday> findByCountry_CodeAndHolidayYear(String countryCode, int year);

}
