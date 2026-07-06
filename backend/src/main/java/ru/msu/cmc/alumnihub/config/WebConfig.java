package ru.msu.cmc.alumnihub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;

import java.nio.file.Paths;

/**
 * Serves uploaded images statically from the local upload directory.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadDir;

    public WebConfig(AppProperties appProperties) {
        this.uploadDir = appProperties.storage().uploadDir();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(uploadDir).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
