package com.noesis.pdf_extractor_tools.core.citations_extractor.annotator;

import java.util.LinkedHashMap;
import java.util.List;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.NoteCandidate;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExtractionContext;

public interface ICitationAnnotator<C, A> {
    List<A> getAnnotatedCitations(
        LinkedHashMap<Integer, List<C>> citationsCandidatesPerPage,
        LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage,
        ExtractionContext context
    );
}