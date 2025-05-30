package com.noesis.pdf_extractor_tools.core.citations_extractor.model.footnote;

import org.apache.pdfbox.text.TextPosition;

public class Footnote {
private final String text;
    private final int page;
    private final String noteNumberAsString;
    private final TextPosition startPos; // premier caractère
    private final TextPosition endPos; // dernier caractère

    public Footnote(final String text, final int page, final String noteNumberAsString, final TextPosition startPos, final TextPosition endPos) {
        this.text = text;
        this.page = page;
        this.noteNumberAsString = noteNumberAsString;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public String getText() {
        return text;
    }

    public int getPage() {
        return page;
    }

    public String getNoteNumber(){
        return noteNumberAsString;
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

    @Override
    public String toString() {
        return "Citation{" +
                "text='" + text + '\'' +
                ", page=" + page +
                ", note=" + noteNumberAsString +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                '}';
    }
}
