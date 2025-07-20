package com.mh.planit.holidayapi.repository;

import com.mh.planit.holidayapi.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    @Query("SELECT new com.mh.planit.holidayapi.dto.HolidayDto(h.name, h.date, c.name) FROM Holiday h JOIN h.country c")
    List<HolidayDto> findAllProjected();
}
