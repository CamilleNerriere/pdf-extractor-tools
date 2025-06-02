package com.noesis.pdf_extractor_tools.core.citations_extractor.exporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedHarvardCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExporterContext;
import com.noesis.pdf_extractor_tools.core.common.ExportedFile;

public class TxtCitationExporter implements ICitationExporter {

    private static final Logger logger = LoggerFactory.getLogger(TxtCitationExporter.class);
    private final String title;

    private final LinkedHashMap<Integer, List<TradCitationWithNote>> tradCitations;
    private final LinkedHashMap<Integer, List<BlocCitationWithNote>> blocCitations;
    private final LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations;

    private StringBuilder content;

    private static final String SEPARATOR_LINE = "=" + "=".repeat(80) + "=";
    private static final String SUB_SEPARATOR_LINE = "-" + "-".repeat(40) + "-";
    private static final int LINE_WIDTH = 80;

    public TxtCitationExporter(ExporterContext context) {
        this.title = context.getTitle();
        this.tradCitations = context.getTradCitations();
        this.blocCitations = context.getBlocCitations();
        this.harvardCitations = context.getHarvardCitations();
    }

    @Override
    public ExportedFile export() throws IOException {

        Path tempFile = null;
        String fileName = generatePathName(title);

        Set<Integer> allPages = new TreeSet<>();
        allPages.addAll(tradCitations.keySet());
        allPages.addAll(harvardCitations.keySet());
        allPages.addAll(blocCitations.keySet());

        try {
            addTitle(title);
            addEmptyLine();

            for (Integer pageNumber : allPages) {

                List<TradCitationWithNote> classical = tradCitations.get(pageNumber);
                List<AnnotatedHarvardCitation> harvard = harvardCitations.get(pageNumber);
                List<BlocCitationWithNote> bloc = blocCitations.get(pageNumber);

                boolean isClassicalEmpty = classical == null || classical.isEmpty();
                boolean isHarvardEmpty = harvard == null || harvard.isEmpty();
                boolean isBlocEmpty = bloc == null || bloc.isEmpty();

                if (isClassicalEmpty && isHarvardEmpty && isBlocEmpty) {
                    continue;
                }

                addEmptyLine();
                addPageSubtitle("Page : " + pageNumber);
                addEmptyLine();

                if (!isClassicalEmpty) {
                    addEmptyLine();
                    addPageSubtitle("Classical Citations");
                    addEmptyLine();

                    for (TradCitationWithNote citation : classical) {
                        addTradCitation(citation);
                    }
                }

                if (!isHarvardEmpty) {
                    addEmptyLine();
                    addPageSubtitle("Havard Citations");
                    addEmptyLine();

                    for (AnnotatedHarvardCitation citation : harvard) {
                        addHarvardCitation(citation);
                    }
                }

                if (!isBlocEmpty) {
                    addEmptyLine();
                    addPageSubtitle("Bloc Citations");
                    addEmptyLine();

                    for (BlocCitationWithNote citation : bloc) {
                        addBlocCitation(citation);
                    }
                }

                tempFile = Files.createTempFile(title, ".txt");
                Files.write(tempFile, content.toString().getBytes("UTF-8"));
                logger.info("TXT export completed");

                return new ExportedFile(fileName, tempFile);

            }
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

        return null;
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

    private void addTradCitation(TradCitationWithNote citation) {

        String citationContent = sanitize(citation.getContent());
        String footnoteContent = sanitize(citation.getFootnote());
        String noteNumber = citation.getNoteNumber();

        boolean isCitationEmpty = citationContent == null || citationContent.isEmpty();
        boolean isFootnoteEmpty = footnoteContent == null || footnoteContent.isEmpty();

        if (!isCitationEmpty) {
            addEmptyLine();
            content.append("Citation : ").append(noteNumber).append(" : ").append(citationContent).append("\n");
            if (!isFootnoteEmpty) {
                content.append("Footnote : ").append(footnoteContent);
            }
            addEmptyLine();
        }
    }

    private void addBlocCitation(BlocCitationWithNote citation) {
        String citationContent = sanitize(citation.getContent());
        String footnoteContent = sanitize(citation.getFootnote());
        String noteNumber = citation.getNoteNumber();

        boolean isCitationEmpty = citationContent == null || citationContent.isEmpty();
        boolean isFootnoteEmpty = footnoteContent == null || footnoteContent.isEmpty();

        if (!isCitationEmpty) {
            addEmptyLine();
            content.append("Citation : ").append(noteNumber).append(" : ").append(citationContent).append("\n");
            if (!isFootnoteEmpty) {
                content.append("Footnote : ").append(footnoteContent);
            }
            addEmptyLine();
        }
    }

    private void addHarvardCitation(AnnotatedHarvardCitation citation) {
        String citationContent = sanitize(citation.getContent());
        String noteContent = sanitize(citation.getNoteContent());

        boolean isCitationEmpty = citationContent == null || citationContent.isEmpty();
        boolean isNoteEmpty = noteContent == null || noteContent.isEmpty();

        if (!isCitationEmpty) {
            addEmptyLine();
            content.append("Citation : ").append(citationContent).append("\n");

            if (!isNoteEmpty) {
                content.append("Note : ").append(noteContent).append("\n");
            }
            addEmptyLine();
        }

    }

    private String centerText(String text) {
        int padding = (LINE_WIDTH - text.length()) / 2;
        if (padding > 0) {
            return " ".repeat(padding) + text;
        }
        return text;
    }

    private void addEmptyLine() {
        content.append("\n");
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
        return title.replaceAll("\\s", "_") + ".txt";
    }
}
