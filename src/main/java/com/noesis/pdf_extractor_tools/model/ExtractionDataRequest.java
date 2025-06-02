package com.noesis.pdf_extractor_tools.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ExtractionDataRequest {
    private String title;
    private String[] formats;
    private MultipartFile pdf;
}
