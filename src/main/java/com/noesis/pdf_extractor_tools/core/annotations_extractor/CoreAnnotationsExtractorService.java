package com.noesis.pdf_extractor_tools.core.annotations_extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.noesis.pdf_extractor_tools.core.annotations_extractor.exporter.ExporterFactory;
import com.noesis.pdf_extractor_tools.core.annotations_extractor.exporter.IAnnotationExporter;
import com.noesis.pdf_extractor_tools.core.annotations_extractor.extractor.Extractor;
import com.noesis.pdf_extractor_tools.core.annotations_extractor.model.Annotation;
import com.noesis.pdf_extractor_tools.core.common.ExportFormats;
import com.noesis.pdf_extractor_tools.core.common.ExportedFile;

@Component
public class CoreAnnotationsExtractorService implements ICoreAnnotationsExtractorService {

    private static final Logger logger = LoggerFactory.getLogger(CoreAnnotationsExtractorService.class);

    @Override
    public List<ExportedFile> extract(InputStream pdfInput, List<ExportFormats> formats, String title)
            throws IOException {
        LinkedHashMap<Integer, List<Annotation>> extractedAnnotations = getAnnotations(pdfInput);

        if(extractedAnnotations != null && !extractedAnnotations.isEmpty()){
            return getExtractionResult(extractedAnnotations, formats, title);
        }
        return null;
    }

    private LinkedHashMap<Integer, List<Annotation>> getAnnotations(InputStream pdfInput) {
        try (RandomAccessRead rar = new RandomAccessReadBuffer(pdfInput);
                PDDocument document = Loader.loadPDF(rar)) {
            Extractor extractor = new Extractor();
            return extractor.getAnnotations(document);
        } catch (Exception e) {
            logger.error("Error during citations extraction.");
            return null;
        }
    }

    private List<ExportedFile> getExtractionResult(LinkedHashMap<Integer, List<Annotation>> annotations,
            List<ExportFormats> formats, String title) throws IOException {

        String exportTitle = title == null ? "Annotations" : title;
        ExporterFactory exporterFactory = new ExporterFactory();

        List<IAnnotationExporter> exporters = exporterFactory.getExporter(annotations, formats, exportTitle);
        
        List<ExportedFile> exportedFiles = new ArrayList<>();
        for (IAnnotationExporter exporter : exporters) {
            exportedFiles.add(exporter.export());
        }

        return exportedFiles;
    }
}