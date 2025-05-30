package com.noesis.pdf_extractor_tools.core.citations_extractor.model.result;

import java.util.List;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedHarvardCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TroncatedCitation;

public record HarvardCitationExtractionResult(List<AnnotatedHarvardCitation> harvardCitations, TroncatedCitation troncatedCitation) {

}
