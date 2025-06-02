package com.noesis.pdf_extractor_tools.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.noesis.pdf_extractor_tools.model.ExtractionDataRequest;
import com.noesis.pdf_extractor_tools.service.AnnotationsService;
import com.noesis.pdf_extractor_tools.service.CitationsService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class CitationController {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationsController.class);

    @Autowired
    private CitationsService citationsService;

    @GetMapping("/citations/test")
    @ResponseBody
    public String test() {
        return "OK";
    }

    /**
     * Extract citations from PDF and return as ZIP file
     */
    @PostMapping(path = "/extract/citations", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public void extractAnnotations(@ModelAttribute ExtractionDataRequest annotationDataRequest,
            HttpServletResponse response) throws IOException {

        try {
            citationsService.extractCitations(annotationDataRequest, response);
        } catch (IOException e) {
            try {
                if (!response.isCommitted()) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during file treatment");
                }
            } catch (IOException ioException) {
                logger.error("Unable to send Http response", ioException);
            }

        } catch (Exception e) {
            logger.error("Unable to send Http response", e);

            try {
                if (!response.isCommitted()) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Internal Server error");
                }
            } catch (IOException ioException) {
                logger.error("Unable to send Http response", ioException);
            }
        }
    }
}
