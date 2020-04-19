package com.happn.agareau.techtest.densitypop;

import com.happn.agareau.techtest.densitypop.domain.PointOfInterest;
import com.happn.agareau.techtest.densitypop.domain.SingletonListPOI;
import com.happn.agareau.techtest.densitypop.error.Error;
import com.happn.agareau.techtest.densitypop.service.TSVService;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.vavr.api.VavrAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TSVServiceTest {


    private final SingletonListPOI singletonListPOI = new SingletonListPOI();
    private final TSVService tsvService = new TSVService(singletonListPOI);

    @Test
    void should_upload_and_return_list() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("test-coordinates/coordinatesTest.tsv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", resource.getInputStream().readAllBytes());

        //WHEN
        Validation<Seq<Error>, List<PointOfInterest>> listValidation = tsvService.uploadAndReadTSVFileAndReturnListPOI(multipartFile);


        //THEN
        assertThat(listValidation).isValid();
        assertNotNull(singletonListPOI.getPointOfInterests());
        assertNotNull(listValidation.get());
        assertEquals(9, listValidation.get().size());
        PointOfInterest pointOfInterest = listValidation.get().get(0);
        assertNotNull(pointOfInterest);
        assertEquals(-48.6, pointOfInterest.getLatitude());

    }

//    @Test
//    void upload_should_fail() throws IOException {
//        //GIVEN
//        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
//                "text/plain", "Toto".getBytes());
//        when(File.createTempFile("temp", "toto")).thenThrow(new IOException());
//
//        //WHEN
//        Validation<Seq<Error>, List<PointOfInterest>> lists = tsvService.uploadAndReadTSVFileAndReturnListPOI(multipartFile);
//
//        //THEN
//        assertThat(lists).isInvalid();
//        assertNotNull(lists.getError());
//
//
//    }

    @Test
    void should_return_list_poi() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("test-coordinates/coordinatesTest.tsv");

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
        Resource resource = new ClassPathResource("test-coordinates/coordinatesTestWithIncorrectAndEmptyLines.tsv");

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
        Resource resource = new ClassPathResource("test-coordinates/empty.tsv");


        //WHEN
        Validation<Seq<Error>, List<PointOfInterest>> listPOIFromFile = tsvService.createListPOIFromFile(resource.getFile());

        //THEN
        assertThat(listPOIFromFile).isValid();
        assertTrue(listPOIFromFile.get().isEmpty());
    }

    @Test
    void should_handle_large_file_and_return_list_poi() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("test-coordinates/LargeFile100000lines.tsv");

        //WHEN
        Validation<Seq<Error>, List<PointOfInterest>> listPOIFromFile = tsvService.createListPOIFromFile(resource.getFile());

        //THEN
        assertThat(listPOIFromFile).isValid();
        assertNotNull(listPOIFromFile);
        assertEquals(100000, listPOIFromFile.get().size());
    }

}