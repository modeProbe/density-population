package com.happn.agareau.techtest.densitypop.service;

import com.happn.agareau.techtest.densitypop.domain.PointOfInterest;
import com.happn.agareau.techtest.densitypop.domain.SingletonListPOI;
import com.happn.agareau.techtest.densitypop.error.Error;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.happn.agareau.techtest.densitypop.error.Error.ReadFileError;
import static com.happn.agareau.techtest.densitypop.error.Error.UploadFileError;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Value
public class TSVService {

    private static final Logger logger = LoggerFactory.getLogger(TSVService.class);
    private static final String TAB = "\\t";
    private static final int NB_ELEMS_PER_LINE = 3;
    private static final String PREFIX_TEMP_FILE = "temp-file-name";
    SingletonListPOI singletonListPOI;

    public Validation<Seq<Error>, List<PointOfInterest>> uploadAndReadTSVFileAndReturnListPOI(MultipartFile multipartFile) {
        return Try.of(() -> File
                .createTempFile(PREFIX_TEMP_FILE + now(), ".tsv"))
                .toValidation(e -> new UploadFileError(multipartFile.getName(), e))
                .mapError(Error::logThenBuildSeqError)
                .flatMap(tempFile -> Try.run(() -> multipartFile.transferTo(tempFile))
                        .toValidation(e -> new UploadFileError(multipartFile.getName(), e))
                        .mapError(Error::logThenBuildSeqError)
                        .map(aVoid -> tempFile))
                .peek(File::deleteOnExit)
                .flatMap(this::createListPOIFromFile)
                .peek(singletonListPOI::setPointOfInterests);
    }

    //public for testing purpose
    public Validation<Seq<Error>, List<PointOfInterest>> createListPOIFromFile(File file) {

        return readTSVFile(file)
                .map(lines -> lines
                        .stream()
                        .skip(1)
                        .map(mapLineToPOI)
                        .collect(toList()));

    }

    private Validation<Seq<Error>, List<String>> readTSVFile(File file) {

        return Try.of(() -> Files
                .lines(file.toPath()))
                .toValidation(e -> new ReadFileError(file.getName(), e))
                .mapError(Error::logThenBuildSeqError)
                .map(stringStream -> stringStream
                        .filter(line -> !line.isEmpty())
                        .filter(isLineCorrect)
                        .collect(toList()));
    }

    Predicate<String> isLineCorrect = line -> {
        String[] p = line.split(TAB);// a TSV has tab separated lines
        if (p.length != NB_ELEMS_PER_LINE) {
            logger.error("Line {} incorrect: needed {} elements but found: {} ", line, NB_ELEMS_PER_LINE, p.length);
            return false;
        }
        return true;
    };

    Function<String, PointOfInterest> mapLineToPOI = line -> {
        String[] p = line.split(TAB);
        String id = p[0];
        double latitude = Double.parseDouble(p[1]);
        double longitude = Double.parseDouble(p[2]);
        return new PointOfInterest(id, latitude, longitude);
    };


}
