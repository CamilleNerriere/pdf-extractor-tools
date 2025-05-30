package com.noesis.pdf_extractor_tools.core.citations_extractor.model.result;

import java.util.LinkedHashMap;
import java.util.List;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedHarvardCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitationWithNote;

public record AllTypeCitationsResult(LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations,
        LinkedHashMap<Integer, List<TradCitationWithNote>> tradCitations,
        LinkedHashMap<Integer, List<BlocCitationWithNote>> blocCitations) {

}
