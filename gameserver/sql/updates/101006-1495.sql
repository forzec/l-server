INSERT into `server_variables` (`name`, `value`) VALUES
	('HB_derekKilled', 'true'),
	('HB_bernardBoxes', 'true'),
	('HB_judesBoxes', 'true'),
	('HB_captainKilled', 'true');
	
UPDATE `server_variables` SET `value` = 2300000 WHERE `name` = 'HellboundConfidence';