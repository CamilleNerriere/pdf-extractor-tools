package com.noesis.pdf_extractor_tools.core.citations_extractor.model.context;

import java.util.List;

import org.apache.pdfbox.text.TextPosition;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.result.LineCoordStatsResult;

public class ExtractionContext {
    private final List<TextPosition> positions;
    private final int page;
    private final float averageFontSize;
    private final float medianFontSize;
    private final LineCoordStatsResult lineCoordStatsResult;

    public ExtractionContext(final List<TextPosition> positions, final int page, final float averageFontSize, final float medianFontSize, final LineCoordStatsResult lineCoordStatsResult) {
        this.positions = positions;
        this.page = page;
        this.averageFontSize = averageFontSize;
        this.medianFontSize = medianFontSize;
        this.lineCoordStatsResult = lineCoordStatsResult;
    }

    public List<TextPosition> getPositions() {
        return positions;
    }

    public int getPage() {
        return page;
    }

    public float getAverageFontSize() {
        return averageFontSize;
    }

    public float getMedianFontSize() {
        return medianFontSize;
    }

    public LineCoordStatsResult getLineCoordStatsResult(){
        return lineCoordStatsResult;
    }

}
