CREATE TABLE IF NOT EXISTS `summon_effects` (
  `object_id` int(11) NOT NULL,
  `call_skill_id` int(11) NOT NULL,
  `skill_id` int(11) NOT NULL,
  `skill_level` int(11) NOT NULL,
  `effect_count` int(11) NOT NULL,
  `effect_cur_time` int(11) NOT NULL,
  `duration` int(11) NOT NULL,
  `order` int(11) NOT NULL,
  PRIMARY KEY (`object_id`,`call_skill_id`,`skill_id`)
);