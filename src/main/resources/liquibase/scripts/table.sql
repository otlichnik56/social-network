-- liquibase formatted sql

-- changeset nurkatovich:1

CREATE TABLE users
(
    id           SERIAL PRIMARY KEY,
    username     TEXT UNIQUE,
    password     TEXT,
    email        TEXT
);
