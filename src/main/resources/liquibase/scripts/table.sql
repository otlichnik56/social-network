-- liquibase formatted sql

-- changeset nurkatovich:1

CREATE TABLE users
(
    id           SERIAL PRIMARY KEY,
    username     TEXT UNIQUE NOT NULL,
    email        TEXT UNIQUE NOT NULL,
    password     TEXT NOT NULL
);

-- changeset nurkatovich:2

CREATE TABLE friend_requests
(
    id                  SERIAL PRIMARY KEY,
    from_user           INTEGER NOT NULL,
    to_user             INTEGER NOT NULL,
    status              INTEGER NOT NULL,
    date_request        TIMESTAMP,
    date_response       TIMESTAMP
);

CREATE TABLE subscriptions
(
    id                  SERIAL PRIMARY KEY,
    from_user           INTEGER NOT NULL,
    to_user             INTEGER NOT NULL,
    status              BOOLEAN NOT NULL,
    date_subscription   TIMESTAMP
);


-- changeset nurkatovich:3

CREATE TABLE publications
(
    id          SERIAL PRIMARY KEY,
    author_id   INTEGER,
    username    TEXT,
    header      TEXT,
    text        TEXT,
    image       OID
);