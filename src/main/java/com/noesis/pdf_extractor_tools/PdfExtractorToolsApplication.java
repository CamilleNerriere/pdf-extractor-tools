package com.noesis.pdf_extractor_tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.noesis.pdf_extractor_tools.model")
public class PdfExtractorToolsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfExtractorToolsApplication.class, args);
	}

}
