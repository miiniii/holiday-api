package com.mh.planit.holidayapi.repository;

import com.mh.planit.holidayapi.dto.HolidaySearchCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.BooleanBuilder;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import com.mh.planit.holidayapi.domain.Holiday;
import com.mh.planit.holidayapi.domain.QHoliday;
import com.mh.planit.holidayapi.domain.QCountry;

@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Holiday> search(HolidaySearchCondition condition, Pageable pageable) {
        QHoliday holiday = QHoliday.holiday;
        QCountry country = QCountry.country;

        BooleanBuilder builder = new BooleanBuilder();

        // 국가 코드 필터
        if (condition.getCountryCode() != null && !condition.getCountryCode().isEmpty()) {
            builder.and(holiday.country.code.eq(condition.getCountryCode()));
        }

        // 연도 필터
        if (condition.getYear() != null) {
            builder.and(holiday.holidayYear.eq(condition.getYear()));
        }

        // 월 필터
        if (condition.getMonth() != null) {
            builder.and(holiday.holidayMonth.eq(condition.getMonth()));
        }

        // 날짜 범위 필터
        if (condition.getFrom() != null) {
            builder.and(holiday.date.goe(condition.getFrom()));  // LocalDate
        }
        if (condition.getTo() != null) {
            builder.and(holiday.date.loe(condition.getTo()));
        }

        // 타입 필터
        if (condition.getType() != null && !condition.getType().isEmpty()) {
            builder.and(holiday.type.eq(condition.getType()));
        }

        // 결과 목록 조회
        List<Holiday> content = queryFactory
                .selectFrom(holiday)
                .join(holiday.country, country).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(holiday.date.asc()) // holidayDate → date 로 수정
                .fetch();

        // 전체 개수 조회
        Long total = queryFactory
                .select(holiday.count())
                .from(holiday)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

}

