ALTER TABLE `characters` ALTER COLUMN `vitality` SET DEFAULT '20000';
UPDATE `characters` SET `vitality`=`vitality`*2;