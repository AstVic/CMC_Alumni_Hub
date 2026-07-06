package ru.msu.cmc.alumnihub.invite.service;

import org.springframework.stereotype.Component;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;

/**
 * Builds the alumni invitation email (subject + body) with the registration link.
 */
@Component
public class InviteEmailComposer {

    public static final String SUBJECT = "Приглашение в CMC Alumni Hub";

    private final String frontendUrl;

    public InviteEmailComposer(AppProperties appProperties) {
        this.frontendUrl = appProperties.frontendUrl();
    }

    public String inviteLink(String rawToken) {
        return frontendUrl + "/invite/register?token=" + rawToken;
    }

    public String body(String rawToken) {
        String link = inviteLink(rawToken);
        return """
                Здравствуйте!

                Вас пригласили присоединиться к CMC Alumni Hub — платформе для \
                взаимодействия студентов ВМК МГУ с выпускниками факультета.

                По ссылке ниже вы можете создать аккаунт выпускника и заполнить свою карточку:

                %s

                Ссылка действительна 7 дней.

                После регистрации вы сможете рассказать о своём карьерном пути, выбрать \
                профессиональные теги и получать вопросы от студентов через платформу.

                Если вы не ожидали это приглашение, просто проигнорируйте письмо.

                С уважением,
                команда CMC Alumni Hub
                """.formatted(link);
    }
}
