package com.happn.agareau.techtest.densitypop.error;

import io.vavr.API;
import io.vavr.collection.Seq;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.vavr.API.Seq;
import static java.lang.String.format;

@AllArgsConstructor
public abstract class Error {
    private static final Logger logger = LoggerFactory.getLogger(Error.class);
    protected final Throwable throwable;

    public abstract String getMessage();

    public static class ReadFileError extends Error {

        private final String name;

        public ReadFileError(String name, Throwable throwable) {
            super(throwable);
            this.name = name;
        }

        @Override
        public String getMessage() {
            return format("Unable to read file : %s %s", name, throwable);
        }
    }

    public static class NbZonesNegError extends Error {

        private final int nbZones;

        public NbZonesNegError(int nbZones, Throwable throwable) {
            super(throwable);
            this.nbZones = nbZones;
        }

        @Override
        public String getMessage() {
            return format("Number of zones cannot be negative: %d %s", nbZones, throwable);
        }
    }

    public static class CreatePOIEror extends Error {

        private final String name;

        public CreatePOIEror(String name, Throwable throwable) {
            super(throwable);
            this.name = name;
        }

        @Override
        public String getMessage() {
            return format("Unable to create POI : %s %s", name, throwable);
        }
    }


    public static class UploadFileError extends Error {

        private final String name;

        public UploadFileError(String name, Throwable throwable) {
            super(throwable);
            this.name = name;
        }

        @Override
        public String getMessage() {
            return format("Unable to upload file : %s %s", name, throwable);
        }
    }

    public Seq<Error> logThenBuildSeqError() {
        logger.error(getMessage());
        return Seq(this);
    }
}
