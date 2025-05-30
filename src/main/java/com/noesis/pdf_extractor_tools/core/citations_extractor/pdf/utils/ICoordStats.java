package com.noesis.pdf_extractor_tools.core.citations_extractor.pdf.utils;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.result.LineCoordStatsResult;

public interface ICoordStats {
    LineCoordStatsResult getLineCoordStats(PDDocument document) throws IOException;
}
