package com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation;

public class TradCitationWithNote {
    private final AnnotatedTradCitation baseAnnotatedCitation;
    private final String footnote;

    public TradCitationWithNote(final AnnotatedTradCitation baseAnnotatedCitation, final String footnote){
        this.baseAnnotatedCitation = baseAnnotatedCitation;
        this.footnote = footnote;
    }

    public AnnotatedTradCitation getBaseAnnotatedCitation(){
        return baseAnnotatedCitation;
    }

    public String getContent(){
        return baseAnnotatedCitation.getBaseCitation().getText();
    }

    public String getNoteNumber(){
        return baseAnnotatedCitation.getNoteNumber();
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
