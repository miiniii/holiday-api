package com.mh.planit.holidayapi.repository;

import com.mh.planit.holidayapi.domain.Holiday;
import com.mh.planit.holidayapi.dto.HolidaySearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayRepositoryCustom {
    Page<Holiday> search(HolidaySearchCondition condition, Pageable pageable);
}
