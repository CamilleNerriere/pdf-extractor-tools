package com.noesis.pdf_extractor_tools.core.citations_extractor.model.result;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitation;

public record BlocCitationExtractionResult(BlocCitation citations, StringBuilder troncatedCitation) {

}
