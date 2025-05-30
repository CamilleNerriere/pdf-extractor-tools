package com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.footnote;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.pdfbox.text.TextPosition;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.NoteCandidate;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExtractionContext;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.footnote.Footnote;


public class FootnoteExtractor implements IFootnoteExtractor {

    @Override
    public List<Footnote> getFootnotes(
            ExtractionContext context,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage) {

        List<TextPosition> positions = context.getPositions();

        int page = context.getPage();

        List<NoteCandidate> pageNoteCandidates = notesCandidatesPerPage.get(page);

        if (pageNoteCandidates == null || pageNoteCandidates.isEmpty()) {
            return new ArrayList<>();
        }

        List<NoteCandidate> footnoteCandidates = getFootnoteCandidates(pageNoteCandidates);

        Set<Integer> alreadyTreatedCandidateNumbers = new HashSet<>();
        List<Footnote> footnotes = new ArrayList<>();

        for (int i = 0; i < positions.size(); i++) {
            TextPosition pos = positions.get(i);
            String c = pos.getUnicode();

            Optional<NoteCandidate> matchingCandidate = findMatchingCandidate(pos, footnoteCandidates);

            /* We find a match, start the treatment */
            if (matchingCandidate.isPresent()) {

                NoteCandidate candidate = matchingCandidate.get();

                String noteNumberAsString = candidate.getText();

                if (!isNumeric(noteNumberAsString))
                    continue;

                int candidateNoteNumber = Integer.parseInt(noteNumberAsString);

                // Avoid multiple treatment
                if (alreadyTreatedCandidateNumbers.contains(candidateNoteNumber))
                    continue;

                alreadyTreatedCandidateNumbers.add(candidateNoteNumber);

                NoteCandidate nextNoteCandidate = getNextNoteCandidate(footnoteCandidates, candidateNoteNumber);

                Footnote footnote = extracFootnote(positions, i, nextNoteCandidate, page, noteNumberAsString);

                if(footnote != null) footnotes.add(footnote);
            }

        }

        return footnotes;
    }

    private List<NoteCandidate> getFootnoteCandidates(List<NoteCandidate> pageNoteCandidates) {
        return pageNoteCandidates.stream()
                .collect(Collectors.groupingBy(NoteCandidate::getText))
                .values().stream()
                .map(group -> group.stream()
                        .max(Comparator.comparing(NoteCandidate::getY))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Optional<NoteCandidate> findMatchingCandidate(TextPosition pos, List<NoteCandidate> footnoteCandidates) {

        float xPosition = pos.getXDirAdj();
        float yPosition = pos.getYDirAdj();

        return footnoteCandidates.stream()
                .filter(note -> {
                    float deltaX = Math.abs(note.getX() - xPosition);
                    float deltaY = Math.abs(note.getY() - yPosition);
                    return deltaX < 10.0f && deltaY < 10.0f;
                })
                .findFirst();
    }

    private Footnote extracFootnote(List<TextPosition> positions, int i, NoteCandidate nextNoteCandidate, int page, String noteNumberAsString) {
        /** Set variables to stock footnote infos */
        StringBuilder footnote = new StringBuilder();
        TextPosition pos = positions.get(i);
        String c = pos.getUnicode();
        footnote.append(c);

        TextPosition startPos = positions.get(i);
        TextPosition endPos;
        boolean noteEnded = false;

        // Start finding next characters

        for (int j = i + 1; j < positions.size(); j++) {

            TextPosition newPos = positions.get(j);
            String cha = newPos.getUnicode();

            // Loop ends if we're approximatly at the next note position

            if (nextNoteCandidate != null) {
                float XNextNote = nextNoteCandidate.getX();
                float YNextNote = nextNoteCandidate.getY();

                float deltaX = Math.abs(newPos.getXDirAdj() - XNextNote);
                float deltaY = Math.abs(newPos.getYDirAdj() - YNextNote);

                if (deltaX < 10.0f && deltaY < 10.0f) {
                    endPos = positions.get(j - 1);
                    Footnote completeFootnote = new Footnote(footnote.toString(), page, noteNumberAsString,
                            startPos, endPos);
                    noteEnded = true;
                    return completeFootnote;
                }
            }

            footnote.append(cha);
        }

        // in case it's the last note on page
        if (!noteEnded && footnote.length() > 0) {
            endPos = positions.get(positions.size() - 1);
            Footnote completeFootnote = new Footnote(footnote.toString(), page, noteNumberAsString, startPos,
                    endPos);
            return completeFootnote;
        }

        return null;
    }

    private NoteCandidate getNextNoteCandidate(List<NoteCandidate> footnoteCandidates, int candidateNoteNumber) {
        List<Integer> noteNumbers = new ArrayList<>();

        for (NoteCandidate note : footnoteCandidates) {
            int noteNumber = Integer.parseInt(note.getText());
            noteNumbers.add(noteNumber);
        }

        int nextNoteNumber = -1;

        for (int num : noteNumbers) {
            if (num - candidateNoteNumber == 1) {
                nextNoteNumber = num;
            }
        }

        final int finalNextNoteNumber = nextNoteNumber;

        Optional<NoteCandidate> nextNoteCandidate = footnoteCandidates.stream()
                .filter(note -> note.getText().equals(Integer.toString(finalNextNoteNumber)))
                .findAny();

        if (nextNoteCandidate.isPresent()) {
            return nextNoteCandidate.get();
        }

        return null;
    }

    private boolean isNumeric(String s) {
        if (s == null || s.isEmpty())
            return false;

        try {
            Integer.valueOf(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
