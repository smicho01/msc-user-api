
CREATE SCHEMA IF NOT EXISTS studentsschema;

CREATE TABLE IF NOT EXISTS studentsschema.student (
    id VARCHAR(36)  PRIMARY KEY,
    student_id VARCHAR(36) NOT NULL,
    name VARCHAR(70) NOT NULL
    );
