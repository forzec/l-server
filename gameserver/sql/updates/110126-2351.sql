INSERT INTO bbs_clannotice (clan_id, type, notice) SELECT clanID,2,notice FROM clan_notices;
DROP TABLE IF EXISTS clan_notices;