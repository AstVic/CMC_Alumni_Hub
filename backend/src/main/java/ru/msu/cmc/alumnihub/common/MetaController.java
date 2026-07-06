package ru.msu.cmc.alumnihub.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Lightweight liveness / meta endpoint. Public and unauthenticated so that
 * Docker healthchecks and the frontend can confirm the API is reachable.
 */
@RestController
@RequestMapping("/api/public/meta")
public class MetaController {

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "app", appName,
                "status", "UP",
                "time", Instant.now().toString()
        );
    }
}
