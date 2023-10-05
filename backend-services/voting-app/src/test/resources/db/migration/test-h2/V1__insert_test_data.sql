INSERT INTO vote (
    id,
    id_numeric_hash,
    event_id,
    category_id,
    proposal_id,
    voter_stake_address,
    cose_signature,
    cose_public_key,
    voting_power,
    voted_at_slot,
    created_at,
    updated_at
) VALUES (
    'a5ad0ab5-8ea6-487c-a578-78bcc5155a59',
    1234567890,
    'CF_TEST_EVENT_03',
    'CHANGE_MAYBE',
    'MAYBE',
    'stake_test1uzn083tm8erradk0lwzzkegewdtwj6mukk2ep2r03g9j87g0020y2',
    'xxx',
    'xxx',
    3021,
    412439,
    NOW(),
    NOW()
);

-- Sample 2
INSERT INTO vote (
    id,
    id_numeric_hash,
    event_id,
    category_id,
    proposal_id,
    voter_stake_address,
    cose_signature,
    cose_public_key,
    voting_power,
    voted_at_slot,
    created_at,
    updated_at
) VALUES (
    '72fe8757-0a2c-49d5-b147-e6d6077b2e85',
    9876543210,
    'CF_TEST_EVENT_03',
    'CHANGE_MAYBE',
    'NO',
    'stake_test1uq840xayy0xcxsymzmmxppk556hflksw2mnkeydra4eukyq9wt492',
    'xxx',
    'xxx',
    5009,
    412439,
    NOW(),
    NOW()
);

-- Sample 3
INSERT INTO vote (
    id,
    id_numeric_hash,
    event_id,
    category_id,
    proposal_id,
    voter_stake_address,
    cose_signature,
    cose_public_key,
    voting_power,
    voted_at_slot,
    created_at,
    updated_at
) VALUES (
    '0c6e0fd5-a926-4dba-9cfc-93f33743d8e3',
    5555555555,
    'CF_TEST_EVENT_03',
    'CHANGE_MAYBE',
    'NO',
    'stake_test1urr837mdv93allvtghm2wvg9skrhhth3rcvk3mfs2af3v7qfl2pn8',
    'xxx',
    'xxx',
    121,
    412439,
    NOW(),
    NOW()
);