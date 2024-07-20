
-- CREATE SCHEMA IF NOT EXISTS users;

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

-- -- -- some dummy data
-- INSERT INTO users.user(id, username, visibleusername, firstname, lastname, email,  active ) VALUES
--     ('7cef2a1b-5ef9-4fc6-88fe-b2572f19bf65', 'johndo01', 'HappyUnicorn' ,'John', 'Doe', 'jdoe@gmail.com', true),
--     ('4d9d8092-b95d-4106-866f-96ef2490252b', 'adasmi30', 'SmartRadish' ,'Adam', 'Smith', 'asmith@gmail.com', true),
--     ('f2e43664-2adf-4abd-97ee-3782e98c39e4', 'annflo01', 'PotentialPineapple' ,'Anna', 'Flowers-Kitty', 'aflowers@gmail.com', true);


CREATE TABLE users.friends (
    user_id VARCHAR(36) NOT NULL,
    friend_id VARCHAR(36) NOT NULL,
    status VARCHAR(50) DEFAULT 'pending', -- to track request status (pending, accepted, rejected, etc.)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users."user"(id),
    FOREIGN KEY (friend_id) REFERENCES users."user"(id)
);

