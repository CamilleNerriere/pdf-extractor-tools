package com.noesis.pdf_extractor_tools.core.citations_extractor.pdf.utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MathUtils {
    static float getMedian(List<Float> values) {
        if (values.isEmpty())
            return 0f;

        List<Float> sorted = new ArrayList<>(values);
        Collections.sort(sorted);

        int middle = sorted.size() / 2;
        if (sorted.size() % 2 == 0) {
            return (sorted.get(middle - 1) + sorted.get(middle)) / 2f;
        } else {
            return sorted.get(middle);
        }
    }
}
