ALTER TABLE candidates ADD COLUMN wallet_address TEXT;
UPDATE candidates SET wallet_address = 'placeholder_wallet_address' WHERE wallet_address IS NULL;
ALTER TABLE candidates ALTER COLUMN wallet_address SET NOT NULL;
