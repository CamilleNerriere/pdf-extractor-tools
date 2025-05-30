package com.noesis.pdf_extractor_tools.core.citations_extractor.exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

public class TxtCitationExporter implements ICitationExporter {

    private final ExporterContext context;

    private static final Logger logger = LoggerFactory.getLogger(TxtCitationExporter.class);

    public TxtCitationExporter(ExporterContext context) {
        this.context = context;

    }

    @Override
    public ExportedFile export() throws IOException {
        LinkedHashMap<Integer, List<TradCitationWithNote>> tradCitations = context.getTradCitations();
        LinkedHashMap<Integer, List<BlocCitationWithNote>> blocCitations = context.getBlocCitations();
        LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations = context.getHarvardCitations();
        String title = context.getTitle();

        StringBuilder txt = new StringBuilder();
        txt.append("-----------------").append(title).append("---------------- \n");
        txt.append(System.lineSeparator());

        Set<Integer> allPages = new TreeSet<>();
        allPages.addAll(tradCitations.keySet());
        allPages.addAll(harvardCitations.keySet());

        for (int page : allPages) {

            boolean hasClassical = tradCitations.get(page) != null && !tradCitations.get(page).isEmpty();
            boolean hasBloc = blocCitations.get(page) != null && !blocCitations.get(page).isEmpty();
            boolean hasHarvard = harvardCitations.get(page) != null && !harvardCitations.get(page).isEmpty();

            if (!hasClassical && !hasHarvard && !hasBloc) {
                continue;
            }

            txt.append(System.lineSeparator());
            txt.append("---------Page ").append(page).append("---------");
            txt.append(System.lineSeparator());

            // Classical
            List<TradCitationWithNote> classical = tradCitations.get(page);
            if (classical != null && !classical.isEmpty()) {
                txt.append(System.lineSeparator());
                txt.append("--------------Classical Type Citation------------");
                txt.append(System.lineSeparator());

                for (TradCitationWithNote citation : classical) {
                    String cit = citation.getBaseAnnotatedCitation().getBaseCitation().getText();
                    String noteNumber = citation.getBaseAnnotatedCitation().getNoteNumber();
                    String footnote = citation.getFootnote();

                    txt.append(System.lineSeparator());
                    txt.append("Note ").append(noteNumber).append(" : ").append(cit).append("\n");
                    txt.append("Footnote : ").append(footnote);
                    txt.append(System.lineSeparator());
                }
            }

            // Harvard
            List<AnnotatedHarvardCitation> harvard = harvardCitations.get(page);
            if (harvard != null && !harvard.isEmpty()) {
                txt.append(System.lineSeparator());
                txt.append("--------------Harvard Type Citation------------");
                txt.append(System.lineSeparator());

                for (AnnotatedHarvardCitation citation : harvard) {
                    String cit = citation.getBaseCitation().getText();
                    String note = citation.getNoteContent();

                    txt.append(System.lineSeparator());
                    txt.append(cit).append("\n");
                    txt.append("Note : ").append(note);
                    txt.append(System.lineSeparator());
                }
            }

            // Bloc
            List<BlocCitationWithNote> bloc = blocCitations.get(page);
            if (bloc != null && !bloc.isEmpty()) {
                txt.append(System.lineSeparator());
                txt.append("--------------Bloc Type Citation------------");
                txt.append(System.lineSeparator());

                for (BlocCitationWithNote citation : bloc) {
                    String cit = citation.getBaseAnnotatedCitation().getBaseCitation().getText();
                    String noteNumber = citation.getBaseAnnotatedCitation().getNoteNumber();
                    String footnote = citation.getFootnote();

                    txt.append(System.lineSeparator());
                    txt.append("Note ").append(noteNumber).append(" : ").append(cit).append("\n");
                    txt.append("Footnote : ").append(footnote);
                    txt.append(System.lineSeparator());
                }
            }

        }

        return new ExportedFile(title, generateTempFile(txt, title));
    }

    private String generatePathName(String title) {
        return title.replaceAll("\\s", "_") + ".txt";
    }

    private Path generateTempFile(StringBuilder txt, String fileName) throws IOException {
        Path tempFile = Files.createTempFile(fileName, ".pdf");

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
            writer.write(txt.toString());
        } catch (IOException e){
            logger.warn("Error during txt export", e);
        }

        logger.info("Txt export completed");
        return tempFile;
    }
}
