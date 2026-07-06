package ru.msu.cmc.alumnihub.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Minimal endpoints used to verify the production authorization policy. */
@RestController
public class SecurityTestController {

    @GetMapping("/actuator/health")
    public String health() {
        return "UP";
    }

    @GetMapping("/swagger-ui/index.html")
    public String swagger() {
        return "swagger";
    }

    @GetMapping("/api/admin/test")
    public String admin() {
        return "admin";
    }
}
