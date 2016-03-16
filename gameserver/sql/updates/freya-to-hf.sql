ALTER TABLE `character_shortcuts` CHANGE `char_obj_id` `object_id` INT( 11 ) NOT NULL;
ALTER TABLE `character_shortcuts` ADD `character_type` INT( 11 ) NOT NULL DEFAULT '1';

ALTER TABLE characters ADD `hunt_points` INT NOT NULL DEFAULT '0' AFTER `rec_bonus_time`;
ALTER TABLE characters ADD `hunt_time` INT NOT NULL DEFAULT '14400' AFTER `hunt_points`;

-- castle
ALTER TABLE castle CHANGE `taxPercent` `tax_percent` INT NOT NULL;

-- fortress
ALTER TABLE forts RENAME TO fortress;
ALTER TABLE fortress CHANGE `castleId` `castle_id` INT NOT NULL;
ALTER TABLE fortress ADD COLUMN `supply_count` bigint(20) NOT NULL;

-- siege date columns
ALTER TABLE clanhall ADD COLUMN `last_siege_date` bigint(20) NOT NULL;
ALTER TABLE clanhall ADD COLUMN `own_date` bigint(20) NOT NULL;
ALTER TABLE clanhall ADD COLUMN `siege_date` bigint(20) NOT NULL;
--
ALTER TABLE fortress ADD COLUMN `last_siege_date` bigint(20) NOT NULL;
ALTER TABLE fortress CHANGE `ownDate` `own_date` bigint(20) NOT NULL;
update fortress set own_date=own_date * 1000;
ALTER TABLE fortress ADD COLUMN `siege_date` bigint(20) NOT NULL;
--
ALTER TABLE castle ADD COLUMN `last_siege_date` bigint(20) NOT NULL;
ALTER TABLE castle CHANGE `ownDate` `own_date` bigint(20) NOT NULL;
update castle set own_date=own_date * 1000;
ALTER TABLE castle ADD COLUMN `siege_date` bigint(20) NOT NULL;

-- facility
ALTER TABLE fortress ADD COLUMN `facility_0` INT NOT NULL;
ALTER TABLE fortress ADD COLUMN `facility_1` INT NOT NULL;
ALTER TABLE fortress ADD COLUMN `facility_2` INT NOT NULL;
ALTER TABLE fortress ADD COLUMN `facility_3` INT NOT NULL;
ALTER TABLE fortress ADD COLUMN `facility_4` INT NOT NULL;

ALTER TABLE items ADD COLUMN `agathion_energy` INT NOT NULL;

ALTER TABLE siege_clans ADD COLUMN `param` BIGINT NOT NULL;
ALTER TABLE siege_clans ADD `date` BIGINT NOT NULL;

-- olympiad
ALTER TABLE olympiad_nobles DROP `char_name`;
alter table olympiad_nobles add column game_classes_count int not null;
alter table olympiad_nobles add column game_noclasses_count int not null;
alter table olympiad_nobles add column game_team_count int not null;
--
alter table clan_data drop column auction_bid_at;

ALTER TABLE clanhall ADD COLUMN auction_length INT NOT NULL;
ALTER TABLE clanhall ADD COLUMN auction_desc TEXT NULL;
ALTER TABLE clanhall ADD COLUMN auction_min_bid BIGINT NOT NULL;

-- cycle system
ALTER TABLE clanhall ADD COLUMN cycle INT NOT NULL;
ALTER TABLE fortress ADD COLUMN cycle INT NOT NULL;

-- reward
ALTER TABLE fortress ADD COLUMN reward_count INT NOT NULL;
ALTER TABLE castle ADD COLUMN reward_count INT NOT NULL;

-- paid
ALTER TABLE fortress ADD COLUMN paid_cycle INT NOT NULL;
ALTER TABLE clanhall ADD COLUMN paid_cycle INT NOT NULL;

-- remove deprecated skills
DELETE FROM character_skills WHERE skill_id = 1387;

ALTER TABLE character_variables CHANGE `expire_time` `expire_time` BIGINT(20) NOT NULL;

-- deprecated quests
DELETE FROM character_quests WHERE name in ('_694_BreakThroughTheHallOfSuffering', '_695_DefendtheHallofSuffering', '_696_ConquertheHallofErosion', '_697_DefendtheHallofErosion', '_698_BlocktheLordsEscape');
DELETE FROM character_quests WHERE name in ('_353_PowerOfDarkness', '_374_WhisperOfDreams1', '_375_WhisperOfDreams2');
DELETE FROM character_quests WHERE name in ('_734_PierceThroughAShield', '_735_MakeSpearsDull', '_736_WeakenMagic', '_737_DenyBlessings', '_738_DestroyKeyTargets');
DELETE FROM character_quests WHERE name in ('_998_BattleForGlory');

-- перед этим уведомить что б все забрали свои итемы, в замках и в фортах, что б непотерять
DELETE FROM server_variables WHERE name LIKE 'ReciveBloodAlli_%';
DELETE FROM server_variables WHERE name LIKE 'ReciveBloodOath_%';

-- конвертация экспы у персонажей
UPDATE character_subclasses SET exp = 1511275834 + (exp - 1511275834) * 0.90648259 WHERE
exp >= 1511275834 AND exp < 2099275834;
UPDATE character_subclasses SET exp = 2044287599 + (exp - 2099275834) * 0.49110615 WHERE 
exp >= 2099275834 AND exp < 4200000000;
UPDATE character_subclasses SET exp = 3075966164 + (exp - 4200000000) * 0.580659898 WHERE 
exp >= 4200000000 AND exp < 6300000000;
UPDATE character_subclasses SET exp = 4295351949 + (exp - 6300000000) * 0.583981394 WHERE 
exp >= 6300000000 AND exp < 8820000000;
UPDATE character_subclasses SET exp = 5766985062 + (exp - 8820000000) * 0.67000406 WHERE 
exp >= 8820000000 AND exp < 11844000000;
UPDATE character_subclasses SET exp = 7793077345 + (exp - 11844000000) * 0.6730301 WHERE 
exp >= 11844000000 AND exp < 15472800000;
UPDATE character_subclasses SET exp = 10235368963 + (exp - 15472800000) * 0.67632829 WHERE 
exp >= 15472800000 AND exp < 19827360000;
UPDATE character_subclasses SET exp = 13180481103 + (exp - 19827360000) * 2.21144288 WHERE 
exp >= 19827360000;

-- instance ids revamped - remove all existing reuses
TRUNCATE character_instances;

TRUNCATE residence_functions;

update clanhall set auction_min_bid = 0, auction_length = 0, siege_date = 0;
update fortress set siege_date = 0;