package com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.citation.trad;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.text.TextPosition;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TroncatedCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExtractionContext;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.result.OneTradCitationResult;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.result.TradCitationExtractionResult;

public class TradCitationExtractor implements ITradCitationExtractor {
    
    @Override
    public TradCitationExtractionResult extractCitationsPerPage(ExtractionContext context,
            TroncatedCitation troncatedCitationFromLastPage) {

        List<TradCitation> allCitations = new ArrayList<>();
        TroncatedCitation updatedTroncated = troncatedCitationFromLastPage;

        String[] openingQuotes = { "«", "\"", "“" };

        for (String opening : openingQuotes) {
            TradCitationExtractionResult result = extractCitations(context, opening, updatedTroncated);
            allCitations.addAll(result.citations());

            if (result.troncatedCitation().isEmpty() == false) {
                updatedTroncated = result.troncatedCitation();
            } else if (updatedTroncated != null && opening.equals(updatedTroncated.openingQuote())) {
                updatedTroncated = new TroncatedCitation(null, null);

            }
        }

        return new TradCitationExtractionResult(allCitations, updatedTroncated);
    }

    
    private TradCitationExtractionResult extractCitations(ExtractionContext context, String openingQuote,
            TroncatedCitation troncatedCitationFromLastPage) {

        List<TradCitation> citations = new ArrayList<>();

        String truncContent = troncatedCitationFromLastPage.content();
        String truncOpeningQuote = troncatedCitationFromLastPage.openingQuote();

        if (!troncatedCitationFromLastPage.isEmpty()) {
            OneTradCitationResult citationResult = extractOneCitation(context,
                    truncOpeningQuote,
                    truncContent, 0);

            if (citationResult.citation() != null) {
                citations.add(citationResult.citation());
                truncContent = "";
            } else {
                truncContent = citationResult.trunc().content();
                truncOpeningQuote = citationResult.trunc().openingQuote();
            }

        }

        List<TextPosition> positions = context.getPositions();

        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i).getUnicode().equals(openingQuote)) {
                OneTradCitationResult citationResult = extractOneCitation(context, openingQuote, "", i);
                if (citationResult.citation() != null) {
                    citations.add(citationResult.citation());
                    truncContent = "";
                } else {
                    truncContent = citationResult.trunc().content();
                    truncOpeningQuote = citationResult.trunc().openingQuote();
                }
            }
        }

        return new TradCitationExtractionResult(citations, new TroncatedCitation(truncContent, truncOpeningQuote));
    }

    private OneTradCitationResult extractOneCitation(ExtractionContext context, String openingQuote,
            String remainingTextFromLastPage, int start) {


        String c1 = openingQuote;
        StringBuilder citationContent = new StringBuilder(remainingTextFromLastPage);
        Boolean isClosed = false;

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

        for (int j = start; j < positions.size(); j++) {

            TextPosition currentCharAsPosition = positions.get(j);
            String currentChar = currentCharAsPosition.getUnicode();

            if (!currentChar.equals(c2)) {

                if (currentCharAsPosition.getFontSizeInPt() < medianFontSize * 0.95) {
                    continue;
                }

                if (!currentChar.equals(c1)) {
                    citationContent.append(positions.get(j).getUnicode());
                }
            } else {
                lastChar = positions.get(j);
                isClosed = true;
                break;

            }
        }

        if (isClosed) {
            TradCitation citation = new TradCitation(citationContent.toString().trim(), context.getPage(),
                    firstChar, lastChar, c1);
            TroncatedCitation trunc = new TroncatedCitation(null, null);
            return new OneTradCitationResult(citation, trunc);
        }

        return new OneTradCitationResult(null, new TroncatedCitation(citationContent.toString(), openingQuote));

    }
}
