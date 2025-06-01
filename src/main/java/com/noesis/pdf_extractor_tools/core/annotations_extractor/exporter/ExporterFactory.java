package com.noesis.pdf_extractor_tools.core.annotations_extractor.exporter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.noesis.pdf_extractor_tools.core.annotations_extractor.model.Annotation;
import com.noesis.pdf_extractor_tools.core.common.ExportFormats;



public class ExporterFactory {
    public List<IAnnotationExporter> getExporter(LinkedHashMap<Integer, List<Annotation>> annotations, List<ExportFormats> formats, String title) {
        return getAskedExporters(formats, annotations, title);

    }

    private List<IAnnotationExporter> getAskedExporters(List<ExportFormats> formats,
            LinkedHashMap<Integer, List<Annotation>> annotations, String title) {

        List<IAnnotationExporter> exporters = new ArrayList<>();

        for (ExportFormats format : formats) {
            if (format == null)
                return null;
            IAnnotationExporter exporter = switch (format) {
                case TXT -> new TxtAnnotationExporter(annotations, title);
                case WORD -> new WordAnnotationExporter(annotations, title);
                case PDF -> new PdfAnnotationExporter(annotations, title);
                default -> throw new IllegalArgumentException("Unexpected value: " + format);
            };
            exporters.add(exporter);
        }

        return exporters;
    }

}
