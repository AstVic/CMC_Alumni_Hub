-- Invitations can now target either an alumni or an admin account.
ALTER TABLE alumni_invites
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'ALUMNI';

ALTER TABLE alumni_invites
    ADD CONSTRAINT ck_invites_role CHECK (role IN ('ADMIN', 'ALUMNI'));
