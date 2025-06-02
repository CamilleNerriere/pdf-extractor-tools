package com.noesis.pdf_extractor_tools.core.common;

import java.util.Optional;

public enum ExportFormats {
    PDF, TXT, WORD;

    public static boolean isValid(String format) {
        try {
            ExportFormats.valueOf(format.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static Optional<ExportFormats> fromString(String format){
        try {
            return Optional.of(ExportFormats.valueOf(format.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
