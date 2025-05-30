package com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.citation.bloc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.text.TextPosition;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExtractionContext;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.result.BlocExtractionResult;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.text.Line;
import com.noesis.pdf_extractor_tools.core.citations_extractor.pdf.utils.FontStats;

public class BlocCitationExtractor implements IBlocCitationExtractor {
    @Override
    public BlocExtractionResult extractCitationsPerPage(ExtractionContext context,
            List<Line> linesFromLastPage, List<BlocCitation> blocCitationsFromLastPage) {

        List<TextPosition> positions = context.getPositions();

        List<Line> allLines = extractPageLines(positions);

        List<BlocCitation> blocCitations = getBlocCitations(allLines, context);

        String mergedText = buildMergedBlocCitation(allLines, blocCitations, linesFromLastPage,
                blocCitationsFromLastPage, context);

        if (mergedText != null) {
            for (int i = 0; i < blocCitations.size(); i++) {
                BlocCitation citation = blocCitations.get(i);
                if (mergedText.contains(citation.getText())) {
                    BlocCitation newCitation = new BlocCitation(mergedText, citation.getPage(), citation.getStartPos(),
                            citation.getEndPos());
                    blocCitations.set(i, newCitation);
                }
            }
        }

        return new BlocExtractionResult(blocCitations, allLines);
    }

    private List<Line> extractPageLines(List<TextPosition> positions) {

        List<Line> allLines = new ArrayList<>();

        if (positions.isEmpty())
            return allLines;

        TextPosition firstPosInLine = positions.get(0);

        StringBuilder text = new StringBuilder();

        List<TextPosition> linePositions = new ArrayList<>();

        int lineNumber = 1;

        for (int i = 0; i < positions.size(); i++) {
            TextPosition lastPosition = i > 0 ? positions.get(i - 1) : null;
            TextPosition actualPosition = positions.get(i);

            float lastPosX = lastPosition != null ? lastPosition.getXDirAdj() : actualPosition.getXDirAdj();
            float actualPosX = actualPosition.getXDirAdj();

            float xDiff = actualPosX - lastPosX;

            String actualChar = actualPosition.getUnicode();

            if (xDiff < -20f) {
                FontStats stats = new FontStats();
                Line line = new Line(lineNumber, text.toString(), firstPosInLine, positions.get(i - 1),
                        stats.getMedianSize(linePositions));
                allLines.add(line);
                text.setLength(0);
                firstPosInLine = positions.get(i);
                linePositions.clear();
                lineNumber++;
            }

            text.append(actualChar);
            linePositions.add(actualPosition);
        }

        if (!linePositions.isEmpty()) {
            FontStats stats = new FontStats();
            Line line = new Line(lineNumber, text.toString(), firstPosInLine,
                    linePositions.get(linePositions.size() - 1),
                    stats.getMedianSize(linePositions));
            allLines.add(line);
        }

        return allLines;
    }

    private List<BlocCitation> getBlocCitations(List<Line> allLines, ExtractionContext context) {

        int page = context.getPage();
        float medianXLineBegining = context.getLineCoordStatsResult().medianXLineBegining();
        float medianXLineEnd = context.getLineCoordStatsResult().medianXLineEnd();

        float medianFontSize = context.getMedianFontSize();

        List<BlocCitation> blocCitations = new ArrayList<>();

        List<Line> blocCitationLines = new ArrayList<>();

        for (Line line : allLines) {
            float xStartLine = line.getXStart();
            float xEndLine = line.getXEnd();

            Optional<Float> OptlineMedianFontSize = line.getMedianFontSize();
            if (OptlineMedianFontSize.isEmpty()) {
                continue;
            }
            float lineMedianFontSize = OptlineMedianFontSize.get();

            boolean isFootnote = lineMedianFontSize < 0.85 * medianFontSize;

            if (xStartLine > medianXLineBegining && xEndLine < medianXLineEnd && !isFootnote) {
                blocCitationLines.add(line);
            } else if (!blocCitationLines.isEmpty()) {
                StringBuilder text = new StringBuilder();
                TextPosition startPos = blocCitationLines.get(0).getStartPos();
                TextPosition endPos = blocCitationLines.get(blocCitationLines.size() - 1).getEndPos();

                for (Line citationLine : blocCitationLines) {
                    text.append(citationLine.getText());
                }

                BlocCitation blocCitation = new BlocCitation(text.toString(), page, startPos, endPos);
                blocCitations.add(blocCitation);

                blocCitationLines.clear();
            }

        }

        return blocCitations;
    }

    private String buildMergedBlocCitation(List<Line> allLines, List<BlocCitation> citationsFromActualPage,
            List<Line> linesFromLastPage, List<BlocCitation> citationsFromLastPage, ExtractionContext context) {

        if (citationsFromActualPage.isEmpty() || citationsFromLastPage.isEmpty() || linesFromLastPage.isEmpty()) {
            return null;
        }

        List<Line> lastPageLinesWithoutFootnotes = getLinesWithoutFootnotesOrFootpage(linesFromLastPage, context);

        BlocCitation firstCitationOnActualPage = citationsFromActualPage.get(0);
        BlocCitation lastCitationOnLastPage = citationsFromLastPage.get(citationsFromLastPage.size() - 1);

        if (firstCitationOnActualPage == null || lastCitationOnLastPage == null) {
            return null;
        }

        String firstLineText = allLines.get(0).getText();
        String firstCitationText = firstCitationOnActualPage.getText();

        String lastLineTextFromLastPage = lastPageLinesWithoutFootnotes.get(lastPageLinesWithoutFootnotes.size() - 1)
                .getText();
        String lastCitationFromLastPage = lastCitationOnLastPage.getText();

        if (firstCitationText.contains(firstLineText) && lastCitationFromLastPage.contains(lastLineTextFromLastPage)) {
            String allText = lastCitationFromLastPage + " " + firstCitationText;
            return allText;
        }

        return null;

    }

    private List<Line> getLinesWithoutFootnotesOrFootpage(List<Line> lines, ExtractionContext context) {

        if (lines.isEmpty())
            return List.of();

        float medianFontSize = context.getMedianFontSize();

        return lines.stream()
                .filter(l -> isLineRelevant(l, medianFontSize))
                .collect(Collectors.toList());

    }

    private boolean isLineRelevant(Line l, float medianFontSize) {
        return l.getText().length() >= 20 &&
                l.getMedianFontSize().filter(size -> size >= 0.85 * medianFontSize).isPresent();
    }

}
