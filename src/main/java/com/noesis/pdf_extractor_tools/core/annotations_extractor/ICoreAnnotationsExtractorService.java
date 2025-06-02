package com.noesis.pdf_extractor_tools.core.annotations_extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.noesis.pdf_extractor_tools.core.common.ExportFormats;
import com.noesis.pdf_extractor_tools.core.common.ExportedFile;

public interface ICoreAnnotationsExtractorService {
    List<ExportedFile> extract(InputStream pdfInput, List<ExportFormats> formats, String title) throws IOException;
}
