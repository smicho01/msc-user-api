CREATE TABLE IF NOT EXISTS users.user (
    id VARCHAR(36)  PRIMARY KEY,
    username VARCHAR(36) NOT NULL,
    visibleusername VARCHAR(70) NOT NULL,
    firstname VARCHAR(40) NOT NULL,
    lastname VARCHAR(40) NOT NULL,
    email VARCHAR(100) NOT NULL,
    college VARCHAR(150),
    collegeid VARCHAR(36),
    active boolean NOT NULL,
    pubKey TEXT,
    privKey TEXT,
    tokens INTEGER DEFAULT 0,
    rank INTEGER DEFAULT 0,
    datecreated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dateupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS users.friends (
    user_id VARCHAR(36) NOT NULL,
    friend_id VARCHAR(36) NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users."user"(id),
    FOREIGN KEY (friend_id) REFERENCES users."user"(id)
);