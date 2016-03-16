CREATE TABLE IF NOT EXISTS `character_tp_bookmarks` (
  `object_id` int(11) NOT NULL,
  `name` varchar(32) CHARACTER SET utf8 NOT NULL,
  `acronym` varchar(4) CHARACTER SET utf8 NOT NULL,
  `icon` int(3) unsigned NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  PRIMARY KEY (`object_id`,`name`,`x`,`y`,`z`)
);