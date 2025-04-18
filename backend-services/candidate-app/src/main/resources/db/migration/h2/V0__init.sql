-- Base candidates table
CREATE TABLE candidates (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    country TEXT NOT NULL,
    social_x TEXT,
    social_linkedin TEXT,
    social_discord TEXT,
    social_telegram TEXT,
    social_other TEXT,
    public_contact TEXT NOT NULL,
    candidate_type TEXT NOT NULL,

    -- General Candidate Information
    about TEXT,
    bio TEXT,
    additional_info TEXT,
    video_presentation_link TEXT,

    -- Additional Candidate Information
    reason_to_serve TEXT,
    governance_experience TEXT,
    communication_strategy TEXT,
    ecosystem_contributions TEXT,
    legal_expertise TEXT,
    weekly_commitment_hours INTEGER,

    -- Verifications
    x_verification TEXT,
    conflict_of_interest TEXT,
    drep_id TEXT,
    stake_id TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for individual candidates
CREATE TABLE individual_candidates (
    candidate_id INTEGER PRIMARY KEY REFERENCES candidates(id) ON DELETE CASCADE
    -- No additional fields currently required
);

-- Table for company candidates
CREATE TABLE company_candidates (
    candidate_id INTEGER PRIMARY KEY REFERENCES candidates(id) ON DELETE CASCADE,
    registration_number TEXT NOT NULL,
    key_contact_person TEXT NOT NULL,
    social_website TEXT
);

-- Table for consortium candidates
CREATE TABLE consortium_candidates (
    candidate_id INTEGER PRIMARY KEY REFERENCES candidates(id) ON DELETE CASCADE
    -- Additional fields could go here in future
);

-- Table for consortium members
CREATE TABLE consortium_members (
    id SERIAL PRIMARY KEY,
    consortium_id INTEGER NOT NULL REFERENCES consortium_candidates(candidate_id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    country TEXT NOT NULL,
    bio TEXT,
    social_x TEXT,
    social_linkedin TEXT,
    social_discord TEXT,
    social_telegram TEXT,
    social_other TEXT,
    x_verification TEXT,
    conflict_of_interest TEXT,
    drep_id TEXT,
    stake_id TEXT
);
