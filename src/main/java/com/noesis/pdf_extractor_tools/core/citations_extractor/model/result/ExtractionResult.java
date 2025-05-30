package com.noesis.pdf_extractor_tools.core.citations_extractor.model.result;

import java.io.InputStream;
import java.util.Map;

public class ExtractionResult {
        private final Map<String, InputStream> files;

    public ExtractionResult(Map<String, InputStream> files) {
        this.files = files;
    }

    public Map<String, InputStream> getFiles() {
        return files;
    }
}
