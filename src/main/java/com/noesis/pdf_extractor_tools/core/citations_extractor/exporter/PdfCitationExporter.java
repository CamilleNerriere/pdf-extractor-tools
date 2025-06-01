package com.noesis.pdf_extractor_tools.core.citations_extractor.exporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedHarvardCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExporterContext;

public class PdfCitationExporter implements ICitationExporter {

    private static final float MARGIN = 50;
    private static final float START_Y = 750;
    private static final float LINE_HEIGHT = 14.5f;
    private static final float BOTTOM_MARGIN = 30;

    private static final Logger logger = LoggerFactory.getLogger(PdfCitationExporter.class);

    private final String title;
    private final LinkedHashMap<Integer, List<TradCitationWithNote>> tradCitations;
    private final LinkedHashMap<Integer, List<BlocCitationWithNote>> blocCitations;
    private final LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations;

    public PdfCitationExporter(ExporterContext context) {
        this.title = context.getTitle();
        this.tradCitations = context.getTradCitations();
        this.blocCitations = context.getBlocCitations();
        this.harvardCitations = context.getHarvardCitations();
    }

    @Override
    public ExportedFile export() {
        Path tempFile = null;

        try (PDDocument document = new PDDocument()) {

            PDType0Font regularFont = PDType0Font.load(document,
                    getClass().getResourceAsStream("/fonts/LiberationSerif-Regular.ttf"));
            PDType0Font boldFont = PDType0Font.load(document,
                    getClass().getResourceAsStream("/fonts/LiberationSerif-Bold.ttf"));

            String fileName = generatePathName(title);
            Set<Integer> allPages = new TreeSet<>();
            allPages.addAll(tradCitations.keySet());
            allPages.addAll(harvardCitations.keySet());

            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(document, page);
            cs.setLeading(LINE_HEIGHT);
            cs.beginText();
            float[] yPosRef = { START_Y };
            cs.newLineAtOffset(MARGIN, yPosRef[0]);

            cs = writeCenteredLine(document, cs, boldFont, 18, title, yPosRef);
            cs = addLineBreak(cs, yPosRef, 2);
            yPosRef[0] -= LINE_HEIGHT;

            for (int pageNum : allPages) {
                List<TradCitationWithNote> classical = tradCitations.get(pageNum);
                List<AnnotatedHarvardCitation> harvard = harvardCitations.get(pageNum);
                List<BlocCitationWithNote> bloc = blocCitations.get(pageNum);

                boolean hasClassical = classical != null && !classical.isEmpty();
                boolean hasHarvard = harvard != null && !harvard.isEmpty();
                boolean hasBloc = bloc != null && !bloc.isEmpty();

                if (!hasClassical && !hasHarvard && !hasBloc)
                    continue;

                cs = writeCenteredLine(document, cs, boldFont, 14, "Page " + pageNum, yPosRef);
                cs = addLineBreak(cs, yPosRef, 1);
                yPosRef[0] -= LINE_HEIGHT;

                if (hasClassical && classical != null) {
                    cs = writeLines(document, cs, boldFont, 13, "----- Classical Type Citation -----", yPosRef);
                    cs = addLineBreak(cs, yPosRef, 1);
                    yPosRef[0] -= LINE_HEIGHT;

                    for (TradCitationWithNote citation : classical) {
                        String cit = citation.getContent();
                        String noteNumber = citation.getNoteNumber();
                        String footnote = citation.getFootnote();

                        cs = writeLines(document, cs, boldFont, 12, "Note " + noteNumber + " :", yPosRef);
                        cs = writeLines(document, cs, regularFont, 12, cit, yPosRef);
                        cs = writeLines(document, cs, boldFont, 12, "Footnote :", yPosRef);
                        cs = writeLines(document, cs, regularFont, 12, footnote, yPosRef);
                        yPosRef[0] -= LINE_HEIGHT;
                        cs = addLineBreak(cs, yPosRef, 1);
                    }
                }

                cs = addLineBreak(cs, yPosRef, 1);

                if (hasBloc && bloc != null) {
                    cs = writeLines(document, cs, boldFont, 13, "----- Bloc Type Citation -----", yPosRef);
                    cs = addLineBreak(cs, yPosRef, 1);
                    yPosRef[0] -= LINE_HEIGHT;

                    for (BlocCitationWithNote citation : bloc) {
                        String cit = citation.getContent();
                        String noteNumber = citation.getNoteNumber();
                        String footnote = citation.getFootnote();

                        cs = writeLines(document, cs, boldFont, 12, "Note " + noteNumber + " :", yPosRef);
                        cs = writeLines(document, cs, regularFont, 12, cit, yPosRef);
                        cs = writeLines(document, cs, boldFont, 12, "Footnote :", yPosRef);
                        cs = writeLines(document, cs, regularFont, 12, footnote, yPosRef);
                        yPosRef[0] -= LINE_HEIGHT;
                        cs = addLineBreak(cs, yPosRef, 1);
                    }
                }

                cs = addLineBreak(cs, yPosRef, 1);

                if (hasHarvard && harvard != null) {
                    cs = writeLines(document, cs, boldFont, 13, "----- Harvard Type Citation -----", yPosRef);
                    cs = addLineBreak(cs, yPosRef, 1);
                    yPosRef[0] -= LINE_HEIGHT;

                    for (AnnotatedHarvardCitation citation : harvard) {
                        String cit = citation.getContent();
                        String note = citation.getNoteContent();

                        cs = writeLines(document, cs, regularFont, 12, cit, yPosRef);
                        cs = writeLines(document, cs, boldFont, 12, "Note :", yPosRef);
                        cs = writeLines(document, cs, regularFont, 12, note, yPosRef);
                        yPosRef[0] -= LINE_HEIGHT;
                        cs = addLineBreak(cs, yPosRef, 1);
                    }
                }

                yPosRef[0] -= LINE_HEIGHT * 2;
            }

            cs.endText();
            cs.close();

            tempFile = Files.createTempFile(title, ".pdf");
            document.save(tempFile.toFile());
            logger.info("Pdf export completed.");

            return new ExportedFile(fileName, tempFile);

        } catch (IOException e) {

            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                    logger.info("Temporary file deleted after failure: {}", tempFile);
                } catch (IOException ex) {
                    logger.warn("Failed to delete temporary file: {}", tempFile, ex);
                }
            }

            return null;
        }
    }

    private PDPageContentStream writeLines(PDDocument document, PDPageContentStream cs,
            PDType0Font font, float fontSize, String rawText,
            float[] yPosRef) throws IOException {

        final float maxWidth = new PDPage().getMediaBox().getWidth() - 2 * MARGIN;
        final float fontScale = fontSize / 1000f;

        String clean = sanitizeForPdf(rawText);
        for (String logicalLine : clean.split("\n")) {
            String[] words = logicalLine.split(" ");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
                String testLine = line.length() == 0 ? word : line + " " + word;
                float textWidth = font.getStringWidth(testLine) * fontScale;

                if (textWidth > maxWidth) {
                    cs = writeWrappedLine(document, cs, font, fontSize, line.toString(), yPosRef);
                    line = new StringBuilder(word);
                } else {
                    line = new StringBuilder(testLine);
                }
            }

            if (line.length() > 0) {
                cs = writeWrappedLine(document, cs, font, fontSize, line.toString(), yPosRef);
            }
        }

        return cs;
    }

    private PDPageContentStream writeCenteredLine(PDDocument document, PDPageContentStream cs,
            PDType0Font font, float fontSize,
            String text, float[] yPosRef) throws IOException {

        String clean = sanitizeForPdf(text);
        PDRectangle mediaBox = new PDPage().getMediaBox();
        float stringWidth = font.getStringWidth(clean) / 1000 * fontSize;
        float startX = (mediaBox.getWidth() - stringWidth) / 2;

        if (yPosRef[0] <= BOTTOM_MARGIN) {
            cs.endText();
            cs.close();
            PDPage newPage = new PDPage();
            document.addPage(newPage);
            cs = new PDPageContentStream(document, newPage);
            cs.setLeading(LINE_HEIGHT);
            cs.beginText();
            yPosRef[0] = START_Y;
            cs.newLineAtOffset(MARGIN, yPosRef[0]);
            cs.setFont(font, fontSize);

        }

        cs.setFont(font, fontSize);
        cs.newLineAtOffset(startX - MARGIN, 0);
        cs.showText(clean);
        cs.newLineAtOffset(-(startX - MARGIN), 0);
        cs.newLine();
        yPosRef[0] -= LINE_HEIGHT;

        return cs;
    }

    private PDPageContentStream writeWrappedLine(PDDocument document, PDPageContentStream cs,
            PDType0Font font, float fontSize,
            String lineText, float[] yPosRef) throws IOException {

        if (yPosRef[0] <= BOTTOM_MARGIN) {
            cs.endText();
            cs.close();
            PDPage newPage = new PDPage();
            document.addPage(newPage);
            cs = new PDPageContentStream(document, newPage);
            cs.setLeading(LINE_HEIGHT);
            cs.beginText();
            cs.setFont(font, fontSize);
            yPosRef[0] = START_Y;
            cs.newLineAtOffset(MARGIN, yPosRef[0]);
        }

        cs.setFont(font, fontSize);
        cs.showText(lineText);
        cs.newLine();
        yPosRef[0] -= LINE_HEIGHT;

        return cs;
    }

    private PDPageContentStream addLineBreak(PDPageContentStream cs, float[] yPosRef) throws IOException {
        return addLineBreak(cs, yPosRef, 1);
    }

    private PDPageContentStream addLineBreak(PDPageContentStream cs, float[] yPosRef, int lines) throws IOException {
        for (int i = 0; i < lines; i++) {
            cs.newLine();
            yPosRef[0] -= LINE_HEIGHT;
        }
        return cs;
    }

    private String sanitizeForPdf(String text) {
        if (text == null)
            return "";
        return text
                .replace("\t", "    ")
                .replace("\u00A0", " ")
                .replace("“", "\"").replace("”", "\"")
                .replace("«", "\"").replace("»", "\"")
                .replace("ﬀ", "ff").replace("ﬁ", "fi").replace("ﬂ", "fl")
                .replace("ﬃ", "ffi").replace("ﬄ", "ffl")
                .replace("ﬅ", "ft").replace("ﬆ", "st")
                .replaceAll("[\\p{Cntrl}&&[^\n]]", "");
    }

    private String generatePathName(String title) {
        return title.replaceAll("\\s", "_") + ".pdf";
    }
}
