ALTER TABLE `items`
  DROP `name`,
  DROP `class`;

ALTER TABLE `items` CHANGE `object_id` `object_id` INT NOT NULL,
CHANGE `loc` `loc` VARCHAR( 32 ) NOT NULL,
CHANGE `loc_data` `loc_data` INT NOT NULL,
CHANGE `count` `count` BIGINT NOT NULL,
CHANGE `item_id` `item_id` INT NOT NULL,
CHANGE `custom_type1` `custom_type1` INT NOT NULL,
CHANGE `custom_type2` `custom_type2` INT NOT NULL,
CHANGE `shadow_life_time` `life_time` INT NOT NULL,
CHANGE `flags` `custom_flags` INT NOT NULL;

ALTER TABLE `items` ADD `augmentation_id` INT NOT NULL,
ADD `augmentation_skill_id`  INT( 11 ) NOT NULL,
ADD `augmentation_skill_level`  INT( 11 ) NOT NULL,
ADD `attribute_fire` INT NOT NULL ,
ADD `attribute_water` INT NOT NULL ,
ADD `attribute_wind` INT NOT NULL ,
ADD `attribute_earth` INT NOT NULL ,
ADD `attribute_holy` INT NOT NULL ,
ADD `attribute_unholy` INT NOT NULL;

UPDATE items INNER JOIN item_attributes ON items.object_id = item_attributes.itemId SET items.augmentation_id = item_attributes.augAttributes,
items.augmentation_skill_id = item_attributes.augSkillId,
items.augmentation_skill_level = item_attributes.augSkillLevel;

UPDATE items SET augmentation_id = 0, augmentation_skill_id = 0, augmentation_skill_level = 0 WHERE augmentation_id = -1;

UPDATE items INNER JOIN item_attributes ON items.object_id = item_attributes.itemId SET items.attribute_fire = item_attributes.elemValue WHERE item_attributes.elemType =0,
items.attribute_water = item_attributes.elemValue WHERE item_attributes.elemType =1,
items.attribute_wind = item_attributes.elemValue WHERE item_attributes.elemType =2,
items.attribute_earth = item_attributes.elemValue WHERE item_attributes.elemType =3,
items.attribute_holy = item_attributes.elemValue WHERE item_attributes.elemType =4,
items.attribute_unholy = item_attributes.elemValue WHERE item_attributes.elemType =5;
	
DROP TABLE item_attributes;

ALTER TABLE `mail` CHANGE `messageId` `message_id` INT NOT NULL AUTO_INCREMENT;
ALTER TABLE `mail` CHANGE `sender` `sender_id` INT NOT NULL;
ALTER TABLE `mail` ADD `sender_name` VARCHAR( 32 ) NOT NULL AFTER `sender_id`; 
ALTER TABLE `mail` CHANGE `receiver` `receiver_id` INT NOT NULL;
ALTER TABLE `mail` ADD `receiver_name` VARCHAR( 32 ) NOT NULL AFTER `receiver_id`; 
ALTER TABLE `mail` ADD `expire_time` INT NOT NULL AFTER `receiver_name`; 
ALTER TABLE `mail` CHANGE `topic` `topic` TINYTEXT NOT NULL;	
ALTER TABLE `mail` CHANGE `price` `price` BIGINT NOT NULL;
ALTER TABLE `mail` CHANGE `system` `system` TINYINT NOT NULL DEFAULT '0';
ALTER TABLE `mail` CHANGE `unread` `unread` TINYINT NOT NULL DEFAULT '1';

UPDATE `mail` SET `expire_time` = UNIX_TIMESTAMP( `expire` );
ALTER TABLE `mail` DROP `attachments`,DROP `needsPayment`, DROP `expire`;
	
ALTER TABLE `mail_attachments` CHANGE `messageId` `message_id` INT NOT NULL;
ALTER TABLE `mail_attachments` CHANGE `itemId` `item_id` INT NOT NULL;
ALTER TABLE `mail_attachments` DROP PRIMARY KEY;
ALTER TABLE `mail_attachments` ADD INDEX ( `message_id` );
ALTER TABLE `mail_attachments` ADD UNIQUE (`item_id`);
	
UPDATE `items` SET `loc` = 'MAIL' WHERE `loc` = 'LEASE';
DELETE mail_attachments LEFT JOIN items ON mail_attachments.item_id = items.object_id WHERE items.object_id IS NULL;
