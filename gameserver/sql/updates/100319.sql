ALTER TABLE `accounts`  ADD COLUMN `activated` TINYINT(10) UNSIGNED NOT NULL DEFAULT '1' AFTER `lock_expire`;

CREATE TABLE IF NOT EXISTS `lock` (
	`login` varchar(45) NOT NULL,
	`type` ENUM('HWID','IP') NOT NULL,
	`string` varchar(32)  NOT NULL,
	PRIMARY KEY  (`login`,`string`)
) ENGINE=MyISAM;

--INSERT INTO `lock` SELECT DISTINCT `Account`, 'HWID', `HWID` FROM `hwids_log`;
--INSERT INTO `lock` SELECT DISTINCT `Account`, 'IP', `IP` FROM `hwids_log`;
--INSERT INTO `lock` SELECT `login`, 'IP', `AllowIPs` FROM `accounts`;
