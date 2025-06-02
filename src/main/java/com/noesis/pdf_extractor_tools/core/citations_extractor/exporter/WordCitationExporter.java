package com.noesis.pdf_extractor_tools.core.citations_extractor.exporter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Color;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedHarvardCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExporterContext;
import com.noesis.pdf_extractor_tools.core.common.ExportedFile;

public class WordCitationExporter implements ICitationExporter {
    
    private static final Logger logger = LoggerFactory.getLogger(WordCitationExporter.class);
    
    private final String title;
    private final LinkedHashMap<Integer, List<TradCitationWithNote>> tradCitations;
    private final LinkedHashMap<Integer, List<BlocCitationWithNote>> blocCitations;
    private final LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations;
    
    private WordprocessingMLPackage wordPackage;
    private MainDocumentPart mainDocumentPart;
    private final ObjectFactory factory;

    public WordCitationExporter(ExporterContext context) {
        this.title = context.getTitle();
        this.tradCitations = context.getTradCitations();
        this.blocCitations = context.getBlocCitations();
        this.harvardCitations = context.getHarvardCitations();
        this.factory = Context.getWmlObjectFactory();
    }

    @Override
    public ExportedFile export() throws IOException {
        String fileName = generatePathName(title);
        Path tempFile = null;

        try {
            initializeDocument();

            // main title
            addTitle(title);
            addLineBreak();

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

                addPageTitle("Page " + page);
                addLineBreak();

                // classical citations
                List<TradCitationWithNote> classical = tradCitations.get(page);
                if (classical != null && !classical.isEmpty()) {
                    addSectionTitle("Classical Type Citation");

                    for (TradCitationWithNote citation : classical) {
                        String cit = citation.getContent();
                        String noteNumber = citation.getNoteNumber();
                        String footnote = citation.getFootnote();

                        addCitationEntry("Note " + noteNumber + " : " + cit, "Footnote : " + footnote);
                    }
                    addLineBreak();
                }

                // havard citation
                List<AnnotatedHarvardCitation> harvard = harvardCitations.get(page);
                if (harvard != null && !harvard.isEmpty()) {
                    addSectionTitle("Harvard Type Citation");

                    for (AnnotatedHarvardCitation citation : harvard) {
                        String cit = citation.getContent();
                        String note = citation.getNoteContent();

                        addCitationEntry(cit, "Note : " + note);
                    }
                    addLineBreak();
                }

                // bloc citation
                List<BlocCitationWithNote> bloc = blocCitations.get(page);
                if (bloc != null && !bloc.isEmpty()) {
                    addSectionTitle("Bloc Type Citation");

                    for (BlocCitationWithNote citation : bloc) {
                        String cit = citation.getContent();
                        String noteNumber = citation.getNoteNumber();
                        String footnote = citation.getFootnote();

                        addCitationEntry("Note " + noteNumber + " : " + cit, "Footnote : " + footnote);
                    }
                    addLineBreak();
                }

            }

            tempFile = Files.createTempFile(title, ".pdf");
            wordPackage.save(tempFile.toFile());

            logger.info("Docx export completed");

            return new ExportedFile(fileName, tempFile);

        } catch (IOException | Docx4JException e) {

            logger.warn("Error during docx export", e);
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

    private void initializeDocument() throws Docx4JException {
        wordPackage = WordprocessingMLPackage.createPackage();
        mainDocumentPart = wordPackage.getMainDocumentPart();
    }

    private void addTitle(String titleText) {
        P paragraph = factory.createP();

        // bold and center
        PPr pPr = factory.createPPr();
        Jc justification = factory.createJc();
        justification.setVal(JcEnumeration.CENTER);
        pPr.setJc(justification);
        paragraph.setPPr(pPr);

        R run = factory.createR();
        RPr rPr = factory.createRPr();
        BooleanDefaultTrue bold = factory.createBooleanDefaultTrue();
        rPr.setB(bold);

        RFonts fonts = factory.createRFonts();
        fonts.setAscii("Arial");
        fonts.setHAnsi("Arial");
        rPr.setRFonts(fonts);

        Color color = factory.createColor();
        color.setVal("073869");
        rPr.setColor(color);

        // font size
        HpsMeasure fontSize = factory.createHpsMeasure();
        fontSize.setVal(BigInteger.valueOf(32)); // 16pt
        rPr.setSz(fontSize);
        rPr.setSzCs(fontSize);

        run.setRPr(rPr);

        Text text = factory.createText();
        text.setValue(titleText);
        run.getContent().add(text);
        paragraph.getContent().add(run);

        mainDocumentPart.getContent().add(paragraph);
    }

    private void addPageTitle(String titleText) {
        P paragraph = factory.createP();

        R run = factory.createR();
        RPr rPr = factory.createRPr();
        BooleanDefaultTrue bold = factory.createBooleanDefaultTrue();
        rPr.setB(bold);

        RFonts fonts = factory.createRFonts();
        fonts.setAscii("Arial");
        fonts.setHAnsi("Arial");
        rPr.setRFonts(fonts);

        Color color = factory.createColor();
        color.setVal("0066CC");
        rPr.setColor(color);

        // Taille de police moyenne
        HpsMeasure fontSize = factory.createHpsMeasure();
        fontSize.setVal(BigInteger.valueOf(28)); // 14pt
        rPr.setSz(fontSize);
        rPr.setSzCs(fontSize);

        run.setRPr(rPr);

        Text text = factory.createText();
        text.setValue(titleText);
        run.getContent().add(text);
        paragraph.getContent().add(run);

        mainDocumentPart.getContent().add(paragraph);
    }

    private void addSectionTitle(String titleText) {
        P paragraph = factory.createP();

        R run = factory.createR();
        RPr rPr = factory.createRPr();
        BooleanDefaultTrue bold = factory.createBooleanDefaultTrue();
        rPr.setB(bold);

        RFonts fonts = factory.createRFonts();
        fonts.setAscii("Arial");
        fonts.setHAnsi("Arial");
        rPr.setRFonts(fonts);

        Color color = factory.createColor();
        color.setVal("334e69");
        rPr.setColor(color);

        HpsMeasure fontSize = factory.createHpsMeasure();
        fontSize.setVal(BigInteger.valueOf(24)); // 12pt
        rPr.setSz(fontSize);
        rPr.setSzCs(fontSize);

        run.setRPr(rPr);

        Text text = factory.createText();
        text.setValue(titleText);
        run.getContent().add(text);
        paragraph.getContent().add(run);

        mainDocumentPart.getContent().add(paragraph);
    }

    private void addCitationEntry(String citation, String footnote) {
        // citation
        P citationParagraph = factory.createP();
        R citationRun = factory.createR();

        RPr citationRPr = factory.createRPr();
        RFonts fonts = factory.createRFonts();
        fonts.setAscii("Arial");
        fonts.setHAnsi("Arial");
        citationRPr.setRFonts(fonts);
        citationRun.setRPr(citationRPr);

        Text citationText = factory.createText();
        citationText.setValue(citation);
        citationRun.getContent().add(citationText);
        citationParagraph.getContent().add(citationRun);
        mainDocumentPart.getContent().add(citationParagraph);

        // note
        P footnoteParagraph = factory.createP();
        R footnoteRun = factory.createR();
        RPr footnoteRPr = factory.createRPr();
        BooleanDefaultTrue italic = factory.createBooleanDefaultTrue();
        footnoteRPr.setI(italic);

        RFonts footnoteFonts = factory.createRFonts();
        footnoteFonts.setAscii("Arial");
        footnoteFonts.setHAnsi("Arial");
        footnoteRPr.setRFonts(footnoteFonts);

        footnoteRun.setRPr(footnoteRPr);

        Text footnoteText = factory.createText();
        footnoteText.setValue(footnote);
        footnoteRun.getContent().add(footnoteText);
        footnoteParagraph.getContent().add(footnoteRun);
        mainDocumentPart.getContent().add(footnoteParagraph);

        addLineBreak();
    }

    private void addLineBreak() {
        P paragraph = factory.createP();
        mainDocumentPart.getContent().add(paragraph);
    }

    private void saveDocument(String outputPath) throws IOException, Docx4JException {

        if (!outputPath.toLowerCase().endsWith(".docx")) {
            outputPath += ".docx";
        }

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            wordPackage.save(fos);
        }
    }

    private String generatePathName(String title) {
        return title.replaceAll("\\s", "_") + ".docx";
    }
}
