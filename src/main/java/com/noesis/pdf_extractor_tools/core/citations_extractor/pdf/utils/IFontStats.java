package com.noesis.pdf_extractor_tools.core.citations_extractor.pdf.utils;

import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.text.TextPosition;

public interface IFontStats {
    float getAverageFontSize(List<TextPosition> positions);
    Optional<Float> getMedianSize(List<TextPosition> positions);
}
