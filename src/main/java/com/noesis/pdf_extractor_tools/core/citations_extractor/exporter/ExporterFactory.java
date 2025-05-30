package com.noesis.pdf_extractor_tools.core.citations_extractor.exporter;

import java.util.ArrayList;
import java.util.List;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExporterContext;
import com.noesis.pdf_extractor_tools.core.common.ExportFormats;

public class ExporterFactory {

    public List<ICitationExporter> getExporter(ExporterContext context, List<ExportFormats> formats) {

        List<ICitationExporter> exporters = new ArrayList<>();

        for (ExportFormats format : formats) {
            if (format == null)
                return null;
            ICitationExporter exporter = switch (format) {
                case TXT -> new TxtCitationExporter(context);
                case WORD -> new WordCitationExporter(context);
                case PDF -> new PdfCitationExporter(context);
                default -> throw new IllegalArgumentException("Unknown format : " + format);
            };
            exporters.add(exporter);
        }

        return exporters;
    }


}
