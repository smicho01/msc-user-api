ALTER TABLE users.user ADD COLUMN imageid INTEGER;

UPDATE users.user SET imageid = FLOOR(1 + random() * 7);