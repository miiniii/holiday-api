package com.mh.planit.holidayapi.batch;

import com.mh.planit.holidayapi.domain.Country;
import com.mh.planit.holidayapi.service.CountryService;
import com.mh.planit.holidayapi.service.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayBatchService {

    private final HolidayService holidayService;
    private final CountryService countryService;

    /**
     * 매년 1월 2일 01시 정각(KST)에 실행
     */
    @Scheduled(cron = "0 0 1 2 1 *", zone = "Asia/Seoul")
    public void refreshHolidayBatch() {
        int thisYear = LocalDate.now().getYear();
        int lastYear = thisYear - 1;

        List<Country> countries = countryService.getAll();

        for (Country country : countries) {
            String code = country.getCode();
            log.info("[배치] {}년도 공휴일 재동기화 시작 (국가: {})", lastYear, code);
            holidayService.refreshHolidays(code, lastYear);

            log.info("[배치] {}년도 공휴일 재동기화 시작 (국가: {})", thisYear, code);
            holidayService.refreshHolidays(code, thisYear);
        }

        log.info("=== [배치] 전년도·금년도 공휴일 재동기화 완료 ===");
    }
}
