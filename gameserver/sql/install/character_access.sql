CREATE TABLE IF NOT EXISTS `character_access` (
  `object_id` int(11) NOT NULL,
  `password_enable` int(11) NOT NULL,
  `password` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`object_id`)
);