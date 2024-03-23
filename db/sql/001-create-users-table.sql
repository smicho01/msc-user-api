
CREATE SCHEMA IF NOT EXISTS users;

CREATE TABLE IF NOT EXISTS users.user (
    id VARCHAR(36)  PRIMARY KEY,
    username VARCHAR(36) NOT NULL,
    firstname VARCHAR(40) NOT NULL,
    lastname VARCHAR(40) NOT NULL,
    email VARCHAR(100) NOT NULL,
    sex VARCHAR(1) NOT NULL,
    password VARCHAR(100) NOT NULL,
    active boolean NOT NULL
    );


INSERT INTO users.user(id, username, firstname, lastname, email, sex, password, active) VALUES
    ('7cef2a1b-5ef9-4fc6-88fe-b2572f19bf65', 'johndo01', 'John', 'Doe', 'jdoe@gmail.com', 'M', 'password', true),
    ('4d9d8092-b95d-4106-866f-96ef2490252b', 'adasmi30', 'Adam', 'Smith', 'asmith@gmail.com', 'M', 'password', true),
    ('f2e43664-2adf-4abd-97ee-3782e98c39e4', 'annflo01', 'Anna', 'Flowers-Kitty', 'aflowers@gmail.com', 'F', 'password', true);