package ru.msu.cmc.alumnihub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point of the CMC Alumni Hub backend.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class AlumniHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlumniHubApplication.class, args);
    }
}
