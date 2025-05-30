package com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.note;

import java.util.List;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.NoteCandidate;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExtractionContext;

public interface INoteDetector {
    List<NoteCandidate> getNoteCandidates(ExtractionContext context);
    List<NoteCandidate> getTradNoteCandidates(ExtractionContext context);
    List<NoteCandidate> getHarvardNoteCandidates(ExtractionContext context);
}
