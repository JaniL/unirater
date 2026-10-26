-- Schema for UniRater, ported from the Hibernate-generated schema of the
-- original Spring application. Column names avoid SQL keywords so the
-- same DDL runs on PostgreSQL and on Scaleway's Serverless SQL Database.

CREATE TABLE IF NOT EXISTS restaurant (
    id       BIGSERIAL PRIMARY KEY,
    name     TEXT NOT NULL UNIQUE,
    uri      TEXT NOT NULL UNIQUE,
    areacode INT    NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS price_value (
    id           BIGSERIAL PRIMARY KEY,
    contract     TEXT,
    graduate     TEXT,
    graduate_hyy TEXT,
    normal       TEXT,
    student      TEXT,
    student_hyy  TEXT
);

CREATE TABLE IF NOT EXISTS price (
    id       BIGSERIAL PRIMARY KEY,
    name     TEXT NOT NULL UNIQUE,
    value_id BIGINT REFERENCES price_value (id)
);

CREATE TABLE IF NOT EXISTS food (
    id                 BIGSERIAL PRIMARY KEY,
    name               TEXT NOT NULL,
    price_id           BIGINT REFERENCES price (id),
    total             INT  NOT NULL DEFAULT 0,
    last_seen_on_menu  DATE,
    restaurant_id      BIGINT NOT NULL REFERENCES restaurant (id),
    UNIQUE (name, restaurant_id)
);

CREATE TABLE IF NOT EXISTS menu_of_the_day (
    id            BIGSERIAL PRIMARY KEY,
    restaurant_id BIGINT NOT NULL REFERENCES restaurant (id),
    menu_date     DATE   NOT NULL,
    UNIQUE (restaurant_id, menu_date)
);

CREATE TABLE IF NOT EXISTS food_menu (
    menu_of_the_day_id BIGINT NOT NULL REFERENCES menu_of_the_day (id) ON DELETE CASCADE,
    food_id            BIGINT NOT NULL REFERENCES food (id),
    PRIMARY KEY (menu_of_the_day_id, food_id)
);

CREATE TABLE IF NOT EXISTS rating (
    id       BIGSERIAL PRIMARY KEY,
    rating   INT  NOT NULL,
    rated_at TIMESTAMPTZ NOT NULL,
    user_id  BIGINT NOT NULL,
    food_id  BIGINT NOT NULL REFERENCES food (id),
    UNIQUE (food_id, user_id)
);

CREATE TABLE IF NOT EXISTS user_accounts (
    id            BIGSERIAL PRIMARY KEY,
    email         TEXT NOT NULL UNIQUE,
    first_name    TEXT NOT NULL,
    last_name     TEXT NOT NULL,
    password      TEXT NOT NULL,
    role          TEXT NOT NULL DEFAULT 'ROLE_USER',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);