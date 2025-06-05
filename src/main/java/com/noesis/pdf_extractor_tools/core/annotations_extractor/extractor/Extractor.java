package com.noesis.pdf_extractor_tools.core.annotations_extractor.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noesis.pdf_extractor_tools.core.annotations_extractor.model.Annotation;
import com.noesis.pdf_extractor_tools.core.exception.ExtractException;

public class Extractor {
    private static final Logger logger = LoggerFactory.getLogger(Extractor.class);

    public LinkedHashMap<Integer, List<Annotation>> getAnnotations(PDDocument document) throws IOException, ExtractException {
        try (document) {
            int pageNum = 1;
            LinkedHashMap<Integer, List<Annotation>> allAnnotations = new LinkedHashMap<>();

            for (PDPage page : document.getPages()) {
                List<Annotation> annotations = getAnnotationsPerPage(page, pageNum);
                if (annotations != null && !annotations.isEmpty()) {
                    allAnnotations.put(pageNum, annotations);
                }

                pageNum++;
            }
            return allAnnotations;
        } catch (Exception e) {
            logger.error("Error during extractor execution", e);
            throw new ExtractException();
        }
    }

    private List<Annotation> getAnnotationsPerPage(PDPage page, int pageNum) throws IOException, ExtractException {
        try {
            List<PDAnnotation> annotations = page.getAnnotations();
            List<Annotation> extractedAnnotations = new ArrayList<>();
            for (PDAnnotation annotation : annotations) {
                if ("Highlight".equals(annotation.getSubtype()) || "Text".equals(annotation.getSubtype()) || "FreeText".equals(annotation.getSubtype())) {
                    Annotation annot = new Annotation(pageNum, annotation.getSubtype(), annotation.getContents());
                    extractedAnnotations.add(annot);
                }
            }
            return extractedAnnotations;
        } catch (IOException e) {
            logger.error("Error during page extraction {}", pageNum, e);
        }
        throw new ExtractException();
    }
}
