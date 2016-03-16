DROP TABLE IF EXISTS banned_ips, loginserv_log, referrals;

ALTER TABLE `accounts` CHANGE `login` `login` VARCHAR( 32 ) NOT NULL,
CHANGE `password` `password` VARCHAR( 255 ) NOT NULL,
CHANGE `lastactive` `last_access` INT NOT NULL DEFAULT '0',
CHANGE `access_level` `access_level` INT NOT NULL DEFAULT '0',
CHANGE `lastIP` `last_ip` VARCHAR( 15 ) DEFAULT NULL,
CHANGE `lastServer` `last_server` INT NOT NULL DEFAULT '0',
CHANGE `bonus` `bonus` DOUBLE NOT NULL DEFAULT '1.0',
CHANGE `banExpires` `ban_expire` INT NOT NULL DEFAULT '0',
CHANGE `AllowIPs` `allow_ip` VARCHAR( 255 ) NOT NULL DEFAULT '',
DROP `comments`,
DROP `email`,
DROP `pay_stat`,
DROP `lock_expire`,
DROP `activated`,
DROP INDEX `bonus`,
DROP INDEX `access_level`,
ADD INDEX ( `last_ip` ) ;

ALTER TABLE `gameservers` CHANGE `server_id` `server_id` INT( 11 ) NOT NULL ,
CHANGE `host` `host` VARCHAR( 255 ) NOT NULL ,
DROP `hexid` ;