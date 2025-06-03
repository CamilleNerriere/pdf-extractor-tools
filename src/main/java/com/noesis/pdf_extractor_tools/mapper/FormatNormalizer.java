package com.noesis.pdf_extractor_tools.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.noesis.pdf_extractor_tools.core.common.ExportFormats;

public class FormatNormalizer {
    public static List<ExportFormats> normalizeFormats(List<String> formats) {

        List<ExportFormats> normalizedFormats = new ArrayList<>();

        for (String format : formats) {
            Optional<ExportFormats> op = ExportFormats.fromString(format);

            if (op.isPresent()) {
                normalizedFormats.add(op.get());
            }
        }

        if (!normalizedFormats.isEmpty()) {
            return normalizedFormats;
        }

        throw new IllegalArgumentException("No valid format found");

    }

}
