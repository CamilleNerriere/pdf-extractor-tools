package com.noesis.pdf_extractor_tools.core.annotations_extractor.exporter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noesis.pdf_extractor_tools.core.annotations_extractor.model.Annotation;
import com.noesis.pdf_extractor_tools.core.common.ExportedFile;
import com.noesis.pdf_extractor_tools.core.exception.ExportException;

public class WordAnnotationExporter implements IAnnotationExporter {

    private final LinkedHashMap<Integer, List<Annotation>> annotations;
    private final String title;
    private static final Logger logger = LoggerFactory.getLogger(WordAnnotationExporter.class);

    public WordAnnotationExporter(LinkedHashMap<Integer, List<Annotation>> annotations, String title) {
        this.annotations = annotations;
        this.title = title;
    }

    @Override
    public ExportedFile export() throws IOException, ExportException {
        String fileName = generatePathName(title);
        Path tempFile = null;
        XWPFDocument document = null;
        FileOutputStream out = null;

        try {
            document = new XWPFDocument();
            
            addTitleParagraph(document, title);
            
            addEmptyParagraph(document);
            
            for (Integer pageNumber : annotations.keySet()) {
                List<Annotation> pageAnnotations = annotations.get(pageNumber);
                
                if (pageAnnotations != null && !pageAnnotations.isEmpty()) {
                    addPageSubtitle(document, "Page " + pageNumber);
                    
                    for (Annotation annotation : pageAnnotations) {
                        addAnnotationParagraph(document, annotation);
                    }
                }
                
                addEmptyParagraph(document);
            }
            
            tempFile = Files.createTempFile(title, ".docx");
            
            out = new FileOutputStream(tempFile.toFile());
            document.write(out);
            
            logger.info("Docx export completed successfully");
            
            return new ExportedFile(fileName, tempFile);
            
        } catch (IOException e) {
            logger.error("Error during word export", e);
            
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                    logger.info("Temporary file deleted after failure: {}", tempFile);
                } catch (IOException ex) {
                    logger.warn("Failed to delete temporary file: {}", tempFile, ex);
                }
            }
            
            throw new ExportException("Unable to export WORD for Annotation extraction");
            
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (document != null) {
                    document.close();
                }
            } catch (IOException e) {
                logger.warn("Error closing resources", e);
            }
        }
    }

    private void addTitleParagraph(XWPFDocument document, String titleText) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        titleParagraph.setSpacingAfter(240); 
        
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(titleText);
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        titleRun.setFontFamily("Arial");
    }

    private void addPageSubtitle(XWPFDocument document, String pageTitle) {
        XWPFParagraph subtitleParagraph = document.createParagraph();
        subtitleParagraph.setSpacingBefore(240);
        subtitleParagraph.setSpacingAfter(120);  
        
        XWPFRun subtitleRun = subtitleParagraph.createRun();
        subtitleRun.setText(pageTitle);
        subtitleRun.setBold(true);
        subtitleRun.setFontSize(14);
        subtitleRun.setColor("0066CC"); 
        subtitleRun.setFontFamily("Arial");
    }

    private void addAnnotationParagraph(XWPFDocument document, Annotation annotation) {
        String content = annotation.getContent();
        
        if (content != null && !content.trim().isEmpty()) {
            XWPFParagraph annotationParagraph = document.createParagraph();
            
            // Créer l'indentation pour simuler une liste à puces
            annotationParagraph.setIndentationLeft(720);   // Indentation gauche
            annotationParagraph.setIndentationHanging(360); // Indentation suspendue
            
            XWPFRun annotationRun = annotationParagraph.createRun();
            annotationRun.setText("• " + content);
            annotationRun.setFontFamily("Arial");
            annotationRun.setFontSize(11);
        }
    }

    private void addEmptyParagraph(XWPFDocument document) {
        document.createParagraph();
    }

    private String generatePathName(String title) {
        return title.replaceAll("\\s", "_") + ".docx";
    }
}