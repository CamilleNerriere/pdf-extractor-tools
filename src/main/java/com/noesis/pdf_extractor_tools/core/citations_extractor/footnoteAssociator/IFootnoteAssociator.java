package com.noesis.pdf_extractor_tools.core.citations_extractor.footnoteAssociator;

import java.util.LinkedHashMap;
import java.util.List;

public interface IFootnoteAssociator<C, A, F> {
    public LinkedHashMap<Integer, List<C>> associateCitationWithFootnote(
            LinkedHashMap<Integer, List<A>> citations,
            LinkedHashMap<Integer, List<F>> footnotes);
}
