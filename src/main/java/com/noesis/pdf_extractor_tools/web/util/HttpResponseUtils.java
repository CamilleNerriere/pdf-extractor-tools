package com.noesis.pdf_extractor_tools.web.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;

public class HttpResponseUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponseUtils.class);

    public static void sendRateLimitExceeded(HttpServletResponse response, int retryAfterSeconds) {
        try {
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
            response.getWriter().write("Rate limit exceeded. Try again later.");
        } catch (IOException e) {
            logger.error("Unable to write rate limit response", e);
        }
    }
}
