package com.happn.agareau.techtest.densitypop;

import com.happn.agareau.techtest.densitypop.domain.PointOfInterest;
import com.happn.agareau.techtest.densitypop.domain.Zone;
import com.happn.agareau.techtest.densitypop.properties.CoordinatesProperties;
import com.happn.agareau.techtest.densitypop.service.ZoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ZoneServiceTest {

    @Mock
    private CoordinatesProperties coordinatesProperties;

    private ZoneService zoneService;

    @BeforeEach
    void setup() {

        lenient().when(coordinatesProperties.getMinLatitude()).thenReturn(-90.0);
        lenient().when(coordinatesProperties.getMaxLatitude()).thenReturn(90.0);
        lenient().when(coordinatesProperties.getMinLongitude()).thenReturn(-180.0);
        lenient().when(coordinatesProperties.getMaxLongitude()).thenReturn(180.0);
        zoneService = new ZoneService(coordinatesProperties);
    }

    @Test
    void should_find_nb_poi_in_zone() {
        //GIVEN
        List<PointOfInterest> pointOfInterests = createListPOI();
        Zone zone = new Zone(-5.5, 8.0, -5, 8.5);

        //WHEN
        long nbPoiInZone = zoneService.nbPoiInZone(zone, pointOfInterests);

        //THEN
        assertEquals(3, nbPoiInZone);

    }

    @Test
    void should_return_zero_because_no_poi_in_zone() {
        //GIVEN
        List<PointOfInterest> pointOfInterests = createListPOI();
        Zone zone = new Zone(-18, 58.0, -17.5, 58.5);

        //WHEN
        long nbPoiInZone = zoneService.nbPoiInZone(zone, pointOfInterests);

        //THEN
        assertEquals(0, nbPoiInZone);
    }

    @Test
    void should_return_n_most_dense_zones() {
        //GIVEN
        List<PointOfInterest> pointOfInterests = createListPOI();
        int nbZones = 2;

        //when
        List<Zone> mostDenseZone = zoneService.findMostDenseZone(nbZones, pointOfInterests);

        //THEN
        assertEquals(2, mostDenseZone.size());
        Zone zone1 = mostDenseZone.get(0);
        Zone zoneExpected = new Zone(-5.5, 8.0, -5, 8.5);
        assertEquals(zoneExpected, zone1);
        Zone zone2 = mostDenseZone.get(1);
        Zone zoneExpected2 = new Zone(-6.0, 8.0, -5.5, 8.5);
        assertEquals(zoneExpected2, zone2);

    }

    @Test
    void should_return_n_most_dense_zones_excluded_zone_outside_grid() {
        //GIVEN
        List<PointOfInterest> pointOfInterests = new java.util.ArrayList<>(createListPOI());
        pointOfInterests.add(new PointOfInterest("idExt", 90, 180));
        int nbZones = 200000;

        //WHEN
        List<Zone> mostDenseZone = zoneService.findMostDenseZone(nbZones, pointOfInterests);


        //THEN
        Zone inside = new Zone(89.5, 179.5, 90, 180);
        assertTrue(mostDenseZone.contains(inside));

        Zone outsideOne = new Zone(90, 179.5, 90.5, 180);
        assertFalse(mostDenseZone.contains(outsideOne));

        Zone outsideTwo = new Zone(89.5, 180, 90, 180.5);
        assertFalse(mostDenseZone.contains(outsideTwo));

        Zone outsideThree = new Zone(90, 180, 90.5, 180.5);
        assertFalse(mostDenseZone.contains(outsideThree));
    }

    @Test
    void should_return_max_most_dense_zones() {
        //GIVEN
        List<PointOfInterest> pointOfInterests = createListPOI();
        int nbZones = 20000000;

        //when
        List<Zone> mostDenseZone = zoneService.findMostDenseZone(nbZones, pointOfInterests);

        //THEN
        //NbZones is greater than size list return by findMostDenseZone so we return all the elements from the list
        assertEquals(12, mostDenseZone.size());


    }

    @Test
    void should_return_only_one_zone_from_poi() {
        //GIVEN
        PointOfInterest pointOfInterest = new PointOfInterest("id0", -3.4, 67.8);

        //WHEN
        List<Zone> zoneFromPOI = zoneService.getZoneFromPoi
                .apply(pointOfInterest)
                .collect(Collectors.toList());

        //THEN
        assertEquals(1, zoneFromPOI.size());
        Zone actual = zoneFromPOI.get(0);
        Zone expected = new Zone(-3.5, 67.5, -3, 68);
        assertEquals(expected, actual);
    }

    @Test
    void should_return_two_zones_left_right_from_poi() {
        //GIVEN
        PointOfInterest pointOfInterest = new PointOfInterest("id0", 1, 1.6);

        //WHEN
        List<Zone> zoneFromPOI = zoneService.getZoneFromPoi
                .apply(pointOfInterest)
                .collect(Collectors.toList());

        //THEN
        assertEquals(2, zoneFromPOI.size());
        Zone actualOne = zoneFromPOI.get(0);
        Zone expectedOne = new Zone(0.5, 1.5, 1, 2);
        assertEquals(expectedOne, actualOne);
        Zone actualTwo = zoneFromPOI.get(1);
        Zone expectedTwo = new Zone(1, 1.5, 1.5, 2);
        assertEquals(expectedTwo, actualTwo);
    }

    @Test
    void should_return_two_zones_top_bottom_from_poi() {
        //GIVEN
        PointOfInterest pointOfInterest = new PointOfInterest("id0", -45.7, 23);

        //WHEN
        List<Zone> zoneFromPOI = zoneService.getZoneFromPoi
                .apply(pointOfInterest)
                .collect(Collectors.toList());

        //THEN
        assertEquals(2, zoneFromPOI.size());
        Zone actualOne = zoneFromPOI.get(0);
        Zone expectedOne = new Zone(-46, 23, -45.5, 23.5);
        assertEquals(expectedOne, actualOne);
        Zone actualTwo = zoneFromPOI.get(1);
        Zone expectedTwo = new Zone(-46, 22.5, -45.5, 23);
        assertEquals(expectedTwo, actualTwo);
    }

    @Test
    void should_return_four_zones_topleft_topright_bottomleft_bottomright_from_poi() {
        //GIVEN
        PointOfInterest pointOfInterest = new PointOfInterest("id0", -50, -22);

        //WHEN
        List<Zone> zoneFromPOI = zoneService.getZoneFromPoi
                .apply(pointOfInterest)
                .collect(Collectors.toList());

        //THEN
        assertEquals(4, zoneFromPOI.size());

        Zone actualOne = zoneFromPOI.get(0);
        Zone expectedOne = new Zone(-50.5, -22.5, -50, -22);
        assertEquals(expectedOne, actualOne);

        Zone actualTwo = zoneFromPOI.get(1);
        Zone expectedTwo = new Zone(-50.5, -22, -50, -21.5);
        assertEquals(expectedTwo, actualTwo);

        Zone actualThree = zoneFromPOI.get(2);
        Zone expectedThree = new Zone(-50, -22.5, -49.5, -22);
        assertEquals(expectedThree, actualThree);

        Zone actualFour = zoneFromPOI.get(3);
        Zone expectedFour = new Zone(-50, -22, -49.5, -21.5);
        assertEquals(expectedFour, actualFour);


    }


    private List<PointOfInterest> createListPOI() {

        PointOfInterest pointOfInterest = new PointOfInterest("id0", -5.2, 8.3);
        PointOfInterest pointOfInterest1 = new PointOfInterest("id1", 3.5, 0.2);
        PointOfInterest pointOfInterest2 = new PointOfInterest("id2", 78.9, -24.3);
        PointOfInterest pointOfInterest3 = new PointOfInterest("id3", -5, 8);
        PointOfInterest pointOfInterest4 = new PointOfInterest("id4", 12.7, -6.9);
        PointOfInterest pointOfInterest5 = new PointOfInterest("id5", 45, 12.3);
        PointOfInterest pointOfInterest6 = new PointOfInterest("id6", -5.5, 8.1);
        PointOfInterest pointOfInterest7 = new PointOfInterest("id7", -5.7, 8.2);
        PointOfInterest pointOfInterest8 = new PointOfInterest("id8", -4.7, 45.3);

        return List.of(pointOfInterest, pointOfInterest1, pointOfInterest2, pointOfInterest3, pointOfInterest4, pointOfInterest5, pointOfInterest6, pointOfInterest7, pointOfInterest8);

    }

}