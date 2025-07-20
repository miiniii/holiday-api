package com.mh.planit.holidayapi.client;

import com.mh.planit.holidayapi.dto.CountryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CountryApiClient {

    @Qualifier("holidayWebClient")
    private final WebClient webClient;

    public List<CountryResponse> getAvailableCountries() {
        return webClient.get()
                .uri("/api/v3/AvailableCountries")
                .retrieve()
                .bodyToFlux(CountryResponse.class)
                .collectList()
                .block();
    }
}
