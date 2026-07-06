-- Alumni cards. One profile per alumni user (1:1).
-- question_count is denormalised for cheap popularity sorting in the catalog;
-- it is incremented when a question becomes visible to the alumni.
CREATE TABLE alumni_profiles (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id               BIGINT       NOT NULL,
    full_name             VARCHAR(255),
    graduation_year       INT,
    department            VARCHAR(255),
    current_position      VARCHAR(255),
    company               VARCHAR(255),
    city                  VARCHAR(255),
    country               VARCHAR(255),
    career_description     TEXT,
    interests_description  TEXT,
    photo_url             VARCHAR(512),
    status                VARCHAR(30)  NOT NULL DEFAULT 'DRAFT',
    moderation_comment    TEXT,
    question_count        INT          NOT NULL DEFAULT 0,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    published_at          TIMESTAMPTZ,
    CONSTRAINT uq_profiles_user UNIQUE (user_id),
    CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT ck_profiles_status CHECK (status IN ('DRAFT', 'PENDING_MODERATION', 'PUBLISHED', 'REJECTED'))
);

CREATE INDEX idx_profiles_status ON alumni_profiles (status);
CREATE INDEX idx_profiles_graduation_year ON alumni_profiles (graduation_year);
CREATE INDEX idx_profiles_question_count ON alumni_profiles (question_count);
CREATE INDEX idx_profiles_company ON alumni_profiles (company);

-- Professional tags (Backend, ML, C++, Big Tech, ...).
CREATE TABLE tags (
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    slug     VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    CONSTRAINT uq_tags_slug UNIQUE (slug)
);

-- Many-to-many between profiles and tags.
CREATE TABLE alumni_profile_tags (
    profile_id BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL,
    PRIMARY KEY (profile_id, tag_id),
    CONSTRAINT fk_apt_profile FOREIGN KEY (profile_id) REFERENCES alumni_profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_apt_tag FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
);

CREATE INDEX idx_apt_tag ON alumni_profile_tags (tag_id);
