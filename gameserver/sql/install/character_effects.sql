CREATE TABLE IF NOT EXISTS `character_effects` (
	`object_id` INT NOT NULL,
	`skill_id` INT NOT NULL,
	`skill_level` INT NOT NULL,
	`effect_count` INT NOT NULL,
	`effect_cur_time` INT NOT NULL,
	`duration` INT NOT NULL,
	`order` INT NOT NULL,
	`class_index` INT NOT NULL,
	PRIMARY KEY (`object_id`,`skill_id`,`class_index`)
) ENGINE=MyISAM;