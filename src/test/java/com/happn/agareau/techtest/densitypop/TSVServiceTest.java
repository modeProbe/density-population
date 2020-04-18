package com.happn.agareau.techtest.densitypop;

import com.happn.agareau.techtest.densitypop.domain.PointOfInterest;
import com.happn.agareau.techtest.densitypop.domain.SingletonListPOI;
import com.happn.agareau.techtest.densitypop.error.Error;
import com.happn.agareau.techtest.densitypop.service.TSVService;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static org.assertj.vavr.api.VavrAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TSVServiceTest {

    private final SingletonListPOI singletonListPOI = new SingletonListPOI();
    private final TSVService tsvService = new TSVService(singletonListPOI);

    @Test
    void should_return_list_poi() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("coordinatesTest.tsv");

        //WHEN
        Validation<Seq<Error>, List<PointOfInterest>> listPOIFromFile = tsvService.createListPOIFromFile(resource.getFile());

        //THEN
        assertThat(listPOIFromFile).isValid();
        assertNotNull(listPOIFromFile.get());
        assertEquals(9, listPOIFromFile.get().size());
        PointOfInterest pointOfInterest = listPOIFromFile.get().get(0);
        assertNotNull(pointOfInterest);
        assertEquals(-48.6, pointOfInterest.getLatitude());
    }

    @Test
    void should_return_filtered_list_poi() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("coordinatesTestWithIncorrectAndEmptyLines.tsv");

        //WHEN
        Validation<Seq<Error>, List<PointOfInterest>> listPOIFromFile = tsvService.createListPOIFromFile(resource.getFile());

        //THEN
        assertThat(listPOIFromFile).isValid();
        assertNotNull(listPOIFromFile.get());
        assertEquals(9, listPOIFromFile.get().size());
        PointOfInterest pointOfInterest = listPOIFromFile.get().get(0);
        assertNotNull(pointOfInterest);
        assertEquals(-48.6, pointOfInterest.getLatitude());
    }

    @Test
    void should_return_empty_list() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("empty.tsv");


        //WHEN
        Validation<Seq<Error>, List<PointOfInterest>> listPOIFromFile = tsvService.createListPOIFromFile(resource.getFile());

        //THEN
        assertThat(listPOIFromFile).isValid();
        assertTrue(listPOIFromFile.get().isEmpty());
    }

    @Test
    void should_handle_large_file_and_return_list_poi() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("LargeFile100000lines.tsv");

        //WHEN
        Validation<Seq<Error>, List<PointOfInterest>> listPOIFromFile = tsvService.createListPOIFromFile(resource.getFile());

        //THEN
        assertThat(listPOIFromFile).isValid();
        assertNotNull(listPOIFromFile);
        assertEquals(100000, listPOIFromFile.get().size());
    }

}