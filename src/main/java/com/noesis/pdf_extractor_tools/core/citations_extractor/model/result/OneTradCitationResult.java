package com.noesis.pdf_extractor_tools.core.citations_extractor.model.result;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TroncatedCitation;

public record OneTradCitationResult(TradCitation citation, TroncatedCitation trunc) {
    public boolean citationIsEmpty() {
        return citation.getText() == null || citation.getText().isEmpty();
    }
}
