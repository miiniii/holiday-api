package com.mh.planit.holidayapi.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "holiday")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String localName;

    @Column(name = "holiday_date")
    private LocalDate date;

    @Column(name = "holiday_year")
    private int year;

    @Column(name = "holiday_month")
    private int month;

    @Column(name = "holiday_day")
    private int day;

    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", referencedColumnName = "code")
    private Country country;
}