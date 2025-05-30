package com.noesis.pdf_extractor_tools.core.citations_extractor.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class CustomTextStripper extends PDFTextStripper{
    private final List<TextPosition> positions = new ArrayList<>();

    public CustomTextStripper() throws IOException {
        super();
        setSortByPosition(true); 
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        positions.add(text);
    }

    public List<TextPosition> getTextPositions() {
        return positions;
    }

    public void clearPositions() {
        positions.clear();
    }
}
