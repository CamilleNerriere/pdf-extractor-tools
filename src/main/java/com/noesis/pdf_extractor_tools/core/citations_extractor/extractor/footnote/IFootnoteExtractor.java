package com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.footnote;

import java.util.LinkedHashMap;
import java.util.List;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.NoteCandidate;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExtractionContext;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.footnote.Footnote;


public interface IFootnoteExtractor {

    List<Footnote> getFootnotes(
         ExtractionContext context,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage);
}
