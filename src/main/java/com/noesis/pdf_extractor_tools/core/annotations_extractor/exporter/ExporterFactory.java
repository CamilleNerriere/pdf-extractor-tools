package com.noesis.pdf_extractor_tools.core.annotations_extractor.exporter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.noesis.pdf_extractor_tools.core.annotations_extractor.model.Annotation;



public class ExporterFactory {
    public List<IAnnotationExporter> getExporter(LinkedHashMap<Integer, List<Annotation>> annotations, String[] formats, String title) {
        List<String> uniqueFormats = removeRedondantFormat(formats);
        return getAskedExporters(uniqueFormats, annotations, title);

    }

    private List<String> removeRedondantFormat(String formats[]) {
        return Arrays.stream(formats).map(String::toLowerCase).distinct().toList();
    }

    private List<IAnnotationExporter> getAskedExporters(List<String> uniqueFormats,
            LinkedHashMap<Integer, List<Annotation>> annotations, String title) {

        List<IAnnotationExporter> exporters = new ArrayList<>();

        for (String format : uniqueFormats) {
            if (format == null)
                return null;
            IAnnotationExporter exporter = switch (format) {
                case "word" -> new WordAnnotationExporter(annotations, title);
                case "pdf" -> new PdfAnnotationExporter(annotations, title);
                default -> throw new IllegalArgumentException("Unknown format : " + format);
            };
            exporters.add(exporter);
        }

        return exporters;
    }

}
