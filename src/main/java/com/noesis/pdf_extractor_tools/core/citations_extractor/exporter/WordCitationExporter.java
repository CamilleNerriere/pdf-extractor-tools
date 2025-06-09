package com.noesis.pdf_extractor_tools.core.citations_extractor.exporter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedHarvardCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExporterContext;
import com.noesis.pdf_extractor_tools.core.common.ExportedFile;
import com.noesis.pdf_extractor_tools.core.exception.ExportException;

public class WordCitationExporter implements ICitationExporter {

    private static final Logger logger = LoggerFactory.getLogger(WordCitationExporter.class);

    private final String title;
    private final LinkedHashMap<Integer, List<TradCitationWithNote>> tradCitations;
    private final LinkedHashMap<Integer, List<BlocCitationWithNote>> blocCitations;
    private final LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations;

    public WordCitationExporter(ExporterContext context) {
        this.title = context.getTitle();
        this.tradCitations = context.getTradCitations();
        this.blocCitations = context.getBlocCitations();
        this.harvardCitations = context.getHarvardCitations();
    }

    @Override
    public ExportedFile export() throws IOException, ExportException {

        String fileName = generatePathName(title);
        Path tempFile = null;
        XWPFDocument document = null;
        FileOutputStream out = null;

        try {
            document = new XWPFDocument();

            addTitle(document, title);
            addLineBreak(document);

            Set<Integer> allPages = new TreeSet<>();
            allPages.addAll(tradCitations.keySet());
            allPages.addAll(blocCitations.keySet());
            allPages.addAll(harvardCitations.keySet());

            for (int page : allPages) {
                boolean hasClassical = tradCitations.get(page) != null && !tradCitations.get(page).isEmpty();
                boolean hasBloc = blocCitations.get(page) != null && !blocCitations.get(page).isEmpty();
                boolean hasHarvard = harvardCitations.get(page) != null && !harvardCitations.get(page).isEmpty();

                if (!hasClassical && !hasHarvard && !hasBloc) {
                    continue;
                }

                addPageTitle(document, "Page " + page);
                addLineBreak(document);

                // classical citation treatment
                List<TradCitationWithNote> classical = tradCitations.get(page);
                if (classical != null && !classical.isEmpty()) {
                    addSectionTitle(document, "Classical Type Citation");
                    for (TradCitationWithNote citation : classical) {
                        String cit = citation.getContent();
                        String noteNumber = citation.getNoteNumber();
                        String footnote = citation.getFootnote();
                        addCitationEntry(document, "Note " + noteNumber + " : " + cit, "Footnote : " + footnote);
                    }
                    addLineBreak(document);
                }

                // havard citation treatment
                List<AnnotatedHarvardCitation> harvard = harvardCitations.get(page);
                if (harvard != null && !harvard.isEmpty()) {
                    addSectionTitle(document, "Harvard Type Citation");
                    for (AnnotatedHarvardCitation citation : harvard) {
                        String cit = citation.getContent();
                        String note = citation.getNoteContent();
                        addCitationEntry(document, cit, "Note : " + note);
                    }
                    addLineBreak(document);
                }

                // bloc citation treatment
                List<BlocCitationWithNote> bloc = blocCitations.get(page);
                if (bloc != null && !bloc.isEmpty()) {
                    addSectionTitle(document, "Bloc Type Citation");
                    for (BlocCitationWithNote citation : bloc) {
                        String cit = citation.getContent();
                        String noteNumber = citation.getNoteNumber();
                        String footnote = citation.getFootnote();
                        addCitationEntry(document, "Note " + noteNumber + " : " + cit, "Footnote : " + footnote);
                    }
                    addLineBreak(document);
                }
            }

            tempFile = Files.createTempFile(sanitizeFileName(title), ".docx");

            out = new FileOutputStream(tempFile.toFile());
            document.write(out);

            logger.info("Docx export completed successfully");

            return new ExportedFile(fileName, tempFile);

        } catch (IOException e) {
            logger.error("Error during word export", e);

            cleanupTempFile(tempFile);

            throw new ExportException("Unable to export WORD for Citation extraction");

        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (document != null) {
                    document.close();
                }
            } catch (IOException e) {
                logger.warn("Error closing resources", e);
            }
        }
    }

    private void addTitle(XWPFDocument document, String titleText) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        titleParagraph.setSpacingAfter(300); // Espacement après le titre

        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(titleText != null ? titleText : "Citations Document");
        titleRun.setBold(true);
        titleRun.setFontSize(18);
        titleRun.setFontFamily("Arial");
    }

    private void addPageTitle(XWPFDocument document, String pageTitle) {
        XWPFParagraph pageParagraph = document.createParagraph();
        pageParagraph.setSpacingBefore(300); 
        pageParagraph.setSpacingAfter(150); 

        XWPFRun pageRun = pageParagraph.createRun();
        pageRun.setText(pageTitle);
        pageRun.setBold(true);
        pageRun.setFontSize(16);
        pageRun.setColor("2c44ad"); 
        pageRun.setFontFamily("Arial");
    }

    private void addSectionTitle(XWPFDocument document, String sectionTitle) {
        XWPFParagraph sectionParagraph = document.createParagraph();
        sectionParagraph.setSpacingBefore(200);
        sectionParagraph.setSpacingAfter(100);

        XWPFRun sectionRun = sectionParagraph.createRun();
        sectionRun.setText(sectionTitle);
        sectionRun.setBold(true);
        sectionRun.setFontSize(13);
        sectionRun.setColor("0066CC"); 
        sectionRun.setFontFamily("Arial");
    }

    private void addCitationEntry(XWPFDocument document, String citation, String note) {
        //citation
        if (citation != null && !citation.trim().isEmpty()) {
            XWPFParagraph citationParagraph = document.createParagraph();
            citationParagraph.setIndentationLeft(360); 
            citationParagraph.setSpacingAfter(50);

            XWPFRun citationRun = citationParagraph.createRun();
            citationRun.setText("• " + citation);
            citationRun.setFontFamily("Arial");
            citationRun.setFontSize(11);
        }

        // note
        if (note != null && !note.trim().isEmpty()) {
            XWPFParagraph noteParagraph = document.createParagraph();
            noteParagraph.setIndentationLeft(720); // Plus d'indentation pour la note
            noteParagraph.setSpacingAfter(100);

            XWPFRun noteRun = noteParagraph.createRun();
            noteRun.setText("→ " + note);
            noteRun.setFontFamily("Arial");
            noteRun.setFontSize(10);
            noteRun.setItalic(true);
            noteRun.setColor("666666"); 
        }
    }

    private void addLineBreak(XWPFDocument document) {
        document.createParagraph();
    }

    private void cleanupTempFile(Path tempFile) {
        if (tempFile != null) {
            try {
                Files.deleteIfExists(tempFile);
                logger.info("Temporary file deleted after failure: {}", tempFile);
            } catch (IOException ex) {
                logger.warn("Failed to delete temporary file: {}", tempFile, ex);
            }
        }
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "citations_document";
        }
        // Remplacer les caractères problématiques
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_").substring(0, Math.min(fileName.length(), 50));
    }

    private String generatePathName(String title) {
        String safeName = title != null ? title.replaceAll("\\s", "_") : "citations_document";
        return safeName + ".docx";
    }

}