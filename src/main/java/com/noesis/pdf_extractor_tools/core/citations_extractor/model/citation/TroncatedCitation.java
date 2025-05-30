package com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation;

public record TroncatedCitation(String content, String openingQuote) {
    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }
}
