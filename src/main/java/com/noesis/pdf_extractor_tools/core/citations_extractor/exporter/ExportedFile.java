package com.noesis.pdf_extractor_tools.core.citations_extractor.exporter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public record ExportedFile(String fileName, Path path) {
    public InputStream openStream() throws IOException {
        return Files.newInputStream(path);
    }

    public void deleteTempFile() throws IOException {
        Files.deleteIfExists(path);
    }
}
