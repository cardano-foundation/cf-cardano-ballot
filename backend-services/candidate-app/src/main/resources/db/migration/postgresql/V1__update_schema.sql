ALTER TABLE candidates ADD COLUMN cold_credentials TEXT;
ALTER TABLE candidates ADD COLUMN governance_action_rationale TEXT;

ALTER TABLE candidates ALTER COLUMN public_contact DROP NOT NULL;
ALTER TABLE candidates ALTER COLUMN country DROP NOT NULL;

ALTER TABLE company_candidates ALTER COLUMN key_contact_person DROP NOT NULL;
ALTER TABLE company_candidates ALTER COLUMN registration_number DROP NOT NULL;

ALTER TABLE consortium_members ALTER COLUMN country DROP NOT NULL;
ALTER TABLE consortium_members ALTER COLUMN name DROP NOT NULL;
