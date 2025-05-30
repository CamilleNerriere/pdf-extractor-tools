package com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation;

import org.apache.pdfbox.text.TextPosition;

public class TradCitation {
    private final String text;
    private final int page;
    private final TextPosition startPos; // premier caractère
    private final TextPosition endPos; // dernier caractère
    private final String openingQuote;

    public TradCitation(final String text, final int page, final TextPosition startPos, final TextPosition endPos, final String openingQuote) {
        this.text = text;
        this.page = page;
        this.startPos = startPos;
        this.endPos = endPos;
        this.openingQuote = openingQuote;
    }

    public String getText() {
        return text;
    }

    public int getPage() {
        return page;
    }

    public TextPosition getStartPos(){
        return startPos;
    }

    public TextPosition getEndPos(){
        return endPos;
    }

    public float getXEnd() {
        return endPos.getXDirAdj() + endPos.getWidthDirAdj();
    }

    public float getYEnd() {
        return endPos.getYDirAdj();
    }

    public String getOpeningQuote() {
        return openingQuote;
    }

    @Override
    public String toString() {
        return "Citation{" +
                "text='" + text + '\'' +
                ", page=" + page +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                '}';
    }
}
