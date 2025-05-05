ALTER TABLE candidates ALTER COLUMN weekly_commitment_hours TYPE TEXT USING weekly_commitment_hours::TEXT;

ALTER TABLE candidates ADD COLUMN social_website TEXT;

UPDATE candidates SET social_website = company_candidates.social_website FROM company_candidates
WHERE candidates.id = company_candidates.candidate_id;

ALTER TABLE company_candidates DROP COLUMN social_website;
