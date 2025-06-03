package com.noesis.pdf_extractor_tools.validation;

import org.springframework.web.multipart.MultipartFile;

public class PdfCommonValidator {

    private static final String MIME_TYPE = "application/pdf";

    public static void validateCommonPdfCriteria(MultipartFile file) {
        if (!isPdf(file)) {
            throw new IllegalArgumentException("Illegal file extension. Only .pdf files are allowed.");
        }

        if (!hasValidMimeType(file)) {
            throw new IllegalArgumentException("Incorrect MIME Type");
        }
    }

    public static boolean isPdf(MultipartFile file) {
        String name = file.getOriginalFilename();
        return name != null && name.toLowerCase().endsWith(".pdf");
    }

    public static boolean hasValidMimeType(MultipartFile file) {
        String fileContentType = file.getContentType();
        if (fileContentType != null) {

            return fileContentType.equalsIgnoreCase(MIME_TYPE);
        }
        return false;
    }

    public static void validateSize(MultipartFile file, long maxSizeMb) {
        long size = file.getSize();
        if (size < 0 || size > maxSizeMb * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "The uploaded file exceeds the maximum allowed size of " + maxSizeMb + " MB.");
        }
    }
}
