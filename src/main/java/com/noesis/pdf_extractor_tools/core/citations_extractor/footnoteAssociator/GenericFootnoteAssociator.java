package com.noesis.pdf_extractor_tools.core.citations_extractor.footnoteAssociator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public abstract class GenericFootnoteAssociator<C, A, F> {
    public LinkedHashMap<Integer, List<C>> associateCitationWithFootnote(
            LinkedHashMap<Integer, List<A>> citations,
            LinkedHashMap<Integer, List<F>> footnotes) {

        Set<Integer> pages = citations.keySet();

        LinkedHashMap<Integer, List<C>> citationsWithNotes = new LinkedHashMap<>();

        for (int page : pages) {
            List<A> citationsPerPage = citations.get(page);
            List<F> footnotePerPage = footnotes.get(page);

            if (footnotePerPage == null || footnotePerPage.isEmpty() || citationsPerPage == null
                    || citationsPerPage.isEmpty())
                continue;

            List<C> citationsPerPageWithFootnotes = new ArrayList<>();

            for (A citation : citationsPerPage) {
                String noteNumber = getNoteNumberFromCitation(citation);

                F footnote = footnotePerPage.stream()
                        .filter(f -> getNoteNumberFromFootnote(f).equals(noteNumber)).findAny().orElse(null);

                if (footnote == null)
                    continue;

                C associated = createCitationWithFootnote(citation, getTextFromFootnote(footnote));
                citationsPerPageWithFootnotes.add(associated);
            }
            citationsWithNotes.put(page, citationsPerPageWithFootnotes);
        }

        return citationsWithNotes;
    }

    protected abstract String getNoteNumberFromCitation(A citation);

    protected abstract String getNoteNumberFromFootnote(F footnote);

    protected abstract String getTextFromFootnote(F footnote);

    protected abstract C createCitationWithFootnote(A citation, String footnoteText);
}
