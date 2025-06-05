package com.noesis.pdf_extractor_tools.core.annotations_extractor.exporter;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Color;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.Ind;
import org.docx4j.wml.PPrBase.Spacing;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Styles;
import org.docx4j.wml.Style;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noesis.pdf_extractor_tools.core.annotations_extractor.model.Annotation;
import com.noesis.pdf_extractor_tools.core.common.ExportedFile;
import com.noesis.pdf_extractor_tools.core.exception.ExportException;

public class WordAnnotationExporter implements IAnnotationExporter {

    private final LinkedHashMap<Integer, List<Annotation>> annotations;
    private final String title;

    private final ObjectFactory factory;
    private MainDocumentPart mainDocumentPart;
    private static final Logger logger = LoggerFactory.getLogger(WordAnnotationExporter.class);

    public WordAnnotationExporter(LinkedHashMap<Integer, List<Annotation>> annotations, String title) {
        this.annotations = annotations;
        this.title = title;
        this.factory = Context.getWmlObjectFactory();
    }

    @Override
    public ExportedFile export() throws IOException, ExportException {
        String fileName = generatePathName(title);
        Path tempFile = null;

        try {
            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
            StyleDefinitionsPart stylesPart = wordPackage.getMainDocumentPart().getStyleDefinitionsPart(true);

            if (stylesPart.getJaxbElement() == null) {
                Styles styles = factory.createStyles();
                stylesPart.setJaxbElement(styles);
            }

            createRequiredStyles(stylesPart);

            mainDocumentPart = wordPackage.getMainDocumentPart();

            addTitleParagraph(title);

            addEmptyParagraph();

            for (Integer pageNumber : annotations.keySet()) {
                List<Annotation> pageAnnotations = annotations.get(pageNumber);

                if (pageAnnotations != null && !pageAnnotations.isEmpty()) {
                    addPageSubtitle("Page " + pageNumber);
                    for (Annotation annotation : pageAnnotations) {
                        addAnnotationParagraph(annotation);
                    }
                }

                addEmptyParagraph();
            }
            tempFile = Files.createTempFile(title, ".pdf");
            wordPackage.save(tempFile.toFile());

            logger.info("Docx export completed");

            return new ExportedFile(fileName, tempFile);

        } catch (Docx4JException e) {
            logger.error("Error during word export", e);
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                    logger.info("Temporary file deleted after failure: {}", tempFile);
                } catch (IOException ex) {
                    logger.warn("Failed to delete temporary file: {}", tempFile, ex);
                }
            }
            throw new ExportException("Unable to export WORD for Annotation extraction");

        }
    }

    private void createRequiredStyles(StyleDefinitionsPart stylesPart) throws Docx4JException {
        // Créer le style Normal
        Style normalStyle = (Style) stylesPart.getStyleById("Normal");
        if (normalStyle == null) {
            logger.info("Style 'Normal' not found. Creating a new one.");
            normalStyle = createNormalStyle();
            stylesPart.getContents().getStyle().add(normalStyle);
        }
        configureStyleFont(normalStyle);

        // Créer le style Title
        Style titleStyle = (Style) stylesPart.getStyleById("Title");
        if (titleStyle == null) {
            logger.info("Style 'Title' not found. Creating a new one.");
            titleStyle = createTitleStyle();
            stylesPart.getContents().getStyle().add(titleStyle);
        }
    }

    private Style createTitleStyle() {
        Style style = factory.createStyle();
        style.setStyleId("Title");
        style.setName(factory.createStyleName());
        style.getName().setVal("Title");
        style.setType("paragraph");

        // Configuration du style titre
        RPr rpr = factory.createRPr();
        RFonts runFont = new RFonts();
        runFont.setAscii("Arial");
        runFont.setHAnsi("Arial");
        rpr.setRFonts(runFont);

        // Titre en gras et plus grand
        BooleanDefaultTrue bold = new BooleanDefaultTrue();
        rpr.setB(bold);

        HpsMeasure fontSize = factory.createHpsMeasure();
        fontSize.setVal(BigInteger.valueOf(32)); // 16pt
        rpr.setSz(fontSize);
        rpr.setSzCs(fontSize);

        style.setRPr(rpr);
        return style;
    }

    private void addTitleParagraph(String titleText) {
        P paragraph = factory.createP();
        R run = factory.createR();
        Text text = factory.createText();
        text.setValue(titleText);
        run.getContent().add(text);

        // Appliquer le formatage du titre
        RPr runProps = factory.createRPr();
        BooleanDefaultTrue bold = new BooleanDefaultTrue();
        runProps.setB(bold);

        HpsMeasure fontSize = factory.createHpsMeasure();
        fontSize.setVal(BigInteger.valueOf(32)); // 16pt
        runProps.setSz(fontSize);
        runProps.setSzCs(fontSize);

        RFonts runFont = new RFonts();
        runFont.setAscii("Arial");
        runFont.setHAnsi("Arial");
        runProps.setRFonts(runFont);

        run.setRPr(runProps);
        paragraph.getContent().add(run);

        // Espacement après le titre
        PPr paragraphProps = factory.createPPr();
        Spacing spacing = factory.createPPrBaseSpacing();
        spacing.setAfter(BigInteger.valueOf(240));
        paragraphProps.setSpacing(spacing);
        paragraph.setPPr(paragraphProps);

        mainDocumentPart.getContent().add(paragraph);
    }

    private Style createNormalStyle() {
        Style style = factory.createStyle();
        style.setStyleId("Normal");
        style.setName(factory.createStyleName());
        style.getName().setVal("Normal");
        style.setType("paragraph");
        style.setDefault(true);
        return style;
    }

    private void configureStyleFont(Style style) {
        RPr rpr = style.getRPr();
        if (rpr == null) {
            rpr = factory.createRPr();
            style.setRPr(rpr);
        }
        RFonts runFont = new RFonts();
        runFont.setAscii("Arial");
        runFont.setHAnsi("Arial");
        rpr.setRFonts(runFont);
    }

    private void addEmptyParagraph() {
        P paragraph = factory.createP();
        mainDocumentPart.getContent().add(paragraph);
    }

    private void addPageSubtitle(String pageTitle) {
        P paragraph = factory.createP();
        R run = factory.createR();
        Text text = factory.createText();
        text.setValue(pageTitle);
        run.getContent().add(text);

        // color
        RPr runProps = factory.createRPr();
        BooleanDefaultTrue bold = new BooleanDefaultTrue();
        runProps.setB(bold);

        Color blue = factory.createColor();
        blue.setVal("0066CC");
        runProps.setColor(blue);

        // font size
        HpsMeasure fontSize = factory.createHpsMeasure();
        fontSize.setVal(BigInteger.valueOf(28));
        runProps.setSz(fontSize);
        runProps.setSzCs(fontSize);

        run.setRPr(runProps);
        paragraph.getContent().add(run);

        // line height
        PPr paragraphProps = factory.createPPr();
        Spacing spacing = factory.createPPrBaseSpacing();
        spacing.setBefore(BigInteger.valueOf(240));
        spacing.setAfter(BigInteger.valueOf(120));
        paragraphProps.setSpacing(spacing);
        paragraph.setPPr(paragraphProps);

        mainDocumentPart.getContent().add(paragraph);
    }

    private void addAnnotationParagraph(Annotation annotation) {

        String content = annotation.getContent() != null ? annotation.getContent() : null;

        if (content != null) {
            P paragraph = factory.createP();
            R run = factory.createR();
            Text textElement = factory.createText();
            textElement.setValue(content);
            run.getContent().add(textElement);
            paragraph.getContent().add(run);

            // format as bullet list
            PPr paragraphProps = factory.createPPr();

            Ind indentation = factory.createPPrBaseInd();
            indentation.setLeft(BigInteger.valueOf(720));
            indentation.setHanging(BigInteger.valueOf(360));
            paragraphProps.setInd(indentation);

            // add bullet point
            R bulletRun = factory.createR();
            Text bulletText = factory.createText();
            bulletText.setValue("• ");
            bulletRun.getContent().add(bulletText);
            paragraph.getContent().add(0, bulletRun);

            paragraph.setPPr(paragraphProps);
            mainDocumentPart.getContent().add(paragraph);
        }

    }

    private String generatePathName(String title) {
        return title.replaceAll("\\s", "_") + ".docx";
    }

}
