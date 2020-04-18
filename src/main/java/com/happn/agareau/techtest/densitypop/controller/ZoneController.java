package com.happn.agareau.techtest.densitypop.controller;

import com.happn.agareau.techtest.densitypop.domain.Zone;
import com.happn.agareau.techtest.densitypop.service.ZoneService;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@AllArgsConstructor
@Value
public class ZoneController {

    ZoneService zoneService;

    @GetMapping(value = "/zone/number-poi/{min-lat}/{min-lon}", produces = "application/json")
    public long getList(@PathVariable("min-lat") double minLat, @PathVariable("min-lon") double minLon) {
        Zone zone = new Zone(minLat, minLon, minLat + 0.5, minLon + 0.5);
        return zoneService.nbPoiInZone(zone);
    }

    @GetMapping(value = "/zones/{nb-zones}/most-dense-zone", produces = "application/json")
    public ResponseEntity<?> getList(@PathVariable("nb-zones") int nbZones) {
        return zoneService.findMostDenseZone(nbZones)
                .fold(errors -> status(BAD_REQUEST).body(errors.toJavaList()),
                        mostDenseZones -> status(OK).body(mostDenseZones));
    }
}
