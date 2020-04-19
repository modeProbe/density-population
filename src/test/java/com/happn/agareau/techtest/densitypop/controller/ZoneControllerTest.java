package com.happn.agareau.techtest.densitypop.controller;

import com.happn.agareau.techtest.densitypop.domain.PointOfInterest;
import com.happn.agareau.techtest.densitypop.domain.Zone;
import com.happn.agareau.techtest.densitypop.error.Error;
import com.happn.agareau.techtest.densitypop.properties.CoordinatesProperties;
import com.happn.agareau.techtest.densitypop.service.ZoneService;
import io.vavr.control.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static io.vavr.API.Seq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ZoneController.class)
@EnableConfigurationProperties(CoordinatesProperties.class)
class ZoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ZoneService zoneService;

    @Value("classpath:/response/mostDensestZonesOKResponse.json")
    private Resource mostDensestZonesOkResponse;

    @Value("classpath:/response/mostDensestZonesKOResponse.json")
    private Resource mostDensestZonesKOResponse;

    @Test
    void get_number_poi_should_success() throws Exception {


        when(zoneService.nbPoiInZone(any())).thenReturn(2L);


        ResultActions resultActions = this.mockMvc.perform(get("/zone/number-poi/1.0/2.0")
                .contentType(MediaType.APPLICATION_JSON));


        resultActions.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));

    }

    @Test
    void get_most_densest_zones_should_success() throws Exception {

        when(zoneService.findMostDenseZone(2)).thenReturn(Validation.valid(mostDensestZones()));


        ResultActions resultActions = this.mockMvc.perform(get("/zones/2/most-dense-zone")
                .contentType(MediaType.APPLICATION_JSON));


        resultActions.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(readResource(mostDensestZonesOkResponse), true));

    }

    @Test
    void get_most_densest_zones_should_fail_because_nb_zones_incorrect() throws Exception {

        when(zoneService.findMostDenseZone(-3)).thenReturn(Validation.invalid(Seq(new Error.NbZonesNegError(-3, new Throwable()))));


        ResultActions resultActions = this.mockMvc.perform(get("/zones/-3/most-dense-zone")
                .contentType(MediaType.APPLICATION_JSON));


        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(readResource(mostDensestZonesKOResponse), true));

    }

    private List<Zone> mostDensestZones() {
        Zone zone1 = new Zone(-2.5, 38.0, -2.0, 38.5);
        Zone zone2 = new Zone(6.5, -7.0, 7.0, -6.5);
        return List.of(zone1, zone2);
    }

    private String readResource(Resource resource) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

}