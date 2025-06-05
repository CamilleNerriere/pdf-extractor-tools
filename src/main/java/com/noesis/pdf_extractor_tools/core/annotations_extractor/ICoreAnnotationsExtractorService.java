package com.noesis.pdf_extractor_tools.core.annotations_extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.noesis.pdf_extractor_tools.core.common.ExportFormats;
import com.noesis.pdf_extractor_tools.core.common.ExportedFile;
import com.noesis.pdf_extractor_tools.core.exception.ExportException;
import com.noesis.pdf_extractor_tools.core.exception.ExtractException;
import com.noesis.pdf_extractor_tools.core.exception.MissingArgumentException;

public interface ICoreAnnotationsExtractorService {
    List<ExportedFile> extract(InputStream pdfInput, List<ExportFormats> formats, String title)
            throws IOException, ExtractException, MissingArgumentException, ExportException;
}
