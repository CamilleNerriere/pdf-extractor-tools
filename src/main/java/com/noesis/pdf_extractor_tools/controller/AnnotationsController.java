package com.noesis.pdf_extractor_tools.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.noesis.pdf_extractor_tools.config.SpecificLimitRates;
import com.noesis.pdf_extractor_tools.core.common.ExportFormats;
import com.noesis.pdf_extractor_tools.mapper.FormatNormalizer;
import com.noesis.pdf_extractor_tools.model.ExtractionDataRequest;
import com.noesis.pdf_extractor_tools.service.AnnotationsService;
import com.noesis.pdf_extractor_tools.validation.extractor.fields.FormatsValidator;
import com.noesis.pdf_extractor_tools.validation.extractor.fields.PdfTitleValidator;
import com.noesis.pdf_extractor_tools.validation.extractor.pdfFile.AnnotationPdfValidator;
import com.noesis.pdf_extractor_tools.web.utils.HttpResponseUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/extract")
public class AnnotationsController {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationsController.class);

    @Autowired
    private AnnotationsService annotationsService;

    @Autowired
    private SpecificLimitRates bucket;

    /**
     * Extract annotations from PDF and return as ZIP file
     */
    @PostMapping(path = "/annotations", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public void extractAnnotations(@RequestParam("file") MultipartFile file,
            @RequestParam("formats") List<String> formats,
            @RequestParam("title") String title,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {


        try {

            if (!bucket.annotationBucket.tryConsume(1)) {
                logger.warn("Rate limit exceeded on /extract/annotation");
                HttpResponseUtils.sendRateLimitExceeded(response, 60);
                return;
            }

            AnnotationPdfValidator.validate(file);
            PdfTitleValidator.validate(title);
            FormatsValidator.validate(formats);

            List<ExportFormats> normalizedFormats = FormatNormalizer.normalizeFormats(formats);

            ExtractionDataRequest extractionDataRequest = new ExtractionDataRequest(title, normalizedFormats, file);

            annotationsService.extractAnnotations(extractionDataRequest, response);

        } catch (IllegalArgumentException e) {
            logger.warn("Validation Error : {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Illegal arguments");

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
