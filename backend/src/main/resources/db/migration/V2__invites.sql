-- One-time email invitations issued by admins for alumni self-registration.
-- Only the SHA-256 hash of the token is stored, never the raw token.
CREATE TABLE alumni_invites (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email               VARCHAR(255) NOT NULL,
    token_hash          VARCHAR(255) NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'CREATED',
    created_by_admin_id BIGINT       NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    expires_at          TIMESTAMPTZ  NOT NULL,
    used_at             TIMESTAMPTZ,
    revoked_at          TIMESTAMPTZ,
    note                TEXT,
    CONSTRAINT uq_invites_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_invites_admin FOREIGN KEY (created_by_admin_id) REFERENCES users (id),
    CONSTRAINT ck_invites_status CHECK (status IN ('CREATED', 'SENT', 'USED', 'EXPIRED', 'REVOKED'))
);

CREATE INDEX idx_invites_email ON alumni_invites (email);
CREATE INDEX idx_invites_status ON alumni_invites (status);
CREATE INDEX idx_invites_expires_at ON alumni_invites (expires_at);
