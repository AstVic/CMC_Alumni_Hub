-- Distinguishes the "main admin" (owner) from regular admins.
-- Exactly one user should have owner = true at any time.
ALTER TABLE users ADD COLUMN owner BOOLEAN NOT NULL DEFAULT FALSE;

-- Promote the earliest existing admin to owner (covers already-seeded databases).
UPDATE users
SET owner = TRUE
WHERE id = (SELECT id FROM users WHERE role = 'ADMIN' ORDER BY id ASC LIMIT 1)
  AND NOT EXISTS (SELECT 1 FROM users WHERE role = 'ADMIN' AND owner = TRUE);
