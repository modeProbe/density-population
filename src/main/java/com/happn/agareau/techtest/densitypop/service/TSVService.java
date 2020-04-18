package com.happn.agareau.techtest.densitypop.service;

import com.happn.agareau.techtest.densitypop.domain.PointOfInterest;
import com.happn.agareau.techtest.densitypop.error.Error;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.happn.agareau.techtest.densitypop.error.Error.ReadFileError;
import static com.happn.agareau.techtest.densitypop.error.Error.UploadFileError;

@Service
@AllArgsConstructor
public class TSVService {

    private static final Logger logger = LoggerFactory.getLogger(TSVService.class);
    private static final String TAB = "\\t";
    private static final int NB_ELEMS_PER_LINE = 3;
    private static final String PREFIX_TEMP_FILE = "temp-file-name";

    public Validation<Seq<Error>, List<PointOfInterest>> uploadAndReadTSVFileAndReturnListPOI(MultipartFile file) {
        return Try.of(() -> File
                .createTempFile(PREFIX_TEMP_FILE + LocalDateTime.now(), ".tsv"))
                .toValidation(e -> new UploadFileError(file.getName(), e))
                .mapError(Error::logThenBuildSeqError)
                .peek(File::deleteOnExit)
                .flatMap(this::createListPOIFromFile);
    }

    //public for testing purpose
    public Validation<Seq<Error>, List<PointOfInterest>> createListPOIFromFile(File file) {

        return readTSVFile(file)
                .map(lines -> lines
                        .stream()
                        .skip(1)
                        .map(mapLineToPOI)
                        .collect(Collectors.toList()));

    }

    private Validation<Seq<Error>, List<String>> readTSVFile(File file) {


        Validation<Seq<Error>, Stream<String>> streams = Try.of(() -> Files
                .lines(file.toPath()))
                .toValidation(e -> new ReadFileError(file.getName(), e))
                .mapError(Error::logThenBuildSeqError);

        return streams.map(stringStream ->
                stringStream
                        .filter(line -> !line.isEmpty())
                        .filter(isLineCorrect)
                        .collect(Collectors.toList()));

    }

    private final Predicate<String> isLineCorrect = (line) -> {
        String[] p = line.split(TAB);// a TSV has tab separated lines
        if (p.length != NB_ELEMS_PER_LINE) {
            logger.error("Line {} incorrect: needed {} elements but found: {} ", line, NB_ELEMS_PER_LINE, p.length);
            return false;
        }
        return true;
    };

    private final Function<String, PointOfInterest> mapLineToPOI = (line) -> {
        String[] p = line.split(TAB);
        String id = p[0];
        double latitude = Double.parseDouble(p[1]);
        double longitude = Double.parseDouble(p[2]);
        return new PointOfInterest(id, latitude, longitude);
    };


}
