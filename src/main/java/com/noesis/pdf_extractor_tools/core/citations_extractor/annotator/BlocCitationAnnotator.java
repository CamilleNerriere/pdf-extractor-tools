package com.noesis.pdf_extractor_tools.core.citations_extractor.annotator;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedBlocCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitation;

public class BlocCitationAnnotator extends GenericCitationAnnotator<BlocCitation, AnnotatedBlocCitation> implements ICitationAnnotator<BlocCitation, AnnotatedBlocCitation> {
    @Override
    protected float getXEnd(BlocCitation citation) {
        return citation.getXEnd();
    }

    @Override
    protected float getYEnd(BlocCitation citation) {
        return citation.getYEnd();
    }

    @Override
    protected AnnotatedBlocCitation createAnnotated(BlocCitation citation, String noteText) {
        return new AnnotatedBlocCitation(citation, noteText);
    }
}
