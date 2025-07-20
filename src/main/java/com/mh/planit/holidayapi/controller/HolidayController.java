package com.mh.planit.holidayapi.controller;


import com.mh.planit.holidayapi.domain.Holiday;
import com.mh.planit.holidayapi.dto.HolidayRequest;
import com.mh.planit.holidayapi.repository.HolidayRepository;
import com.mh.planit.holidayapi.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/holidays")
public class HolidayController {

    private final HolidayService holidayService;
    private final HolidayRepository holidayRepository;

    @PostMapping
    public ResponseEntity<String> save(@RequestBody HolidayRequest request) {
        holidayService.save(request);
        return ResponseEntity.ok("Saved");
    }

    // 지연로딩 확인용 테스트
    @GetMapping("/{id}")
    public ResponseEntity<String> getHoliday(@PathVariable Long id) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not Found"));

        String name = holiday.getName();
        String countryName = holiday.getCountry().getName(); // 여기서 LAZY 로딩 발생

        return ResponseEntity.ok("Holiday: " + name + ", Country: " + countryName);
    }
}
