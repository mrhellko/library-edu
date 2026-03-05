--liquibase formatted sql

--changeset EA:3
CREATE SEQUENCE books_seq START WITH 1 INCREMENT BY 1;

--changeset RA:1
CREATE TABLE books
(
    id          bigint PRIMARY KEY,
    book_name   text,
    author_name text
);