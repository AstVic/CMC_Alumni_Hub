-- Alumni can publish an answer to an approved question; the answer is shown
-- publicly on the profile card next to the question.
ALTER TABLE questions ADD COLUMN answer_text  TEXT;
ALTER TABLE questions ADD COLUMN answered_at  TIMESTAMPTZ;
