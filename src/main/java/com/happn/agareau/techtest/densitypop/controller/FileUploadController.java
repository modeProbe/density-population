package com.happn.agareau.techtest.densitypop.controller;

import com.happn.agareau.techtest.densitypop.service.TSVService;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.*;

@RestController
@AllArgsConstructor
@Value
public class FileUploadController {

    TSVService tsvService;

    @PostMapping(value = "/upload", produces = "application/json")
    public ResponseEntity<?> uploadTsvFile(@RequestParam("file") MultipartFile file) {
        return tsvService.uploadAndReadTSVFileAndReturnListPOI(file)
                .fold(errors -> status(INTERNAL_SERVER_ERROR).body(errors.toJavaList()),
                        pointOfInterests -> status(CREATED).body(pointOfInterests));
    }

}
