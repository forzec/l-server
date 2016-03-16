ALTER TABLE clan_data ADD COLUMN castle_defend_count INT NOT NULL DEFAULT '0';

UPDATE clan_data SET castle_defend_count=(SELECT reward_count FROM castle WHERE id=hasCastle) WHERE hasCastle > 0;

ALTER TABLE castle DROP COLUMN reward_count;

ALTER TABLE clan_data DROP COLUMN auction_bid_at;