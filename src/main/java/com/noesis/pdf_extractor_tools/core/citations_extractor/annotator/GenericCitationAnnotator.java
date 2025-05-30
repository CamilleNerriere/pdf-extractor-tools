
package com.noesis.pdf_extractor_tools.core.citations_extractor.annotator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.NoteCandidate;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExtractionContext;

public abstract class GenericCitationAnnotator<C, A> {

    public List<A> getAnnotatedCitations(
            LinkedHashMap<Integer, List<C>> citationsCandidatesPerPage,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage,
            ExtractionContext context) {

        List<A> sortedCitations = new ArrayList<>();
        int page = context.getPage();

        List<C> citations = citationsCandidatesPerPage.get(page);

        if (citations == null)
            return sortedCitations;
        List<NoteCandidate> notesInPage = notesCandidatesPerPage.getOrDefault(page, new ArrayList<>());

        for (C citation : citations) {
            float xCitationEnd = getXEnd(citation);
            float yCitation = getYEnd(citation);

            for (NoteCandidate note : notesInPage) {
                float dx = Math.abs(note.getX() - xCitationEnd);
                float dy = Math.abs(note.getY() - yCitation);

                if (dx < 35 && dy < 10) {
                    sortedCitations.add(createAnnotated(citation, note.getText()));
                }
            }
        }

        return sortedCitations;
    }

    protected abstract float getXEnd(C citation);

    protected abstract float getYEnd(C citation);

    protected abstract A createAnnotated(C citation, String noteText);
}
