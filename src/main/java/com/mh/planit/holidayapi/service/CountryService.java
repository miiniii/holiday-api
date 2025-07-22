package com.mh.planit.holidayapi.service;

import com.mh.planit.holidayapi.domain.Country;
import com.mh.planit.holidayapi.dto.CountryResponse;
import com.mh.planit.holidayapi.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public void upsertCountries(List<CountryResponse> countryResponses) {
        for (CountryResponse response : countryResponses) {
            // 이미 있는 경우는 건너뛰거나 업데이트
            countryRepository.findByCode(response.getCountryCode())
                    .orElseGet(() -> countryRepository.save(
                            Country.builder()
                                    .code(response.getCountryCode())
                                    .name(response.getName())
                                    .build()
                    ));
        }
    }

    public Country getByCode(String code) {
        return countryRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 국가 코드입니다: " + code));
    }
}
