package com.happn.agareau.techtest.densitypop.controller;

import com.happn.agareau.techtest.densitypop.domain.PointOfInterest;
import com.happn.agareau.techtest.densitypop.domain.SingletonListPOI;
import com.happn.agareau.techtest.densitypop.error.Error;
import com.happn.agareau.techtest.densitypop.service.TSVService;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
public class FileUploadController {

    private final TSVService tsvService;
    private final SingletonListPOI singletonListPOI;

    @PostMapping(value = "/upload", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public List<PointOfInterest> handleFileUpload(@RequestParam("file") MultipartFile file) {

        Validation<Seq<Error>, List<PointOfInterest>> pointOfInterest = tsvService.uploadAndReadTSVFileAndReturnListPOI(file);
        singletonListPOI.setPointOfInterests(pointOfInterest.get());
        return pointOfInterest.get();

    }

}
