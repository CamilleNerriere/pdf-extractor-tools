package com.noesis.pdf_extractor_tools.core.citations_extractor.model.context;

import java.util.LinkedHashMap;
import java.util.List;

import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.AnnotatedHarvardCitation;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.BlocCitationWithNote;
import com.noesis.pdf_extractor_tools.core.citations_extractor.model.citation.TradCitationWithNote;

public class ExporterContext {

    private final LinkedHashMap<Integer, List<TradCitationWithNote>> tradCitations;
    private final LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations;
    private final LinkedHashMap<Integer, List<BlocCitationWithNote>> blocCitations;
    private final String title;

    public ExporterContext(final LinkedHashMap<Integer, List<TradCitationWithNote>> tradCitations,
    final LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations, LinkedHashMap<Integer, List<BlocCitationWithNote>> blocCitations, String title) {
        this.tradCitations = tradCitations;
        this.harvardCitations = harvardCitations;
        this.blocCitations = blocCitations;
        this.title = title;
    }

    public LinkedHashMap<Integer, List<TradCitationWithNote>> getTradCitations(){
        return tradCitations;
    }

    public LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> getHarvardCitations(){
        return harvardCitations;
    }

    public LinkedHashMap<Integer, List<BlocCitationWithNote>> getBlocCitations(){
        return blocCitations;
    }

    public String getTitle(){
        return title;
    }

}
