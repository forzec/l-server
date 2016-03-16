DROP TABLE IF EXISTS forums;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS topic;

DROP TABLE IF EXISTS `bbs_clannotice`;
CREATE TABLE `bbs_clannotice` (
`clan_id` INT UNSIGNED NOT NULL,
`type` SMALLINT NOT NULL DEFAULT '0',
`notice` text NOT NULL,
PRIMARY KEY(`clan_id`,`type`)
) TYPE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `bbs_favorites`;
CREATE TABLE `bbs_favorites` (
`fav_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
`object_id` INT(15) UNSIGNED NOT NULL,
`fav_bypass` VARCHAR(35) NOT NULL,
`fav_title` VARCHAR(100) NOT NULL,
`add_date` INT(15) UNSIGNED NOT NULL,
PRIMARY KEY(`fav_id`),
INDEX(`object_id`)
) TYPE=MyISAM DEFAULT CHARSET=utf8;
ALTER TABLE bbs_favorites ADD UNIQUE INDEX ix_obj_id_bypass (object_id, fav_bypass);

DROP TABLE IF EXISTS `bbs_mail`;
CREATE TABLE `bbs_mail` (
`message_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
`to_name` VARCHAR(35) NOT NULL,
`to_object_id` INT UNSIGNED NOT NULL,
`from_name` VARCHAR(35) NOT NULL,
`from_object_id` INT UNSIGNED NOT NULL,
`title` VARCHAR(128) NOT NULL,
`message` TEXT NOT NULL,
`post_date` INT(15) UNSIGNED NOT NULL,
`read` SMALLINT NOT NULL DEFAULT '0',
`box_type` SMALLINT NOT NULL DEFAULT '0',
PRIMARY KEY(`message_id`),
INDEX(`to_object_id`),
INDEX(`from_object_id`),
INDEX(`read`),
INDEX(`box_type`)
) TYPE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `bbs_memo`;
CREATE TABLE `bbs_memo` (
`memo_id` int(11) NOT NULL auto_increment,
`account_name` varchar(45) NOT NULL,
`char_name` varchar(35) NOT NULL,
`ip` varchar(16) NOT NULL,
`title` varchar(128) NOT NULL,
`memo` text NOT NULL,
`post_date` INT(15) UNSIGNED NOT NULL,
PRIMARY KEY(`memo_id`),
INDEX(account_name)
) TYPE=MyISAM DEFAULT CHARSET=utf8;