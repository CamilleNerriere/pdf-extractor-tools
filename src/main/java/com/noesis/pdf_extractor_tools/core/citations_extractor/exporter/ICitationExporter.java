package com.noesis.pdf_extractor_tools.core.citations_extractor.exporter;

import java.io.IOException;

public interface ICitationExporter {
    ExportedFile export() throws IOException;
}
