package ru.msu.cmc.alumnihub.common.ratelimit;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight in-memory fixed-window rate limiter keyed by an identifier
 * (typically the client IP). Dependency-free; sufficient for a single-instance
 * MVP. For a clustered deployment this would move to Redis/Bucket4j.
 */
@Service
public class RateLimiterService {

    private record Window(long windowStartMillis, int count) {
    }

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    /**
     * @return true if the action is allowed, false if the limit is exceeded.
     */
    public boolean tryConsume(String key, int maxRequests, Duration window) {
        long now = System.currentTimeMillis();
        long windowMillis = window.toMillis();

        Window updated = windows.compute(key, (k, current) -> {
            if (current == null || now - current.windowStartMillis() >= windowMillis) {
                return new Window(now, 1);
            }
            return new Window(current.windowStartMillis(), current.count() + 1);
        });

        return updated.count() <= maxRequests;
    }
}
