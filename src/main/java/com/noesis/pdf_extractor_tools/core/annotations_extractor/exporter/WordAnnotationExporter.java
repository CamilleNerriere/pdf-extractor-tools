package com.noesis.pdf_extractor_tools.core.annotations_extractor.exporter;

import java.io.File;
import java.math.BigInteger;
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
import org.docx4j.wml.Style;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noesis.pdf_extractor_tools.core.annotations_extractor.model.Annotation;

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
    public void export() {

        try {
            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
            StyleDefinitionsPart stylesPart = wordPackage.getMainDocumentPart().getStyleDefinitionsPart();
            Style style = (Style) stylesPart.getStyleById("Normal");
            RPr rpr = style.getRPr();
            if (rpr == null) {
                rpr = factory.createRPr();
                style.setRPr(rpr);
            }
            RFonts runFont = new RFonts();
            runFont.setAscii("Arial");
            runFont.setHAnsi("Arial");
            rpr.setRFonts(runFont);
            mainDocumentPart = wordPackage.getMainDocumentPart();
            mainDocumentPart.addStyledParagraphOfText("Title", title);

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

            File exportFile = new File(generatePathName(title));
            wordPackage.save(exportFile);
            logger.info("Word Export ended successfully : {}", exportFile.getAbsolutePath());

        } catch (Docx4JException e) {
            logger.error("Error during word export", e);
        }
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
