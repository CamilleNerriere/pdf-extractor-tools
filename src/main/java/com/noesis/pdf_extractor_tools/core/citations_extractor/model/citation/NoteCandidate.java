package com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation;

public class NoteCandidate {
    private final String text;
    private final int page;
    private final float x;
    private final float y;
    private final String styleType;

    public NoteCandidate(final String text, final int page, final float x, final float y, final String styleType) {
        this.text = text;
        this.page = page;
        this.x = x;
        this.y = y;
        this.styleType = styleType;
    }

    public String getText() {
        return text;
    }

    public int getPage(){
        return page;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getStyleType(){
        return styleType;
    }

    @Override
    public String toString() {
        return "NoteCandidate{" +
                "text='" + text + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", styleType=" + styleType +
                '}';
    }
}

