package com.noesis.pdf_extractor_tools.core.citations_extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.noesis.pdf_extractor_tools.core.citations_extractor.exporter.ExportedFile;
import com.noesis.pdf_extractor_tools.core.common.ExportFormats;


public interface ICoreCitationsExtractorService {
    List<ExportedFile> extract(InputStream pdfInput, List<ExportFormats> formats, String title) throws IOException;
}
