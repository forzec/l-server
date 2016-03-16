DROP TABLE IF EXISTS `clan_subpledges_skills`;
CREATE TABLE `clan_subpledges_skills` (
  `clan_id` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL,
  `skill_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `skill_level` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`clan_id`,`type`,`skill_id`)
);

-- убираем хлам
DROP TABLE IF EXISTS skill_spellbooks;
DROP TABLE IF EXISTS skill_trees;

-- если клан лидер в саб юните - переносим
UPDATE characters SET characters.pledge_type=0 WHERE characters.pledge_type <> 0 AND (SELECT clan_id FROM clan_data WHERE leader_id=characters.obj_Id) > 0
-- перемещаем даные с клана в саб юниты
INSERT INTO clan_subpledges (clan_id, type, leader_id, name) SELECT clan_id,0,leader_id,clan_name FROM clan_data;
-- удаляем даные которые уже перенеслись
ALTER TABLE clan_data DROP COLUMN clan_name;
ALTER TABLE clan_data DROP COLUMN leader_id;

-- обновляем дефалт значения
ALTER TABLE characters ALTER COLUMN pledge_type SET DEFAULT '-128';
-- ставим стандартное значения если нету клана
UPDATE characters SET pledge_type='-128' WHERE clanid=0;