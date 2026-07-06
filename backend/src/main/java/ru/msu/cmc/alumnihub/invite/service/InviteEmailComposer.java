package ru.msu.cmc.alumnihub.invite.service;

import org.springframework.stereotype.Component;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;
import ru.msu.cmc.alumnihub.user.entity.Role;

/**
 * Builds the invitation email (subject + body) with the registration link,
 * adapted to the invited role (alumni or admin).
 */
@Component
public class InviteEmailComposer {

    private final String frontendUrl;

    public InviteEmailComposer(AppProperties appProperties) {
        this.frontendUrl = appProperties.frontendUrl();
    }

    public String inviteLink(String rawToken) {
        return frontendUrl + "/invite/register?token=" + rawToken;
    }

    public String subject(Role role) {
        return role == Role.ADMIN
                ? "Приглашение в администраторы CMC Alumni Hub"
                : "Приглашение в CMC Alumni Hub";
    }

    public String body(String rawToken, Role role) {
        return role == Role.ADMIN ? adminBody(rawToken) : alumniBody(rawToken);
    }

    private String alumniBody(String rawToken) {
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
                """.formatted(inviteLink(rawToken));
    }

    private String adminBody(String rawToken) {
        return """
                Здравствуйте!

                Вас пригласили стать администратором платформы CMC Alumni Hub.

                По ссылке ниже вы можете создать аккаунт администратора и задать пароль:

                %s

                Ссылка действительна 7 дней.

                После регистрации вам будут доступны модерация карточек и вопросов, \
                управление приглашениями выпускников и тегами.

                Если вы не ожидали это приглашение, просто проигнорируйте письмо.

                С уважением,
                команда CMC Alumni Hub
                """.formatted(inviteLink(rawToken));
    }
}
