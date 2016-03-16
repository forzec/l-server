ALTER TABLE fortress DROP `blood_oath_count`;
ALTER TABLE castle DROP `blood_alliance_count`;

-- cycle system
ALTER TABLE clanhall ADD COLUMN cycle INT NOT NULL;
ALTER TABLE castle ADD COLUMN cycle INT NOT NULL;
ALTER TABLE fortress ADD COLUMN cycle INT NOT NULL;

-- reward
ALTER TABLE fortress ADD COLUMN reward_count INT NOT NULL;
ALTER TABLE castle ADD COLUMN reward_count INT NOT NULL;

-- paid
ALTER TABLE fortress ADD COLUMN paid_cycle INT NOT NULL;
ALTER TABLE clanhall ADD COLUMN paid_cycle INT NOT NULL;