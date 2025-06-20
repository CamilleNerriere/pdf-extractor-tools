package com.noesis.pdf_extractor_tools.validation.extractor.pdfFile;

import org.springframework.web.multipart.MultipartFile;

public class AnnotationPdfValidator {
    private static final long MAX_SIZE_MB = 100;

    public static void validate(MultipartFile file) {
        PdfCommonValidator.validateCommonPdfCriteria(file);
        PdfCommonValidator.validateSize(file, MAX_SIZE_MB);
    }
}
