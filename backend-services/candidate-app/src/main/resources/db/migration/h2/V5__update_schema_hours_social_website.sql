ALTER TABLE candidates ALTER COLUMN weekly_commitment_hours TYPE TEXT USING weekly_commitment_hours::TEXT;

ALTER TABLE candidates ADD COLUMN social_website TEXT;

UPDATE candidates
SET social_website = (
    SELECT cc.social_website
    FROM company_candidates cc
    WHERE cc.candidate_id = candidates.id
);

ALTER TABLE company_candidates DROP COLUMN social_website;
