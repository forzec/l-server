UPDATE pets INNER JOIN pets_data
SET pets.exp = pets_data.newexp
WHERE pets.object_id = pets_data.object_id;
