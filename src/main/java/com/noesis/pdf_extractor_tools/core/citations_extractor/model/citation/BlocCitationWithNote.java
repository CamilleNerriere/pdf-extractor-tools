package com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation;

public class BlocCitationWithNote {
    private final AnnotatedBlocCitation baseAnnotatedCitation;
    private final String footnote;

    public BlocCitationWithNote(final AnnotatedBlocCitation baseAnnotatedCitation, final String footnote){
        this.baseAnnotatedCitation = baseAnnotatedCitation;
        this.footnote = footnote;
    }

    public AnnotatedBlocCitation getBaseAnnotatedCitation(){
        return baseAnnotatedCitation;
    }

    public String getFootnote(){
        return footnote;
    }

    @Override
    public String toString() {
        return "Citation{" +
                "text='" + baseAnnotatedCitation.getBaseCitation().getText() + '\'' +
                ", page=" + baseAnnotatedCitation.getBaseCitation().getPage() +
                ", endX=" + baseAnnotatedCitation.getBaseCitation().getXEnd() +
                ", endY=" + baseAnnotatedCitation.getBaseCitation().getYEnd() +
                ", note=" + baseAnnotatedCitation.getNoteNumber() + 
                ", footnote=" + footnote + 
                '}';
    }
}
