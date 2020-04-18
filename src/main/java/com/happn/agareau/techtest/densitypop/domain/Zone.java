package com.happn.agareau.techtest.densitypop.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.lang.Double.compare;
import static java.util.Objects.hash;

@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
@Setter
@Getter
public class Zone {
    private double minLat;
    private double minLong;
    private double maxLat;
    private double maxLong;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return compare(zone.minLat, minLat) == 0 &&
                compare(zone.minLong, minLong) == 0 &&
                compare(zone.maxLat, maxLat) == 0 &&
                compare(zone.maxLong, maxLong) == 0;
    }

    @Override
    public int hashCode() {
        return hash(minLat, minLong, maxLat, maxLong);
    }


    @Override
    public String toString() {
        return "Zone{" +
                "minLat=" + minLat +
                ", minLong=" + minLong +
                ", maxLat=" + maxLat +
                ", maxLong=" + maxLong +
                '}';
    }


}
