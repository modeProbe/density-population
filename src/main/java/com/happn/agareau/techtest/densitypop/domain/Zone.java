package com.happn.agareau.techtest.densitypop.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Zone {
    private double minLat;
    private double minLong;
    private double maxLat;
    private double maxLong;


    public double getMinLat() {
        return minLat;
    }

    public void setMinLat(double minLat) {
        this.minLat = minLat;
    }

    public double getMinLong() {
        return minLong;
    }

    public void setMinLong(double minLong) {
        this.minLong = minLong;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public void setMaxLat(double maxLat) {
        this.maxLat = maxLat;
    }

    public double getMaxLong() {
        return maxLong;
    }

    public void setMaxLong(double maxLong) {
        this.maxLong = maxLong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return Double.compare(zone.minLat, minLat) == 0 &&
                Double.compare(zone.minLong, minLong) == 0 &&
                Double.compare(zone.maxLat, maxLat) == 0 &&
                Double.compare(zone.maxLong, maxLong) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minLat, minLong, maxLat, maxLong);
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
