package ru.msu.cmc.alumnihub.moderation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * MVP moderation using blacklists and heuristics. Detects profanity, spam/ads,
 * links, gibberish and too-short/too-long messages. The default provider.
 */
@Component
@ConditionalOnProperty(name = "app.moderation.provider", havingValue = "rule-based", matchIfMissing = true)
public class RuleBasedModerationProvider implements ModerationProvider {

    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 2000;

    // Obscene/insult roots (substring match, lowercase). Intentionally short list.
    private static final List<String> PROFANITY = List.of(
            "хуй", "хуё", "пизд", "ебан", "еба", "бля", "сука", "мудак", "гандон",
            "долбо", "уёбок", "идиот", "дебил", "тварь",
            "fuck", "shit", "bitch", "asshole", "bastard");

    private static final List<String> SPAM = List.of(
            "казино", "casino", "ставк", "букмекер", "кредит без", "займ", "выигр",
            "скидка", "промокод", "распродаж", "заработок", "инвестиц", "крипт",
            "porn", "viagra", "sex ", "телеграм канал", "подпишись на");

    private static final Pattern URL = Pattern.compile("(https?://|www\\.|\\bt\\.me/|@[a-zA-Z0-9_]{4,})");
    private static final Pattern REPEATED_CHAR = Pattern.compile("(.)\\1{6,}");
    private static final Pattern LETTER = Pattern.compile("\\p{L}");
    private static final Pattern VOWEL = Pattern.compile("[аеёиоуыэюяaeiouy]", Pattern.CASE_INSENSITIVE);

    @Override
    public ModerationResult moderateQuestion(String text) {
        if (text == null || text.isBlank()) {
            return ModerationResult.rejected("Пустое сообщение");
        }
        String normalized = text.trim();
        String lower = normalized.toLowerCase();

        if (normalized.length() < MIN_LENGTH) {
            return ModerationResult.rejected("Слишком короткое сообщение");
        }
        if (normalized.length() > MAX_LENGTH) {
            return ModerationResult.needsReview("Очень длинное сообщение");
        }
        if (containsAny(lower, PROFANITY)) {
            return ModerationResult.rejected("Обнаружены нецензурные выражения или оскорбления");
        }
        if (URL.matcher(lower).find()) {
            return ModerationResult.rejected("Сообщение содержит ссылки или рекламу");
        }
        if (containsAny(lower, SPAM)) {
            return ModerationResult.rejected("Похоже на спам или рекламу");
        }
        if (REPEATED_CHAR.matcher(lower).find()) {
            return ModerationResult.rejected("Бессмысленный набор символов");
        }
        if (looksLikeGibberish(normalized)) {
            return ModerationResult.needsReview("Текст выглядит подозрительно");
        }
        return ModerationResult.approved();
    }

    private boolean containsAny(String text, List<String> needles) {
        for (String needle : needles) {
            if (!needle.isEmpty() && text.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    /** Low letter ratio or no vowels among letters suggests random typing. */
    private boolean looksLikeGibberish(String text) {
        long letters = LETTER.matcher(text).results().count();
        if (letters < text.length() * 0.4) {
            return true;
        }
        long vowels = VOWEL.matcher(text).results().count();
        return letters >= 8 && vowels == 0;
    }
}
