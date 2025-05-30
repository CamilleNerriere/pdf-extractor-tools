package com.noesis.pdf_extractor_tools.core.citations_extractor.model.result;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TroncatedCitation;

public record OnePotentialCitationResult(TradCitation citation, TroncatedCitation trunc, int lastIndex) {
    public boolean citationIsEmpty() {
        return citation == null || citation.getText() == null || citation.getText().isEmpty();
    }
}
