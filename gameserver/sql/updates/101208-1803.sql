-- нужна ищо конвертации
ALTER TABLE character_subclasses ADD COLUMN certification INT NOT NULL DEFAULT 0;
--
UPDATE character_subclasses SET certification=15 WHERE skills <> '';
--
ALTER TABLE character_subclasses DROP COLUMN skills;
