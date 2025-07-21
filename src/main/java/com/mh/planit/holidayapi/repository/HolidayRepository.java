package com.mh.planit.holidayapi.repository;

import com.mh.planit.holidayapi.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayRepositoryCustom {
}
