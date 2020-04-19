package com.happn.agareau.techtest.densitypop.controller;

import com.happn.agareau.techtest.densitypop.domain.PointOfInterest;
import com.happn.agareau.techtest.densitypop.error.Error;
import com.happn.agareau.techtest.densitypop.properties.CoordinatesProperties;
import com.happn.agareau.techtest.densitypop.service.TSVService;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static com.happn.agareau.techtest.densitypop.error.Error.ReadFileError;
import static com.happn.agareau.techtest.densitypop.error.Error.UploadFileError;
import static io.vavr.API.Seq;
import static io.vavr.control.Validation.invalid;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileUploadController.class)
@EnableConfigurationProperties(CoordinatesProperties.class)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TSVService tsvService;

    @Value("classpath:/response/uploadTsvFileOKResponse.json")
    private Resource uploadTsvFileOKResponse;

    @Value("classpath:/response/uploadTsvFileKOResponse.json")
    private Resource uploadTsvFileKOResponse;

    @Test
    void upload_file_and_return_list_should_success() throws Exception {

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Test upload".getBytes());

        when(tsvService.uploadAndReadTSVFileAndReturnListPOI(multipartFile)).thenReturn(Validation.valid(createListPOI()));


        ResultActions resultActions = this.mockMvc
                .perform(multipart("/upload")
                        .file(multipartFile));

//        ResultActions resultActions = this.mockMvc.perform(post("/upload")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(Objects.requireNonNull(readResource(betResourceRequest))));


        resultActions.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(readResource(uploadTsvFileOKResponse), true));

    }

    @Test
    void upload_file_should_fail() throws Exception {

        Seq<Error> fileError = Seq(new UploadFileError("cannot upload file", new Throwable()),
                new ReadFileError("cannot read file", new IOException()));

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Test upload".getBytes());

        when(tsvService.uploadAndReadTSVFileAndReturnListPOI(multipartFile)).thenReturn(invalid(fileError));

        ResultActions resultActions = this.mockMvc
                .perform(multipart("/upload")
                        .file(multipartFile));

        resultActions.andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(readResource(uploadTsvFileKOResponse), true));

    }


    private String readResource(Resource resource) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private List<PointOfInterest> createListPOI() {

        PointOfInterest pointOfInterest = new PointOfInterest("id0", -48.6, -37.7);
        PointOfInterest pointOfInterest1 = new PointOfInterest("id1", -27.1, 8.4);
        PointOfInterest pointOfInterest2 = new PointOfInterest("id2", 6.6, -6.9);
        PointOfInterest pointOfInterest3 = new PointOfInterest("id3", -2.3, 38.3);
        PointOfInterest pointOfInterest4 = new PointOfInterest("id4", 6.8, -6.9);
        PointOfInterest pointOfInterest5 = new PointOfInterest("id5", -2.5, 38.3);
        PointOfInterest pointOfInterest6 = new PointOfInterest("id6", 0.1, -0.1);
        PointOfInterest pointOfInterest7 = new PointOfInterest("id7", -2.1, 38.1);
        PointOfInterest pointOfInterest8 = new PointOfInterest("id8", -2.1, 33.1);
        return List.of(pointOfInterest, pointOfInterest1, pointOfInterest2, pointOfInterest3, pointOfInterest4, pointOfInterest5, pointOfInterest6, pointOfInterest7, pointOfInterest8);

    }

}