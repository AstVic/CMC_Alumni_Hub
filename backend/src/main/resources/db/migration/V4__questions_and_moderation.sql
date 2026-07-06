-- Questions asked by anonymous visitors to a specific alumni profile.
CREATE TABLE questions (
    id                      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    alumni_profile_id       BIGINT       NOT NULL,
    sender_name             VARCHAR(255),
    sender_email            VARCHAR(255),
    question_text           TEXT         NOT NULL,
    status                  VARCHAR(30)  NOT NULL DEFAULT 'PENDING_MODERATION',
    ai_moderation_status    VARCHAR(30),
    ai_moderation_reason    TEXT,
    admin_moderation_comment TEXT,
    is_read_by_alumni       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT fk_questions_profile FOREIGN KEY (alumni_profile_id) REFERENCES alumni_profiles (id) ON DELETE CASCADE,
    CONSTRAINT ck_questions_status CHECK (status IN (
        'PENDING_MODERATION', 'AI_APPROVED', 'AI_REJECTED', 'PENDING_ADMIN_REVIEW',
        'APPROVED_BY_ADMIN', 'REJECTED_BY_ADMIN', 'VISIBLE_TO_ALUMNI', 'ARCHIVED'
    )),
    CONSTRAINT ck_questions_ai_status CHECK (ai_moderation_status IS NULL OR ai_moderation_status IN (
        'PENDING', 'APPROVED', 'REJECTED', 'NEEDS_REVIEW'
    ))
);

CREATE INDEX idx_questions_profile ON questions (alumni_profile_id);
CREATE INDEX idx_questions_status ON questions (status);
CREATE INDEX idx_questions_created_at ON questions (created_at);

-- Audit log of moderation decisions for both profiles and questions.
-- Polymorphic reference (entity_type + entity_id); no hard FK by design.
CREATE TABLE moderation_logs (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    entity_type    VARCHAR(20) NOT NULL,          -- PROFILE | QUESTION
    entity_id      BIGINT      NOT NULL,
    moderator_type VARCHAR(20) NOT NULL,          -- AI | ADMIN
    decision       VARCHAR(30) NOT NULL,
    reason         TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_modlog_entity_type CHECK (entity_type IN ('PROFILE', 'QUESTION')),
    CONSTRAINT ck_modlog_moderator_type CHECK (moderator_type IN ('AI', 'ADMIN'))
);

CREATE INDEX idx_modlog_entity ON moderation_logs (entity_type, entity_id);
