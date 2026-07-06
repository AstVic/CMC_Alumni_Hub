package ru.msu.cmc.alumnihub.moderation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;

/**
 * Placeholder for a real AI moderation API, activated with
 * {@code app.moderation.provider=ai}. The wiring (endpoint/key from env) is in
 * place; the actual API call is a TODO. Until implemented it defers to admin
 * review so nothing is auto-published.
 */
@Component
@ConditionalOnProperty(name = "app.moderation.provider", havingValue = "ai")
public class AiModerationProvider implements ModerationProvider {

    private static final Logger log = LoggerFactory.getLogger(AiModerationProvider.class);

    private final String apiUrl;
    private final String apiKey;

    public AiModerationProvider(AppProperties appProperties) {
        this.apiUrl = appProperties.moderation().aiApiUrl();
        this.apiKey = appProperties.moderation().aiApiKey();
    }

    @Override
    public ModerationResult moderateQuestion(String text) {
        // TODO: call the configured AI moderation API (apiUrl/apiKey) and map
        //  its response to a ModerationResult. For now be conservative.
        log.warn("AI moderation provider is not yet implemented (apiUrl set={}). "
                + "Deferring to admin review.", apiUrl != null && !apiUrl.isBlank());
        return ModerationResult.needsReview("Ожидает ручной проверки (AI-провайдер не настроен)");
    }
}
