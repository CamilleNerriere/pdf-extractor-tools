package com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation;

public class AnnotatedHarvardCitation {
    private final TradCitation baseCitation;
    private final String noteContent;

    public AnnotatedHarvardCitation(final TradCitation baseCitation, final String noteContent){
        this.baseCitation = baseCitation;
        this.noteContent = noteContent;
    }

    public TradCitation getBaseCitation(){
        return baseCitation;
    }

    public String getContent(){
        return baseCitation.getText();
    }

    public String getNoteContent(){
        return noteContent;
    }

    @Override
    public String toString() {
        return "Citation{" +
                "text='" + baseCitation.getText() + '\'' +
                ", page=" + baseCitation.getPage() +
                ", endX=" + baseCitation.getXEnd() +
                ", endY=" + baseCitation.getYEnd() +
                ", note=" + noteContent + 
                '}';
    }
}
