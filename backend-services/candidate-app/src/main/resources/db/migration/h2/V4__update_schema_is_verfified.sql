ALTER TABLE candidates ADD COLUMN is_verified BOOLEAN;
UPDATE candidates SET is_verified = false WHERE is_verified IS NULL;
ALTER TABLE candidates ALTER COLUMN is_verified SET NOT NULL;
