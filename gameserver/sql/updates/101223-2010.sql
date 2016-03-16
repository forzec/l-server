ALTER TABLE `characters` ADD COLUMN `rec_bonus_time` INT NOT NULL DEFAULT '3600' AFTER `rec_left`;
ALTER TABLE `characters` ALTER COLUMN `rec_left` SET DEFAULT '20';
UPDATE `characters` SET `rec_left`=`rec_left`+20;