package com.noesis.pdf_extractor_tools.core.citations_extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.noesis.pdf_extractor_tools.core.citations_extractor.annotator.BlocCitationAnnotator;
import com.noesis.pdf_extractor_tools.core.citations_extractor.annotator.ICitationAnnotator;
import com.noesis.pdf_extractor_tools.core.citations_extractor.annotator.TradCitationAnnotator;
import com.noesis.pdf_extractor_tools.core.citations_extractor.exporter.ExporterFactory;
import com.noesis.pdf_extractor_tools.core.citations_extractor.exporter.ICitationExporter;
import com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.Extractor;
import com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.citation.bloc.BlocCitationExtractor;
import com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.citation.bloc.IBlocCitationExtractor;
import com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.citation.harvard.HarvardCitationExtractor;
import com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.citation.harvard.IHarvardCitationExtractor;
import com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.citation.trad.ITradCitationExtractor;
import com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.citation.trad.TradCitationExtractor;
import com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.footnote.FootnoteExtractor;
import com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.footnote.IFootnoteExtractor;
import com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.note.INoteDetector;
import com.noesis.pdf_extractor_tools.core.citations_extractor.extractor.note.NoteDetector;
import com.noesis.pdf_extractor_tools.core.citations_extractor.footnoteAssociator.BlocCitationFootnoteAssociator;
import com.noesis.pdf_extractor_tools.core.citations_extractor.footnoteAssociator.IFootnoteAssociator;
import com.noesis.pdf_extractor_tools.core.citations_extractor.footnoteAssociator.TradCitationFootnoteAssociator;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedBlocCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedTradCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.context.ExporterContext;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.footnote.Footnote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.result.AllTypeCitationsResult;
import com.noesis.pdf_extractor_tools.core.citations_extractor.pdf.utils.CoordStats;
import com.noesis.pdf_extractor_tools.core.citations_extractor.pdf.utils.FontStats;
import com.noesis.pdf_extractor_tools.core.citations_extractor.pdf.utils.ICoordStats;
import com.noesis.pdf_extractor_tools.core.citations_extractor.pdf.utils.IFontStats;
import com.noesis.pdf_extractor_tools.core.common.ExportFormats;
import com.noesis.pdf_extractor_tools.core.common.ExportedFile;
import com.noesis.pdf_extractor_tools.core.exception.ExportException;
import com.noesis.pdf_extractor_tools.core.exception.ExtractException;


@Component
public class CoreCitationsExtractorService implements ICoreCitationsExtractorService {

    private static final Logger logger = LoggerFactory.getLogger(CoreCitationsExtractorService.class);

    @Override
    public List<ExportedFile> extract(InputStream pdfInput, List<ExportFormats> formats, String title) throws IOException, ExtractException, ExportException {
        AllTypeCitationsResult extractionResult = getCitationsResult(pdfInput);
        if (extractionResult != null) {
            ExporterContext exporterContext = generateContext(title, extractionResult);
            return getExtractionResult(exporterContext, formats);
        }
        throw new ExtractException();
    }

    private AllTypeCitationsResult getCitationsResult(InputStream pdfInput) throws ExtractException {

        try (RandomAccessRead rar = new RandomAccessReadBuffer(pdfInput);
                PDDocument document = Loader.loadPDF(rar)) {
            IFontStats fontStats = new FontStats();
            INoteDetector noteDetector = new NoteDetector();
            ITradCitationExtractor citationExtractor = new TradCitationExtractor();
            ICitationAnnotator<TradCitation, AnnotatedTradCitation> citationAnnotator = new TradCitationAnnotator();
            IHarvardCitationExtractor harvardExtractor = new HarvardCitationExtractor();
            IBlocCitationExtractor blocExtractor = new BlocCitationExtractor();
            ICitationAnnotator<BlocCitation, AnnotatedBlocCitation> blocCitationAnnotator = new BlocCitationAnnotator();
            IFootnoteExtractor footnoteExtractor = new FootnoteExtractor();
            IFootnoteAssociator<TradCitationWithNote, AnnotatedTradCitation, Footnote> tradFootnoteAssociator = new TradCitationFootnoteAssociator();
            IFootnoteAssociator<BlocCitationWithNote, AnnotatedBlocCitation, Footnote> blocFootnoteAssociator = new BlocCitationFootnoteAssociator();
            ICoordStats coordStats = new CoordStats();

            Extractor extractor = new Extractor(fontStats, noteDetector, citationExtractor, citationAnnotator,
                    harvardExtractor, blocExtractor, blocCitationAnnotator, footnoteExtractor, tradFootnoteAssociator,
                    blocFootnoteAssociator, coordStats);

            AllTypeCitationsResult citationsPerPage = extractor.extractAll(document);

            return citationsPerPage;

        } catch (Exception e) {
            logger.error("Error during citations extraction.");
            throw new ExtractException();
        }
    }

    private List<ExportedFile> getExtractionResult(ExporterContext exporterContext,
            List<ExportFormats> formats) throws IOException, ExportException {

        List<ExportedFile> exportedFiles = new ArrayList<>();

        ExporterFactory exporterFactory = new ExporterFactory();

        List<ICitationExporter> citationExporters = exporterFactory.getExporter(exporterContext, formats);

        for (ICitationExporter exporter : citationExporters) {
            ExportedFile exportedFile = exporter.export();
            exportedFiles.add(exportedFile);
        }

        return exportedFiles;
    }

    private ExporterContext generateContext(String title, AllTypeCitationsResult extractedCitations) {
        String exportTitle = title == null ? "Export Result" : title;
        return new ExporterContext(extractedCitations.tradCitations(),
                extractedCitations.harvardCitations(), extractedCitations.blocCitations(), exportTitle);
    }


}
