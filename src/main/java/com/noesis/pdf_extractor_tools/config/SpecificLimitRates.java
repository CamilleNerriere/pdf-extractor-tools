package com.noesis.pdf_extractor_tools.config;

import java.time.Duration;

import org.springframework.stereotype.Component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Component
public class SpecificLimitRates {
    public final Bucket citationBucket = Bucket.builder()
        .addLimit(Bandwidth.classic(3, Refill.intervally(1, Duration.ofMinutes(1))))
        .build();

    public final Bucket annotationBucket = Bucket.builder()
        .addLimit(Bandwidth.classic(1, Refill.intervally(2, Duration.ofMinutes(1))))
        .build();
}
