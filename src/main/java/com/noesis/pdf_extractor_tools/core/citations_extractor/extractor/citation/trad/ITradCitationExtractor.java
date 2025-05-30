package com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.citation.trad;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TroncatedCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExtractionContext;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.result.TradCitationExtractionResult;

public interface ITradCitationExtractor {
    TradCitationExtractionResult extractCitationsPerPage(ExtractionContext context,
            TroncatedCitation troncatedCitationFromLastPage);

}
