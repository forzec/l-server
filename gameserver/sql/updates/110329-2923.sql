ALTER TABLE `character_shortcuts` CHANGE `char_obj_id` `object_id` INT( 11 ) NOT NULL;
ALTER TABLE `character_shortcuts` ADD `character_type` INT( 11 ) NOT NULL DEFAULT '1';