package com.noesis.pdf_extractor_tools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noesis.pdf_extractor_tools.core.annotations_extractor.CoreAnnotationsExtractorService;
import com.noesis.pdf_extractor_tools.model.ExtractionDataRequest;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class AnnotationsService {

    @Autowired
    CoreAnnotationsExtractorService annotationsExtractorService;

    @Autowired
    private GenericExtractionService genericExtractionService;

    public void extractAnnotations(ExtractionDataRequest request, HttpServletResponse response) throws Exception {

        genericExtractionService.extractAndSendFiles(request, response,
                (pdf, formats, title) -> annotationsExtractorService.extract(pdf, formats, title));
    }

}
