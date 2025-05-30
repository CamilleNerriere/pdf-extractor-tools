package com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation;

import org.apache.pdfbox.text.TextPosition;

public class BlocCitation {
    private final String text;
    private final int page;
    private final TextPosition startPos; // first char position
    private final TextPosition endPos; // last char lposition

    public BlocCitation(final String text, final int page, final TextPosition startPos, final TextPosition endPos) {
        this.text = text;
        this.page = page;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public String getText() {
        return text;
    }

    public int getPage() {
        return page;
    }

    public TextPosition getStartPos() {
        return startPos;
    }

    public TextPosition getEndPos() {
        return endPos;
    }

    public float getXEnd() {
        return endPos.getXDirAdj() + endPos.getWidthDirAdj();
    }

    public float getYEnd() {
        return endPos.getYDirAdj();
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
