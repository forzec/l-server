ALTER TABLE clan_subpledges ADD COLUMN `upgraded` INT NOT NULL DEFAULT '0';
ALTER TABLE clan_data ADD COLUMN `disband_end` INT NOT NULL DEFAULT '0';
ALTER TABLE clan_data ADD COLUMN `disband_penalty` INT NOT NULL DEFAULT '0';

UPDATE clan_subpledges SET `upgraded` = 1 WHERE type <> 0 AND type <> -1;
