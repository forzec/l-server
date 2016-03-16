-- servitor_effects.sql к этому айпдейту


-- удаляем старые бафы суммов
DELETE FROM character_effects_save WHERE id > 100000;
-- востанавливаем старое
ALTER TABLE character_effects_save CHANGE `id` `class_index` INT NOT NULL;
-- косметика
ALTER TABLE character_effects_save RENAME TO character_effects;

ALTER TABLE pets RENAME TO servitors;
ALTER TABLE servitors CHANGE `item_obj_id` `object_id` INT NOT NULL;
ALTER TABLE servitors CHANGE `curHp` `current_hp` INT NOT NULL;
ALTER TABLE servitors CHANGE `curMp` `current_mp` INT NOT NULL;
ALTER TABLE servitors CHANGE `fed` `current_life` INT NOT NULL;
ALTER TABLE servitors DROP `max_fed`;
ALTER TABLE servitors DROP `level`;
ALTER TABLE servitors ADD COLUMN `max_life` INT NOT NULL DEFAULT 0;
ALTER TABLE servitors ADD COLUMN `npc_id` INT NOT NULL DEFAULT 0;

-- summon sector
ALTER TABLE servitors ADD COLUMN `exp_penalty` DOUBLE NOT NULL DEFAULT 0;
ALTER TABLE servitors ADD COLUMN `item_consume_id` INT NOT NULL DEFAULT 0;
ALTER TABLE servitors ADD COLUMN `item_consume_count` INT NOT NULL DEFAULT 0;
ALTER TABLE servitors ADD COLUMN `item_consume_delay` INT NOT NULL DEFAULT 0;


