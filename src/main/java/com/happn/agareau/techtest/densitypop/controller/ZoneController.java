package com.happn.agareau.techtest.densitypop.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.happn.agareau.techtest.densitypop.domain.SingletonListPOI;
import com.happn.agareau.techtest.densitypop.domain.Zone;
import com.happn.agareau.techtest.densitypop.service.ZoneService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class ZoneController {

    private final SingletonListPOI singletonListPOI;
    private final ZoneService zoneService;

    @GetMapping(value = "/zone/number-poi/{min-lat}/{min-lon}", produces = "application/json")
    public JSONPObject getList(@PathVariable("min-lat") double minLat, @PathVariable("min-lon") double minLon) {
        Zone zone = new Zone(minLat, minLon, minLat + 0.5, minLon + 0.5);
        long l = zoneService.nbPoiInZone(zone, singletonListPOI.getPointOfInterests());
        return new JSONPObject("value", l);

    }

    @GetMapping(value = "/zones/{nb-zones}/most-dense-zone", produces = "application/json")
    public List<Zone> getList(@PathVariable("nb-zones") int nbZones) {
        return zoneService.findMostDenseZone(nbZones, singletonListPOI.getPointOfInterests());
    }
}
