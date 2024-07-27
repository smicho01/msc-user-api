CREATE TABLE IF NOT EXISTS users.message (
    id VARCHAR(36)  PRIMARY KEY,
    fromid VARCHAR(36) NOT NULL,
    toid VARCHAR(36) NOT NULL,
    content TEXT,
    datecreated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read Boolean DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_message_fromid ON users.message(fromid);
CREATE INDEX IF NOT EXISTS idx_message_toid ON users.message(toid);
