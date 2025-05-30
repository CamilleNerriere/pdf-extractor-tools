package com.noesis.pdf_extractor_tools.core.citations_extractor.model.result;

import java.util.List;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TroncatedCitation;

public record TradCitationExtractionResult(List<TradCitation> citations, TroncatedCitation troncatedCitation) {

}
