package com.happn.agareau.techtest.densitypop.service;

import com.happn.agareau.techtest.densitypop.domain.PointOfInterest;
import com.happn.agareau.techtest.densitypop.domain.SingletonListPOI;
import com.happn.agareau.techtest.densitypop.domain.Zone;
import com.happn.agareau.techtest.densitypop.error.Error;
import com.happn.agareau.techtest.densitypop.properties.CoordinatesProperties;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.happn.agareau.techtest.densitypop.error.Error.NbZonesNegError;
import static java.util.Collections.reverseOrder;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Service
@AllArgsConstructor
@Value
public class ZoneService {

    SingletonListPOI singletonListPOI;
    CoordinatesProperties coordinatesProperties;

    public long nbPoiInZone(Zone zone) {

        return singletonListPOI.getPointOfInterests()
                .stream()
                .filter(pointOfInterest -> pointOfInterest.getLatitude() >= zone.getMinLat() && pointOfInterest.getLatitude() <= zone.getMaxLat())
                .filter(pointOfInterest -> pointOfInterest.getLongitude() >= zone.getMinLong() && pointOfInterest.getLongitude() <= zone.getMaxLong())
                .count();
    }

    public Validation<Seq<Error>, List<Zone>> findMostDenseZone(int nbZones) {

        Map<Zone, Long> mapZoneNbPoi = singletonListPOI.getPointOfInterests()
                .stream()
                .filter(poi -> isPOICorrect.test(poi, coordinatesProperties))
                .flatMap(getZoneFromPoi)
                .filter(this::checkZone)
                .collect(groupingBy(identity(), counting()));

        LinkedHashMap<Zone, Long> sortedMap = mapZoneNbPoi
                .entrySet()
                .stream()
                .sorted(reverseOrder(Entry.comparingByValue()))
                .collect(
                        toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        return Try.of(() -> sortedMap.keySet().stream().limit(nbZones))
                .toValidation(e -> new NbZonesNegError(nbZones, e))
                .mapError(Error::logThenBuildSeqError)
                .map(zoneStream -> zoneStream.collect(toList()));
    }

    //Public for testing purpose
    public Function<PointOfInterest, Stream<Zone>> getZoneFromPoi = (pointOfInterest) -> {
        double currentLatitude = pointOfInterest.getLatitude();
        double currentLongitude = pointOfInterest.getLongitude();
        long closestLat = Math.round(currentLatitude);
        long closestLong = Math.round(currentLongitude);
        boolean isLatOnEdge = Math.abs(currentLatitude) % 1 == 0 || Math.abs(currentLatitude) % 1 == 0.5;
        boolean isLongOnEdge = Math.abs(currentLongitude) % 1 == 0 || Math.abs(currentLongitude) % 1 == 0.5;
        if (isLatOnEdge && isLongOnEdge) {
            return getAdjacentsZone(currentLatitude, currentLongitude);
        }
        if (isLatOnEdge) {
            return getZonesLeftAndRight(currentLatitude, currentLongitude, closestLong);
        }
        if (isLongOnEdge) {
            return getZonesTopAndBottom(currentLatitude, currentLongitude, closestLat);
        }
        return getContainedZone(currentLatitude, currentLongitude, closestLat, closestLong);
    };

    private Stream<Zone> getContainedZone(double currentLatitude, double currentLongitude, long closestLat, long closestLong) {
        Zone zone = new Zone();
        if (closestLat < currentLatitude) {
            zone.setMinLat(closestLat);
            zone.setMaxLat(closestLat + 0.5);
        } else {
            zone.setMinLat(closestLat - 0.5);
            zone.setMaxLat(closestLat);
        }
        if (closestLong < currentLongitude) {
            zone.setMinLong(closestLong);
            zone.setMaxLong(closestLong + 0.5);
        } else {
            zone.setMinLong(closestLong - 0.5);
            zone.setMaxLong(closestLong);
        }
        return Stream.of(zone);
    }

    private Stream<Zone> getZonesTopAndBottom(double currentLatitude, double currentLongitude, long closestLat) {
        double minLat;
        double maxLat;
        if (closestLat > currentLatitude) {
            minLat = closestLat - 0.5;
            maxLat = closestLat;
        } else {
            minLat = closestLat;
            maxLat = closestLat + 0.5;
        }
        Zone zoneTop = new Zone(minLat, currentLongitude, maxLat, currentLongitude + 0.5);
        Zone zoneBottom = new Zone(minLat, currentLongitude - 0.5, maxLat, currentLongitude);
        return Stream.of(zoneTop, zoneBottom);
    }

    private Stream<Zone> getZonesLeftAndRight(double currentLatitude, double currentLongitude, long closestLong) {
        double minLon;
        double maxLon;
        if (closestLong < currentLongitude) {
            minLon = closestLong;
            maxLon = closestLong + 0.5;
        } else {
            minLon = closestLong - 0.5;
            maxLon = closestLong;
        }
        Zone zoneLeft = new Zone(currentLatitude - 0.5, minLon, currentLatitude, maxLon);
        Zone zoneRight = new Zone(currentLatitude, minLon, currentLatitude + 0.5, maxLon);
        return Stream.of(zoneLeft, zoneRight);
    }

    private Stream<Zone> getAdjacentsZone(double currentLatitude, double currentLongitude) {
        Zone zoneTopLeft = getZoneTopLeft(currentLatitude, currentLongitude);
        Zone zoneBottomLeft = getZoneBottomLeft(currentLatitude, currentLongitude);
        Zone zoneTopRight = getZoneTopRight(currentLatitude, currentLongitude);
        Zone zoneBottomRight = getZoneBottomRight(currentLatitude, currentLongitude);
        return Stream.of(zoneBottomLeft, zoneTopLeft, zoneBottomRight, zoneTopRight);
    }

    private boolean checkZone(Zone zone) {
        return zone.getMinLat() >= coordinatesProperties.getMinLatitude() && zone.getMaxLat() <= coordinatesProperties.getMaxLatitude()
                && zone.getMinLong() >= coordinatesProperties.getMinLongitude() && zone.getMaxLong() <= coordinatesProperties.getMaxLongitude();
    }

    BiPredicate<PointOfInterest, CoordinatesProperties> isPOICorrect = (pointOfInterest, coordinatesProperties) -> {
        boolean correctLatitude = pointOfInterest.getLatitude() >= coordinatesProperties.getMinLatitude() && pointOfInterest.getLatitude() <= coordinatesProperties.getMaxLatitude();
        boolean correctLongitude = pointOfInterest.getLongitude() >= coordinatesProperties.getMinLongitude() && pointOfInterest.getLongitude() <= coordinatesProperties.getMaxLongitude();
        return correctLatitude && correctLongitude;
    };

    private Zone getZoneBottomRight(double currentLatitude, double currentLongitude) {
        return new Zone(currentLatitude, currentLongitude - 0.5, currentLatitude + 0.5, currentLongitude);
    }

    private Zone getZoneTopRight(double currentLatitude, double currentLongitude) {
        return new Zone(currentLatitude, currentLongitude, currentLatitude + 0.5, currentLongitude + 0.5);
    }

    private Zone getZoneBottomLeft(double currentLatitude, double currentLongitude) {
        return new Zone(currentLatitude - 0.5, currentLongitude - 0.5, currentLatitude, currentLongitude);
    }

    private Zone getZoneTopLeft(double currentLatitude, double currentLongitude) {
        return new Zone(currentLatitude - 0.5, currentLongitude, currentLatitude, currentLongitude + 0.5);
    }
}
