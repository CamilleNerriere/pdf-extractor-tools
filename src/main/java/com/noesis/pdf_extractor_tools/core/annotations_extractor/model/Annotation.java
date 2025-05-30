package com.noesis.pdf_extractor_tools.core.annotations_extractor.model;

public class Annotation {
    private final int page; 
    private final String type;
    private final String content;

    public Annotation(final int page, final String type, final String content){
        this.page = page;
        this.type = type;
        this.content = content;
    }

    public int getPage(){
        return page;
    }

    public String getType(){
        return type;
    }

    public String getContent(){
        return content;
    }

    @Override
    public String toString(){
        return "Annotation{" + 
                "page = " + page +
                ", type = " + type + 
                ", content = " + content +
                "}";
    }
}
