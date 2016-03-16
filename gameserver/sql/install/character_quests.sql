CREATE TABLE IF NOT EXISTS `character_quests` (
  `char_id` int(11) NOT NULL,
  `quest_id` int(11) NOT NULL,
  `var` varchar(20) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`char_id`,`quest_id`,`var`),
  KEY `char_id` (`char_id`),
  KEY `quest_id` (`quest_id`)
);