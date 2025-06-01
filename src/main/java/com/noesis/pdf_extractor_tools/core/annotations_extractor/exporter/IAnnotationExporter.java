package com.noesis.pdf_extractor_tools.core.annotations_extractor.exporter;

import java.io.IOException;

import com.noesis.pdf_extractor_tools.core.citations_extractor.exporter.ExportedFile;

public interface IAnnotationExporter {
    ExportedFile export() throws IOException;
}
