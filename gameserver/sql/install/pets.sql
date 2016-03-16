CREATE TABLE IF NOT EXISTS `pets` (
  `object_id` int(11) NOT NULL,
  `name` varchar(20) CHARACTER SET utf8 DEFAULT NULL,
  `current_hp` int(11) NOT NULL,
  `current_mp` int(11) NOT NULL,
  `exp` bigint(20) DEFAULT NULL,
  `sp` int(10) unsigned DEFAULT NULL,
  `current_life` int(11) NOT NULL,
  PRIMARY KEY (`object_id`)
);