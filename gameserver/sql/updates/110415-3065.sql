-- castle
ALTER TABLE castle ADD COLUMN `last_siege_date` bigint(20) NOT NULL;
ALTER TABLE castle ADD COLUMN `own_date` bigint(20) NOT NULL;
ALTER TABLE castle ADD COLUMN `siege_date` bigint(20) NOT NULL;
ALTER TABLE castle ADD COLUMN `blood_alliance_count` bigint(20) NOT NULL;
ALTER TABLE castle CHANGE `taxPercent` `tax_percent` INT NOT NULL;
ALTER TABLE castle CHANGE `townId` `town_id` INT NOT NULL;
-- fortress
ALTER TABLE forts RENAME TO fortress;
ALTER TABLE fortress CHANGE `castleId` `castle_id` INT NOT NULL;
ALTER TABLE fortress ADD COLUMN `last_siege_date` bigint(20) NOT NULL;
ALTER TABLE fortress ADD COLUMN `own_date` bigint(20) NOT NULL;
ALTER TABLE fortress ADD COLUMN `siege_date` bigint(20) NOT NULL;
ALTER TABLE fortress ADD COLUMN `blood_oath_count` bigint(20) NOT NULL;
ALTER TABLE fortress ADD COLUMN `supply_count` bigint(20) NOT NULL;
ALTER TABLE fortress ADD COLUMN `guard_buff_level` bigint(20) NOT NULL;
-- clanhall
ALTER TABLE clanhall ADD COLUMN `last_siege_date` bigint(20) NOT NULL;
ALTER TABLE clanhall ADD COLUMN `own_date` bigint(20) NOT NULL;
ALTER TABLE clanhall ADD COLUMN `siege_date` bigint(20) NOT NULL;