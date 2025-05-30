package com.noesis.pdf_extractor_tools.core.citations_extractor.footnoteAssociator;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedTradCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.footnote.Footnote;

public class TradCitationFootnoteAssociator extends GenericFootnoteAssociator<TradCitationWithNote, AnnotatedTradCitation, Footnote> implements IFootnoteAssociator<TradCitationWithNote, AnnotatedTradCitation, Footnote> {
    @Override
    protected String getNoteNumberFromCitation(AnnotatedTradCitation citation){
        return citation.getNoteNumber();
    }

    @Override
    protected String getNoteNumberFromFootnote(Footnote footnote){
        return footnote.getNoteNumber();
    }

    @Override
    protected String getTextFromFootnote(Footnote footnote){
        return footnote.getText();
    }

    @Override
    protected TradCitationWithNote createCitationWithFootnote(AnnotatedTradCitation citation, String footnoteText){
        return new TradCitationWithNote(citation, footnoteText);
    }
}
