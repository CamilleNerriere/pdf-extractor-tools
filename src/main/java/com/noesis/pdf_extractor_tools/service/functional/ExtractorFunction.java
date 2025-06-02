package com.noesis.pdf_extractor_tools.service.functional;

import java.io.InputStream;
import java.util.List;

import com.noesis.pdf_extractor_tools.core.common.ExportFormats;
import com.noesis.pdf_extractor_tools.core.common.ExportedFile;

@FunctionalInterface
public interface ExtractorFunction {
    List<ExportedFile> extract(InputStream pdf, List<ExportFormats> formats, String title) throws Exception;
}