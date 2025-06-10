-- 1) Создаём/пересоздаём роль jobuser

DROP ROLE IF EXISTS jobuser;
CREATE ROLE jobuser LOGIN PASSWORD 'jobpass' CREATEDB;

-- 2) Пересоздаём базу jobparser
DROP DATABASE IF EXISTS jobparser;
CREATE DATABASE jobparser OWNER jobuser;
-- 3) Подключаемся к ней
\connect jobparser
SET ROLE jobuser;
-- 4) Схема таблицы vacancy
CREATE TABLE IF NOT EXISTS vacancy (
                                       id              SERIAL        PRIMARY KEY,
                                       hh_id           VARCHAR(64)   UNIQUE NOT NULL,
    title           TEXT,
    company         TEXT,
    city            TEXT,
    salary_min      INTEGER,
    salary_max      INTEGER,
    description     TEXT,
    requirements    TEXT,
    employment_type TEXT,
    category        TEXT,
    published_date  DATE,
    url             TEXT,
    active          BOOLEAN       DEFAULT TRUE,
    last_updated    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
    );


select * from vacancy;