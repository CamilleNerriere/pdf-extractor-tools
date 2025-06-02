package com.noesis.pdf_extractor_tools.core.citations_extractor.exporter;

import java.io.IOException;

import com.noesis.pdf_extractor_tools.core.common.ExportedFile;

public interface ICitationExporter {
    ExportedFile export() throws IOException;
}
