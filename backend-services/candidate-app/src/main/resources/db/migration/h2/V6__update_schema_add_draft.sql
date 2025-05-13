ALTER TABLE candidates ADD COLUMN is_draft TEXT;

UPDATE candidates SET is_draft = false WHERE is_draft IS NULL;

ALTER TABLE candidates ALTER COLUMN is_draft SET NOT NULL;
