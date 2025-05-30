package com.noesis.pdf_extractor_tools.core.citations_extractor.pdf.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.pdfbox.text.TextPosition;

public class FontStats implements IFontStats {
    @Override
    public float getAverageFontSize(List<TextPosition> positions) {
        Map<Float, Integer> sizeStatistics = new LinkedHashMap<>();

        for (TextPosition tp : positions) {
            String c = tp.getUnicode();
            if (!c.trim().isEmpty()) {
                float size = tp.getFontSizeInPt();

                if (sizeStatistics.containsKey(size)) {
                    sizeStatistics.put(size, sizeStatistics.get(size) + 1);
                } else {
                    sizeStatistics.put(size, 1);
                }
            }
        }

        float averageSize = 0f;
        int frequence = 0;

        for (Float size : sizeStatistics.keySet()) {
            Integer value = sizeStatistics.get(size);
            if (value > frequence) {
                averageSize = size;
                frequence = value;
            }
        }

        return averageSize;
    }

    @Override
    public Optional<Float> getMedianSize(List<TextPosition> positions) {
        if (positions.isEmpty()) {
            return Optional.empty();
        }

        List<Float> sizes = positions.stream()
                .map(pos -> pos.getFontSizeInPt())
                .filter(size -> size > 2 && size < 25)
                .sorted()
                .toList();

        if (sizes == null || sizes.isEmpty()) {
            return Optional.empty();
        }

        int listSize = sizes.size();
        float median = 0;

        if (listSize % 2 != 0) {
            median = sizes.get(listSize / 2);
        } else {
            median = (sizes.get(listSize / 2 - 1) + sizes.get(listSize / 2)) / 2;
        }

        return Optional.of(median);
    }
}
