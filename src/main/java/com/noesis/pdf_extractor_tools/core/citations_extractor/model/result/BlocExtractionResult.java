package com.noesis.pdf_extractor_tools.core.citations_extractor.model.result;

import java.util.List;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.text.Line;

public record BlocExtractionResult(List<BlocCitation> citation, List<Line> lines) {

}
