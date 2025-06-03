package com.noesis.pdf_extractor_tools.validation.extractor.fields;

public class PdfTitleValidator {
    public static void validate(String title) {
        if (title != null && title.length() > 50) {
            throw new IllegalArgumentException("Title exceed max length");
        }
    }
}
