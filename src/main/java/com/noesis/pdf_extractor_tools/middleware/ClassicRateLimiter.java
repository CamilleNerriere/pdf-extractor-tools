package com.noesis.pdf_extractor_tools.middleware;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class ClassicRateLimiter implements HandlerInterceptor {
    /* TODO : Add Redis Cache in Prod */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket generalBucket(String ip) {
        return buckets.computeIfAbsent(ip,
                k -> Bucket.builder()
        .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(15))))
        .build());
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws IOException {
        String ip = request.getRemoteAddr();
        Bucket bucket = generalBucket(ip);
        if(!bucket.tryConsume(1)){
            HttpResponseUtils.sendRateLimitExceeded(response, 60);
            return false;
        }
        return true;
    }

}
