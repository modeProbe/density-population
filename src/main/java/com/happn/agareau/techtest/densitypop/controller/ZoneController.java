package com.happn.agareau.techtest.densitypop.controller;

import com.happn.agareau.techtest.densitypop.domain.SingletonListPOI;
import com.happn.agareau.techtest.densitypop.domain.Zone;
import com.happn.agareau.techtest.densitypop.error.Error;
import com.happn.agareau.techtest.densitypop.service.ZoneService;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public long getList(@PathVariable("min-lat") double minLat, @PathVariable("min-lon") double minLon) {
        Zone zone = new Zone(minLat, minLon, minLat + 0.5, minLon + 0.5);
        return zoneService.nbPoiInZone(zone, singletonListPOI.getPointOfInterests());
    }

    @GetMapping(value = "/zones/{nb-zones}/most-dense-zone", produces = "application/json")
    public ResponseEntity<?> getList(@PathVariable("nb-zones") int nbZones) {
        Validation<Seq<Error>, List<Zone>> mostDenseZone = zoneService.findMostDenseZone(nbZones, singletonListPOI.getPointOfInterests());

        return mostDenseZone.isValid()
                ? ResponseEntity.status(HttpStatus.OK).body(mostDenseZone.get())
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mostDenseZone.getError().toJavaList());
    }
}
