package com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.citation.harvard;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TroncatedCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExtractionContext;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.result.HarvardCitationExtractionResult;

public interface IHarvardCitationExtractor {
        HarvardCitationExtractionResult extractCitationsPerPage(ExtractionContext context,
                        TroncatedCitation troncatedCitationFromLastPage);
}
