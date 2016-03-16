CREATE TABLE IF NOT EXISTS `summons` (
  `object_id` int(11) NOT NULL,
  `call_skill_id` int(11) NOT NULL DEFAULT '0',
  `current_hp` int(11) NOT NULL,
  `current_mp` int(11) NOT NULL,
  `current_life` int(11) NOT NULL,
  `max_life` int(11) NOT NULL DEFAULT '0',
  `npc_id` int(11) NOT NULL DEFAULT '0',
  `exp_penalty` double NOT NULL DEFAULT '0',
  `item_consume_id` int(11) NOT NULL DEFAULT '0',
  `item_consume_count` int(11) NOT NULL DEFAULT '0',
  `item_consume_delay` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`object_id`,`call_skill_id`)
);