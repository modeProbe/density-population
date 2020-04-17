package com.happn.agareau.techtest.densitypop;

import com.happn.agareau.techtest.densitypop.domain.PointOfInterest;
import com.happn.agareau.techtest.densitypop.service.TSVService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TSVServiceTest {

    private final TSVService tsvService = new TSVService();

    @Test
    void should_return_list_poi() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("coordinatesTest.tsv");

        //WHEN
        List<PointOfInterest> listPOIFromFile = tsvService.createListPOIFromFile(resource.getFile());

        //THEN
        assertNotNull(listPOIFromFile);
        assertEquals(9, listPOIFromFile.size());
        PointOfInterest pointOfInterest = listPOIFromFile.get(0);
        assertNotNull(pointOfInterest);
        assertEquals(-48.6, pointOfInterest.getLatitude());
    }

    @Test
    void should_return_filtered_list_poi() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("coordinatesTestWithIncorrectAndEmptyLines.tsv");

        //WHEN
        List<PointOfInterest> listPOIFromFile = tsvService.createListPOIFromFile(resource.getFile());

        //THEN
        assertNotNull(listPOIFromFile);
        assertEquals(9, listPOIFromFile.size());
        PointOfInterest pointOfInterest = listPOIFromFile.get(0);
        assertNotNull(pointOfInterest);
        assertEquals(-48.6, pointOfInterest.getLatitude());
    }

    @Test
    void should_return_empty_list() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("empty.tsv");


        //WHEN
        List<PointOfInterest> listPOIFromFile = tsvService.createListPOIFromFile(resource.getFile());

        //THEN
        assertTrue(listPOIFromFile.isEmpty());
    }

    @Test
    void should_handle_large_file_and_return_list_poi() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("LargeFile100000lines.tsv");

        //WHEN
        List<PointOfInterest> listPOIFromFile = tsvService.createListPOIFromFile(resource.getFile());

        //THEN
        assertNotNull(listPOIFromFile);
        assertEquals(100000, listPOIFromFile.size());
    }

}