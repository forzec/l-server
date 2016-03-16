CREATE TABLE IF NOT EXISTS `character_servitors` (
  `object_id` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `servitor_id` int(11) NOT NULL,
  PRIMARY KEY (`object_id`,`servitor_id`)
);