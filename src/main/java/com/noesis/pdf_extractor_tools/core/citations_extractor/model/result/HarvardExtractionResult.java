package com.noesis.pdf_extractor_tools.core.citations_extractor.model.result;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedHarvardCitation;

public record HarvardExtractionResult(AnnotatedHarvardCitation citation, String truncContent, String truncOpeningQuote) {}


