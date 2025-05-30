package com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.citation.harvard;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.text.TextPosition;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedHarvardCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TroncatedCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExtractionContext;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.result.HarvardCitationExtractionResult;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.result.HarvardExtractionResult;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.result.OnePotentialCitationResult;

public class HarvardCitationExtractor implements IHarvardCitationExtractor {

    @Override
    public HarvardCitationExtractionResult extractCitationsPerPage(ExtractionContext context,
            TroncatedCitation troncatedCitationFromLastPage) {

        List<AnnotatedHarvardCitation> allCitations = new ArrayList<>();
        TroncatedCitation updatedTroncated = troncatedCitationFromLastPage;

        String[] openingQuotes = { "«", "\"", "“" };

        for (String opening : openingQuotes) {
            String content = (updatedTroncated != null && opening.equals(updatedTroncated.openingQuote()))
                    ? updatedTroncated.content()
                    : null;
            TroncatedCitation troncatedToPass = new TroncatedCitation(content, opening);
            HarvardCitationExtractionResult result = extractCitations(context, opening, troncatedToPass);
            allCitations.addAll(result.harvardCitations());

            if (result.troncatedCitation().isEmpty() == false) {
                updatedTroncated = result.troncatedCitation();
            } else if (updatedTroncated != null && opening.equals(updatedTroncated.openingQuote())) {
                updatedTroncated = new TroncatedCitation(null, null);

            }
        }

        return new HarvardCitationExtractionResult(allCitations, updatedTroncated);
    }

    private HarvardCitationExtractionResult extractCitations(ExtractionContext context, String openingQuote,
            TroncatedCitation troncatedCitationFromLastPage) {

        List<AnnotatedHarvardCitation> citations = new ArrayList<>();

        String truncContent = troncatedCitationFromLastPage.content();
        String truncOpeningQuote = troncatedCitationFromLastPage.openingQuote();

        if (!troncatedCitationFromLastPage.isEmpty()) {
            OnePotentialCitationResult citationResult = extractOneCitation(context,
                    truncOpeningQuote,
                    truncContent, 0);
                    
                    HarvardExtractionResult result = getExtractionResult(context, citationResult);

                    if(result.citation() !=null){
                        citations.add(result.citation());
                    }
                    truncContent = result.truncContent();
                    truncOpeningQuote = result.truncOpeningQuote();

        }

        List<TextPosition> positions = context.getPositions();

        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i).getUnicode().equals(openingQuote)) {
                OnePotentialCitationResult citationResult = extractOneCitation(context, openingQuote, "", i);
                HarvardExtractionResult result = getExtractionResult(context, citationResult);

                    if(result.citation() !=null){
                        citations.add(result.citation());
                    }
                    truncContent = result.truncContent();
                    truncOpeningQuote = result.truncOpeningQuote();
            }
        }

        return new HarvardCitationExtractionResult(citations, new TroncatedCitation(truncContent, truncOpeningQuote));
    }


    private OnePotentialCitationResult extractOneCitation(ExtractionContext context, String openingQuote,
            String remainingTextFromLastPage, int start) {

        String c1 = openingQuote;
        StringBuilder citationContent = new StringBuilder(remainingTextFromLastPage);

        String c2 = switch (c1) {
            case "«" -> "»";
            case "\"" -> "\"";
            case "“" -> "”";
            default -> throw new IllegalArgumentException("Unauthorized Opening Quote" + c1);
        };

        // Get first and last char to calculate note positions

        List<TextPosition> positions = context.getPositions();
        float medianFontSize = context.getMedianFontSize();

        TextPosition firstChar = positions.get(start);
        TextPosition lastChar = null;
        boolean isClosed = false;
        int lastCharIndex = 0;

        for (int j = start; j < positions.size(); j++) {

            TextPosition currentCharAsPosition = positions.get(j);
            String currentChar = currentCharAsPosition.getUnicode();

            /* To avoid getting page note content in case of truncated citation */
            boolean isTruncationContext = remainingTextFromLastPage.length() > 0;
            boolean isSmallFont = currentCharAsPosition.getFontSizeInPt() < medianFontSize * 0.95;

            if (isTruncationContext && isSmallFont)
                continue;

            if (currentChar.equals(c2)) {
                citationContent.append(c2);
                lastChar = positions.get(j);
                isClosed = true;
                lastCharIndex = j;
                break;
            }

            citationContent.append(currentChar);

        }

        if (lastChar == null) {
            return new OnePotentialCitationResult(null, new TroncatedCitation(citationContent.toString(), openingQuote),
                    lastCharIndex);
        }

        TradCitation citation = new TradCitation(citationContent.toString().trim(), context.getPage(),
                firstChar, lastChar, c1);

        return new OnePotentialCitationResult(citation, new TroncatedCitation(null, null), lastCharIndex);

    }


    private AnnotatedHarvardCitation extractOneHarvardCitation(ExtractionContext context, TradCitation citation,
            int start) {

        List<TextPosition> positions = context.getPositions();
        StringBuilder harvardNote = new StringBuilder();
        boolean foundHavardNote = false;

        for (int j = start + 1; j < positions.size(); j++) {
            String c = positions.get(j).getUnicode();

            if (!foundHavardNote) {
                if (c.equals("(") && j <= start + 5) {
                    harvardNote.append(c);
                    foundHavardNote = true;
                }

            } else {
                harvardNote.append(c);
                if (c.equals(")"))
                    break;
            }
        }

        if (!harvardNote.isEmpty() && isValidHarvardNote(harvardNote.toString())) {
            AnnotatedHarvardCitation annotatedHarvardCitation = new AnnotatedHarvardCitation(citation,
                    harvardNote.toString());
            return annotatedHarvardCitation;
        }
        return null;
    }

    private HarvardExtractionResult getExtractionResult(ExtractionContext context, OnePotentialCitationResult citationResult) {
        String truncContent = citationResult.trunc().content();
        String truncOpeningQuote = citationResult.trunc().openingQuote();
        if (!citationResult.citationIsEmpty()) {
            AnnotatedHarvardCitation harvardCitation = extractOneHarvardCitation(context, citationResult.citation(),
                    citationResult.lastIndex());
            truncContent = "";
            return new HarvardExtractionResult(harvardCitation, truncContent, truncOpeningQuote);
        }

        return new HarvardExtractionResult(null, truncContent, truncOpeningQuote);

    }

    private boolean isValidHarvardNote(String note) {
        if (note == null || note.length() < 3)
            return false;
        boolean hasDigit = note.matches(".*\\d+.*");
        boolean hasUpper = note.matches(".*[A-ZÉÈÊÂÀÙÎÔÇ].*"); // majuscules françaises aussi
        return (hasDigit || hasUpper);
    }

}
