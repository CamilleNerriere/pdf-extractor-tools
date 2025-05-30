
package com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.note;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.pdfbox.text.TextPosition;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.NoteCandidate;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExtractionContext;

public class NoteDetector implements INoteDetector {

    private static final Set<String> CLOSING_QUOTES = Set.of("»", "\"", "”");

    @Override
    public List<NoteCandidate> getNoteCandidates(ExtractionContext context) {

        List<NoteCandidate> noteCandidates = new ArrayList<>();
        List<NoteCandidate> tradCandidates = getTradNoteCandidates(context);
        noteCandidates.addAll(tradCandidates);
        
        /** Right now , havards note are directly searched for with citation extraction, but in case in need to extract separatly them, there is this code */
        // List<NoteCandidate> harvardCandidates = getHarvardNoteCandidates(context);
        // noteCandidates.addAll(harvardCandidates);

        return noteCandidates;
    }

    @Override
    public List<NoteCandidate> getTradNoteCandidates(ExtractionContext context) {

        List<TextPosition> positions = context.getPositions();
        int page = context.getPage();
        float avgFontSize = context.getAverageFontSize();

        List<NoteCandidate> noteCandidates = new ArrayList<>(); // ici on va venir récupérer les notes nombre

        TextPosition lastNumberFound = null;
        StringBuilder completeNote = new StringBuilder("");
        float xStart = 0;
        float yStart = 0;
        String styleType = "note";

        for (TextPosition candidate : positions) {
            String c = candidate.getUnicode();
            if (c == null || c.length() == 0)
                continue;

            char ch = c.charAt(0);
            int type = Character.getType(ch);

            // Doit être un chiffre (chiffre décimal ou caractère comme ¹)
            if ((type == Character.DECIMAL_DIGIT_NUMBER || type == Character.OTHER_NUMBER)
                    && candidate.getFontSizeInPt() < avgFontSize * 0.75) {

                // a previous number has been found
                if (lastNumberFound != null) {

                    float deltaX = Math.abs(candidate.getXDirAdj() - lastNumberFound.getXDirAdj());
                    float deltaY = Math.abs(candidate.getYDirAdj() - lastNumberFound.getYDirAdj());

                    if (deltaX < 10 && deltaY < 10) { // it's close
                        completeNote.append(candidate.getUnicode());
                    } else { // it's not close -> start a new note
                        if (completeNote.length() > 0) {
                            noteCandidates
                                    .add(new NoteCandidate(completeNote.toString(), page, xStart, yStart, styleType));
                        }
                        completeNote.setLength(0);
                        completeNote.append(candidate.getUnicode());
                        xStart = candidate.getXDirAdj();
                        yStart = candidate.getYDirAdj();
                    }
                } else { // complete note is empty -> start a new note
                    completeNote.setLength(0);
                    completeNote.append(candidate.getUnicode());
                    xStart = candidate.getXDirAdj();
                    yStart = candidate.getYDirAdj();
                }

                lastNumberFound = candidate;
            }

        }

        if (completeNote.length() > 0) {
            noteCandidates.add(new NoteCandidate(completeNote.toString(), page, xStart, yStart, styleType));
        }

        return noteCandidates;
    }

    @Override
    public List<NoteCandidate> getHarvardNoteCandidates(ExtractionContext context) {

        List<TextPosition> positions = context.getPositions();
        int page = context.getPage();

        List<NoteCandidate> noteCandidates = new ArrayList<>(); // ici on va venir récupérer les notes harvard

        TextPosition closingQuoteFound = null;

        boolean isCitationCandidate = false;

        StringBuilder completeNote = new StringBuilder();
        String styleType = "harvard";

        for (TextPosition candidate : positions) {
            String c = candidate.getUnicode();
            if (c == null || c.length() == 0)
                continue;

            if (CLOSING_QUOTES.contains(c)) {
                closingQuoteFound = candidate;
            }

            if (closingQuoteFound != null && c.equals("(")) {
                float deltaX = Math.abs(candidate.getXDirAdj() - closingQuoteFound.getXDirAdj());
                float deltaY = Math.abs(candidate.getYDirAdj() - closingQuoteFound.getYDirAdj());

                if (deltaX < 10 && deltaY < 10) { // it's close
                    completeNote.append(c);
                    isCitationCandidate = true;
                }
            }

            if (isCitationCandidate && !c.equals(")")) {
                completeNote.append(c);
            } else if (c.equals(")")) {
                completeNote.append(c);

                if (completeNote.length() > 3) { 
                    float xStart = candidate.getXDirAdj();
                    float yStart = candidate.getYDirAdj();
                    NoteCandidate noteCandidate = new NoteCandidate(completeNote.toString(), page, xStart, yStart,
                            styleType);
                    noteCandidates.add(noteCandidate);
                }

                completeNote.setLength(0);
                isCitationCandidate = false;
            }

        }
        return noteCandidates;
    }
}
