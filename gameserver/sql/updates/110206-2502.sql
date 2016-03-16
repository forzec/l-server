ALTER TABLE `character_effects_save` CHANGE `id` `id` INT( 11 ) NOT NULL,
CHANGE `skill_id` `skill_id` INT NOT NULL,
CHANGE `skill_level` `skill_level` INT NOT NULL,
CHANGE `effect_count` `effect_count` INT NOT NULL,
CHANGE `effect_cur_time` `effect_cur_time` INT NOT NULL,
CHANGE `duration` `duration` INT NOT NULL,
CHANGE `order` `order` INT NOT NULL,
CHANGE `class_index` `class_index` INT NOT NULL;

ALTER TABLE `character_effects_save` CHANGE `id` `object_id` INT NOT NULL;
ALTER TABLE `character_effects_save` CHANGE `class_index` `id` INT NOT NULL;
DELETE FROM `character_effects_save` WHERE `id` = -1;