-- Blessed Zaken and Greater Blessed Zaken
UPDATE items SET augmentation_id=0 WHERE item_id IN (21712, 22175);
-- Knight's Cloak, Holy Spirit's Cloak
UPDATE items SET attribute_unholy=0 WHERE item_id IN (13687, 13688, 13689, 13690, 13889, 13890, 13891, 13892);