package com.noesis.pdf_extractor_tools.validation;

import org.springframework.web.multipart.MultipartFile;

public class CitationPdfValidator {
    private static final long MAX_SIZE_MB = 50;

    public static void validate(MultipartFile file){
        PdfCommonValidator.validateCommonPdfCriteria(file);
        PdfCommonValidator.validateSize(file, MAX_SIZE_MB);
    }
}
