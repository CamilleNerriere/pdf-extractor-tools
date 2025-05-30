package com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.citation.bloc;

import java.util.List;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExtractionContext;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.result.BlocExtractionResult;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.text.Line;

public interface IBlocCitationExtractor {
    BlocExtractionResult extractCitationsPerPage(ExtractionContext context,
                            List<Line> linesFromLastPage, List<BlocCitation> citationsFromLastPage) ;
}
