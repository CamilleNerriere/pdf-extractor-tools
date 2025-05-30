package com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation;

public class AnnotatedBlocCitation {
    private final BlocCitation baseCitation;
    private final String noteNumberAString;

    public AnnotatedBlocCitation(final BlocCitation baseCitation, final String noteNumberAString){
        this.baseCitation = baseCitation;
        this.noteNumberAString = noteNumberAString;
    }

    public BlocCitation getBaseCitation(){
        return baseCitation;
    }

    public String getNoteNumber(){
        return noteNumberAString;
    }

    @Override
    public String toString() {
        return "Citation{" +
                "text='" + baseCitation.getText() + '\'' +
                ", page=" + baseCitation.getPage() +
                ", endX=" + baseCitation.getXEnd() +
                ", endY=" + baseCitation.getYEnd() +
                ", note=" + noteNumberAString + 
                '}';
    }
}
