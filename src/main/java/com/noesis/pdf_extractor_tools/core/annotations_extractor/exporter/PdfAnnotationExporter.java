package com.noesis.pdf_extractor_tools.core.annotations_extractor.exporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noesis.pdf_extractor_tools.core.annotations_extractor.model.Annotation;
import com.noesis.pdf_extractor_tools.core.common.ExportedFile;
import com.noesis.pdf_extractor_tools.core.exception.ExportException;

public class PdfAnnotationExporter implements IAnnotationExporter {
    private static final Logger logger = LoggerFactory.getLogger(PdfAnnotationExporter.class);

    private final LinkedHashMap<Integer, List<Annotation>> annotations;
    private final String title;

    private PDDocument document;
    private PDPage currentPage;
    private PDPageContentStream contentStream;
    private float yPosition;

    private PDFont TITLE_FONT;
    private PDFont SUBTITLE_FONT;
    private PDFont CONTENT_FONT;
    private static final PDColor MAIN_COLOR = new PDColor(new float[] { 0, 0, 0 }, PDDeviceRGB.INSTANCE);
    private static final PDColor TITLE_COLOR = new PDColor(new float[] {
            19f / 255, 84f / 255, 135f / 255 }, PDDeviceRGB.INSTANCE);
    private static final PDColor SUBTITLE_COLOR = new PDColor(new float[] {
            19f / 255, 102f / 255, 135f / 255 }, PDDeviceRGB.INSTANCE);

    private static final float TITLE_SIZE = 18f;
    private static final float SUBTITLE_SIZE = 14f;
    private static final float CONTENT_SIZE = 11f;

    private static final float MARGIN = 50f;
    private static final float LINE_SPACING = 15f;
    private static final float PARAGRAPH_SPACING = 20f;

    public PdfAnnotationExporter(LinkedHashMap<Integer, List<Annotation>> annotations, String title) {
        this.annotations = annotations;
        this.title = title;
    }

    @Override
    public ExportedFile export() throws ExportException {
        Path tempFile = null;
        try {
            document = new PDDocument();
            TITLE_FONT = PDType0Font.load(document, getClass().getResourceAsStream("/fonts/LiberationSerif-Bold.ttf"));
            SUBTITLE_FONT = PDType0Font.load(document,
                    getClass().getResourceAsStream("/fonts/LiberationSerif-Bold.ttf"));
            CONTENT_FONT = PDType0Font.load(document,
                    getClass().getResourceAsStream("/fonts/LiberationSerif-Regular.ttf"));

            createNewPage();

            // main title
            addTitle(title);
            addEmptyLine();

            // get all annotations
            for (Integer pageNumber : annotations.keySet()) {
                List<Annotation> pageAnnotations = annotations.get(pageNumber);

                if (pageAnnotations != null && !pageAnnotations.isEmpty()) {
                    addPageSubtitle("Page " + pageNumber);

                    for (Annotation annotation : pageAnnotations) {
                        addAnnotationParagraph(annotation);
                    }

                    addEmptyLine();
                }
            }

            if (contentStream != null) {
                contentStream.close();
            }

            tempFile = Files.createTempFile(title, ".pdf");
            document.save(tempFile.toFile());

            logger.info("Pdf export completed.");
            return new ExportedFile(generatePathName(title), tempFile);

        } catch (IOException e) {
            logger.error("Error during PDF export", e);
            if (contentStream != null)
                try {
                    contentStream.close();
                } catch (IOException ex) {
                    logger.error("Error closing contentStream", ex);
                }
            if (document != null)
                try {
                    document.close();
                } catch (IOException ex) {
                    logger.error("Error closing document", ex);
                }

            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                    logger.info("Temporary file deleted after failure: {}", tempFile);
                } catch (IOException ex) {
                    logger.warn("Failed to delete temporary file: {}", tempFile, ex);
                }
            }

            throw new ExportException("Unable to export PDF for Annotation extraction");
        }
    }

    private void createNewPage() throws IOException {
        // close last stream if necessary
        if (contentStream != null) {
            contentStream.close();
        }

        currentPage = new PDPage(PDRectangle.A4);
        document.addPage(currentPage);
        contentStream = new PDPageContentStream(document, currentPage);
        yPosition = currentPage.getMediaBox().getHeight() - MARGIN;
    }

    private void addTitle(String title) throws IOException {

        String formatTitle = title.replace("\n", " ");
        checkPageSpace(TITLE_SIZE + PARAGRAPH_SPACING);

        contentStream.beginText();
        contentStream.setFont(TITLE_FONT, TITLE_SIZE);
        contentStream.setNonStrokingColor(TITLE_COLOR);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(formatTitle);
        contentStream.endText();

        contentStream.setNonStrokingColor(MAIN_COLOR);

        yPosition -= TITLE_SIZE + PARAGRAPH_SPACING;
    }

    private void checkPageSpace(float requiredSpace) throws IOException {
        if (yPosition - requiredSpace < MARGIN) {
            createNewPage();
        }
    }

    private void addPageSubtitle(String subtitle) throws IOException {
        checkPageSpace(SUBTITLE_SIZE + PARAGRAPH_SPACING);

        contentStream.beginText();
        contentStream.setFont(SUBTITLE_FONT, SUBTITLE_SIZE);
        contentStream.setNonStrokingColor(SUBTITLE_COLOR);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(subtitle);
        contentStream.endText();

        contentStream.setNonStrokingColor(MAIN_COLOR);

        yPosition -= SUBTITLE_SIZE + LINE_SPACING;
    }

    private void addAnnotationParagraph(Annotation annotation) throws IOException {
        String content = sanitize(annotation.getContent());

        if (content != null && !content.isEmpty()) {
            String[] logicalLines = content.split("\n");

            for (int blockIndex = 0; blockIndex < logicalLines.length; blockIndex++) {
                String logicalLine = logicalLines[blockIndex].trim();

                if (logicalLine.isEmpty())
                    continue;

                // Add bullet for first line
                String bulletContent = (blockIndex == 0 ? "• " : "  ") + logicalLine;

                List<String> lines = splitTextToFitWidth(bulletContent, CONTENT_FONT, CONTENT_SIZE,
                        currentPage.getMediaBox().getWidth() - 2 * MARGIN - 20);

                float totalHeight = lines.size() * (CONTENT_SIZE + 2);
                checkPageSpace(totalHeight + LINE_SPACING);

                float indentX = MARGIN + 20;

                contentStream.beginText();
                contentStream.setFont(CONTENT_FONT, CONTENT_SIZE);
                contentStream.newLineAtOffset(indentX, yPosition);

                for (int i = 0; i < lines.size(); i++) {
                    if (i > 0) {
                        contentStream.newLineAtOffset(0, -(CONTENT_SIZE + 2));
                    }
                    contentStream.showText(lines.get(i));
                }

                contentStream.endText();

                yPosition -= totalHeight + LINE_SPACING;
            }
        }
    }

    private List<String> splitTextToFitWidth(String text, PDFont font, float fontSize, float maxWidth)
            throws IOException {
        List<String> lines = new java.util.ArrayList<>();

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float textWidth = font.getStringWidth(testLine) / 1000 * fontSize;

            if (textWidth > maxWidth && currentLine.length() > 0) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    private void addEmptyLine() {
        yPosition -= LINE_SPACING;
    }

    private String sanitize(String text) {
        if (text == null)
            return "";
        return text.replace("\t", "    ").replace("\uFEFF", "").replace("\u200B", "");
    }

    private String generatePathName(String title) {
        String titleWithoutWhiteSpace = title.replaceAll("\\s", "_");
        return titleWithoutWhiteSpace + ".pdf";
    }

}
