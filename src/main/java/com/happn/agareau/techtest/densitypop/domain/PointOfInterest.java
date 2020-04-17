package com.happn.agareau.techtest.densitypop.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PointOfInterest implements Comparable<PointOfInterest> {

    private String id;
    private double latitude;
    private double longitude;

    @Override
    public String toString() {
        return "PointOfInterest{" +
                "id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public int compareTo(PointOfInterest o) {
        return Double.compare(this.getLatitude(), o.getLatitude()) == 0 ? Double.compare(this.getLongitude(), o.getLongitude()) : Double.compare(this.getLatitude(), o.getLatitude());
    }
}
