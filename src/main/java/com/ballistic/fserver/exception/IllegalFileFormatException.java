package com.ballistic.fserver.exception;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;


public class IllegalFileFormatException extends RuntimeException {

    public IllegalFileFormatException(String error, String... fileFormats) {
        super(IllegalFileFormatException.generateMessage(error, fileFormats));
    }

    private static String generateMessage(String error, String... fileFormats) {
        StringBuilder formate = new StringBuilder();
        Arrays.stream(fileFormats).forEach(fileType -> { formate.append(fileType+",");});
        return StringUtils.capitalize(error) + " supported format are " + formate;
    }

}
