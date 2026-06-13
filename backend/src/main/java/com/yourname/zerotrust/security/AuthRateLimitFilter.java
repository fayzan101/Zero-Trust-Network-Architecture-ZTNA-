package com.yourname.zerotrust.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourname.zerotrust.dto.ApiErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MS = 60_000;

    private final Map<String, List<Long>> requestLog = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!isRateLimitedPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = resolveClientKey(request);
        if (isRateLimited(clientKey)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(),
                    new ApiErrorResponse(429, "Too Many Requests",
                            "Rate limit exceeded — try again in a minute"));
            return;
        }

        recordRequest(clientKey);
        filterChain.doFilter(request, response);
    }

    private boolean isRateLimitedPath(String path) {
        return path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.equals("/api/auth/mfa");
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private boolean isRateLimited(String clientKey) {
        long now = System.currentTimeMillis();
        List<Long> timestamps = requestLog.getOrDefault(clientKey, List.of());
        long recentCount = timestamps.stream().filter(ts -> now - ts < WINDOW_MS).count();
        return recentCount >= MAX_REQUESTS;
    }

    private void recordRequest(String clientKey) {
        long now = System.currentTimeMillis();
        requestLog.compute(clientKey, (key, timestamps) -> {
            List<Long> updated = new ArrayList<>();
            if (timestamps != null) {
                timestamps.stream()
                        .filter(ts -> now - ts < WINDOW_MS)
                        .forEach(updated::add);
            }
            updated.add(now);
            return updated;
        });
    }
}
