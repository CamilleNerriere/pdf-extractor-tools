package com.noesis.pdf_extractor_tools.core.citations_extractor.footnoteAssociator;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedBlocCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.footnote.Footnote;

public class BlocCitationFootnoteAssociator
        extends GenericFootnoteAssociator<BlocCitationWithNote, AnnotatedBlocCitation, Footnote>
        implements IFootnoteAssociator<BlocCitationWithNote, AnnotatedBlocCitation, Footnote> {
    @Override
    protected String getNoteNumberFromCitation(AnnotatedBlocCitation citation) {
        return citation.getNoteNumber();
    }

    @Override
    protected String getNoteNumberFromFootnote(Footnote footnote) {
        return footnote.getNoteNumber();
    }

    @Override
    protected String getTextFromFootnote(Footnote footnote) {
        return footnote.getText();
    }

    @Override
    protected BlocCitationWithNote createCitationWithFootnote(AnnotatedBlocCitation citation, String footnoteText) {
        return new BlocCitationWithNote(citation, footnoteText);
    }
}
