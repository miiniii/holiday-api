package com.mh.planit.holidayapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
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
    private int holidayYear;

    @Column(name = "holiday_month")
    private int holidayMonth;

    @Column(name = "holiday_day")
    private int holidayday;

    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", referencedColumnName = "code")
    private Country country;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Holiday holiday = (Holiday) object;
        return Objects.equals(name, holiday.name)
                && Objects.equals(localName, holiday.localName)
                && Objects.equals(date, holiday.date)
                && Objects.equals(type, holiday.type)
                && Objects.equals(country.getCode(), holiday.country.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, localName, date, type, country.getCode());
    }
}