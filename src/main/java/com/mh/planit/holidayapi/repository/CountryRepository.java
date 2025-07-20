package com.mh.planit.holidayapi.repository;

import com.mh.planit.holidayapi.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, String> {

    Optional<Country> findByCode(String code);
}
