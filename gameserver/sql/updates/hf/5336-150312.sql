ALTER TABLE items
	DROP COLUMN augmentation_id,
	ADD COLUMN augmentation_mineral_id INT NOT NULL DEFAULT 0,
	ADD COLUMN augmentation_id1 INT NOT NULL DEFAULT 0,
	ADD COLUMN augmentation_id2 INT NOT NULL DEFAULT 0;