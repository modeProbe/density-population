package com.happn.agareau.techtest.densitypop.service;

import com.happn.agareau.techtest.densitypop.domain.PointOfInterest;
import com.happn.agareau.techtest.densitypop.error.ErrorCode;
import com.happn.agareau.techtest.densitypop.error.ServiceException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class TSVService {

    private static final Logger logger = LoggerFactory.getLogger(TSVService.class);
    private static final String TAB = "\\t";
    private static final int NB_ELEMS_PER_LINE = 3;
    private static final String PREFIX_TEMP_FILE = "temp-file-name";

    public List<PointOfInterest> uploadAndReadTSVFileAndReturnListPOI(MultipartFile file) {
        try {
            File temp = File.createTempFile(PREFIX_TEMP_FILE + LocalDateTime.now(), ".tsv");
            temp.deleteOnExit();
            file.transferTo(temp);
            return createListPOIFromFile(temp);
        } catch (IOException ioex) {
            throw new ServiceException(ErrorCode.UPLOAD_ERROR, file.getName(), ioex);
        }
    }

    //public for testing purpose
    public List<PointOfInterest> createListPOIFromFile(File file) {

        return readTSVFile(file)
                .stream()
                .skip(1)
                .map(mapLineToPOI)
                .collect(Collectors.toList());
    }

    private List<String> readTSVFile(File file) {
        List<String> lines;
        try (Stream<String> streamedLines = Files.lines(file.toPath())) {
            lines = streamedLines
                    .filter(line -> !line.isEmpty())
                    .filter(isLineCorrect)
                    .collect(Collectors.toList());
        } catch (IOException ioex) {
            throw new ServiceException(ErrorCode.READING_FILE_ERROR, file.getName(), ioex);
        }
        return lines;

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
