package com.noesis.pdf_extractor_tools.core.common;

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
}
