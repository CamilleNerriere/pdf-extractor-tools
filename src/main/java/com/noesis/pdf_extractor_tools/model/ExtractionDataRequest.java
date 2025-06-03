package com.noesis.pdf_extractor_tools.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.noesis.pdf_extractor_tools.core.common.ExportFormats;

import lombok.Data;

@Data
public class ExtractionDataRequest {
    
    private final String title;
    private final List<ExportFormats> formats;
    private final MultipartFile pdf;

    public ExtractionDataRequest(final String title, final List<ExportFormats> formats, final MultipartFile pdf){
        this.title = title;
        this.formats = formats;
        this.pdf = pdf;
    }
}
