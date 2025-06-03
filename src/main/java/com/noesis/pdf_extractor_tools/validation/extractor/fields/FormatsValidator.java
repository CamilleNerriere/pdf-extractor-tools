package com.noesis.pdf_extractor_tools.validation.extractor.fields;

import java.util.List;

public class FormatsValidator {
    public static void validate(List<String> formats) {
        if (formats == null || formats.isEmpty()) {
            throw new IllegalArgumentException("No format specification. At least one export format must be provided.");
        }
    }
}
