package com.noesis.pdf_extractor_tools.core.annotations_extractor.exporter;

import java.io.IOException;

import com.noesis.pdf_extractor_tools.core.common.ExportedFile;
import com.noesis.pdf_extractor_tools.core.exception.ExportException;

public interface IAnnotationExporter {
    ExportedFile export() throws IOException, ExportException;
}
