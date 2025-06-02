package com.noesis.pdf_extractor_tools.core.annotations_extractor.exporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noesis.pdf_extractor_tools.core.annotations_extractor.model.Annotation;
import com.noesis.pdf_extractor_tools.core.common.ExportedFile;

public class TxtAnnotationExporter implements IAnnotationExporter {
    private static final Logger logger = LoggerFactory.getLogger(TxtAnnotationExporter.class);
    private final LinkedHashMap<Integer, List<Annotation>> annotations;
    private final String title;

    private StringBuilder content;

    private static final String SEPARATOR_LINE = "=" + "=".repeat(80) + "=";
    private static final String SUB_SEPARATOR_LINE = "-" + "-".repeat(40) + "-";
    private static final int LINE_WIDTH = 80;

    public TxtAnnotationExporter(LinkedHashMap<Integer, List<Annotation>> annotations, String title) {
        this.title = title;
        this.annotations = annotations;
    }

    @Override
    public ExportedFile export() {
        Path tempFile = null;
        String fileName = generatePathName(title);
        content = new StringBuilder();

        try {
            // main title

            addTitle(title);
            addEmptyLine();

            // annotations per page
            for (Integer pageNumber : annotations.keySet()) {
                List<Annotation> pageAnnotations = annotations.get(pageNumber);

                if (pageAnnotations != null && !pageAnnotations.isEmpty()) {
                    addPageSubtitle("Page : " + pageNumber);

                    for (Annotation annotation : pageAnnotations) {
                        addAnnotationParagraph(annotation);
                    }

                    addEmptyLine();
                }
            }

            tempFile = Files.createTempFile(title, ".txt");
            Files.write(tempFile, content.toString().getBytes("UTF-8"));
            logger.info("TXT export completed");

            return new ExportedFile(fileName, tempFile);

        } catch (IOException e) {
            logger.error("Error during TXT export", e);

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

    private void addTitle(String title) {
        String formattedTitle = title.replace("\n", " ");
        content.append(SEPARATOR_LINE).append("\n");
        content.append(centerText(formattedTitle.toUpperCase())).append("\n");
        content.append(SEPARATOR_LINE).append("\n");
    }

    private void addPageSubtitle(String subtitle) {
        content.append(SUB_SEPARATOR_LINE).append("\n");
        content.append(subtitle.toUpperCase()).append("\n");
        content.append(SUB_SEPARATOR_LINE).append("\n");
    }

    private void addAnnotationParagraph(Annotation annotation) {
        String annotationContent = sanitize(annotation.getContent());

        if (annotationContent != null && !annotationContent.isEmpty()) {
            String[] lines = annotationContent.split("\n");

            for (int blockIndex = 0; blockIndex < lines.length; blockIndex++) {
                String logicalLine = lines[blockIndex].trim();

                if (logicalLine.isEmpty()) {
                    continue;
                }

                // bullet point on the first line
                String bulletContent = (blockIndex == 0 ? "• " : "  ") + logicalLine;

                // handle line width (cut line)
                List<String> wrappedLines = wrapText(bulletContent, LINE_WIDTH - 4); // -4 indentiation

                for (int i = 0; i < wrappedLines.size(); i++) {
                    content.append("    "); // indent 4
                    content.append(wrappedLines.get(i)).append("\n");
                }
            }

            addEmptyLine();
        }
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new java.util.ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;

            if (testLine.length() > maxWidth && currentLine.length() > 0) {
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
        content.append("\n");
    }

    private String centerText(String text) {
        int padding = (LINE_WIDTH - text.length()) / 2;
        if (padding > 0) {
            return " ".repeat(padding) + text;
        }
        return text;
    }

    private String sanitize(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\t", "    ")
                .replace("\uFEFF", "")
                .replace("\u200B", "");
    }

    private String generatePathName(String title) {
        String titleWithoutWhiteSpace = title.replaceAll("\\s", "_");
        return titleWithoutWhiteSpace + ".txt";
    }
}
