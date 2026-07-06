package ru.msu.cmc.alumnihub.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/** Emits a compact, secret-free startup marker useful in deployment logs. */
@Component
public class StartupLogger {

    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);
    private final Environment environment;

    public StartupLogger(Environment environment) {
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logReady() {
        String[] active = environment.getActiveProfiles();
        String profiles = active.length == 0
                ? Arrays.toString(environment.getDefaultProfiles())
                : Arrays.toString(active);
        log.info("CMC Alumni Hub backend is ready (profiles={})", profiles);
    }
}
