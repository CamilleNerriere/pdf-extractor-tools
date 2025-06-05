package com.noesis.pdf_extractor_tools.middleware;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.noesis.pdf_extractor_tools.web.utils.HttpResponseUtils;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class StrictRateLimiter implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(StrictRateLimiter.class);
    /* TODO : Add Redis Cache in Prod */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket strictBucket(String ip) {
        return buckets.computeIfAbsent(ip,
                k -> Bucket.builder()
        .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
        .build());
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws IOException {
        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();
        Bucket bucket = strictBucket(ip);
        if(!bucket.tryConsume(1)){
            long waitForTokenSeconds = bucket.estimateAbilityToConsume(1)
                .getNanosToWaitForRefill() / 1_000_000_000L;
            HttpResponseUtils.sendRateLimitExceeded(response, waitForTokenSeconds);
            logger.warn("Too many requests from IP {} on URL {}", ip, url);
            return false;
        }
        return true;
    }

}
