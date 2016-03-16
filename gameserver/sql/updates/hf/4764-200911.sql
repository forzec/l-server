-- удаляем старые бафы суммов
DELETE FROM character_servitors WHERE servitor_id < 0;
DELETE FROM servitor_effects WHERE object_id < 0;
DELETE FROM servitors WHERE object_id < 0;

-- востанавливаем
ALTER TABLE servitors RENAME TO pets;
DROP TABLE servitor_effects;

ALTER TABLE pets DROP COLUMN exp_penalty, DROP COLUMN npc_id, DROP COLUMN item_consume_id, DROP COLUMN item_consume_count, DROP COLUMN max_life, DROP COLUMN item_consume_delay;