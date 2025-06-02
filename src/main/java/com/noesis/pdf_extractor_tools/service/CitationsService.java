package com.noesis.pdf_extractor_tools.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noesis.pdf_extractor_tools.core.citations_extractor.CoreCitationsExtractorService;
import com.noesis.pdf_extractor_tools.model.ExtractionDataRequest;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class CitationsService {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationsService.class);

    @Autowired
    CoreCitationsExtractorService citationsExtractorService;

    @Autowired
    private GenericExtractionService genericExtractionService;

    public void extractCitations(ExtractionDataRequest request, HttpServletResponse response) throws Exception {
        genericExtractionService.extractAndSendFiles(request, response,
            (pdf, formats, title) -> citationsExtractorService.extract(pdf, formats, title)
        );
    }

}
