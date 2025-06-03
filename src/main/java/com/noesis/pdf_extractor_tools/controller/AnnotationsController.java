package com.noesis.pdf_extractor_tools.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.noesis.pdf_extractor_tools.core.common.ExportFormats;
import com.noesis.pdf_extractor_tools.mapper.FormatNormalizer;
import com.noesis.pdf_extractor_tools.model.ExtractionDataRequest;
import com.noesis.pdf_extractor_tools.service.AnnotationsService;
import com.noesis.pdf_extractor_tools.validation.extractor.fields.FormatsValidator;
import com.noesis.pdf_extractor_tools.validation.extractor.fields.PdfTitleValidator;
import com.noesis.pdf_extractor_tools.validation.extractor.pdfFile.AnnotationPdfValidator;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AnnotationsController {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationsController.class);

    @Autowired
    private AnnotationsService annotationsService;

    /**
     * Extract annotations from PDF and return as ZIP file
     */
    @PostMapping(path = "/extract/annotations", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public void extractAnnotations(@RequestParam("file") MultipartFile file,
            @RequestParam("formats") List<String> formats,
            @RequestParam("title") String title,
            HttpServletResponse response) throws IOException {

        try {

            AnnotationPdfValidator.validate(file);
            PdfTitleValidator.validate(title);
            FormatsValidator.validate(formats);

            List<ExportFormats> normalizedFormats = FormatNormalizer.normalizeFormats(formats);
        
            ExtractionDataRequest extractionDataRequest = new ExtractionDataRequest(title, normalizedFormats, file);

            annotationsService.extractAnnotations(extractionDataRequest, response);
        } catch (Exception e) {
            handleError(response);
        }
    }

    private void handleError(HttpServletResponse response) {
        try {
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server error");
            }
        } catch (IOException ioException) {
            logger.error("Unable to send HTTP response", ioException);
        }
    }
}
