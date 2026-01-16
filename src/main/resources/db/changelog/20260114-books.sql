--liquibase formatted sql

--changeset RA:1
CREATE TABLE books (
    id bigint PRIMARY KEY,
    book_name text,
    author_name text
);
--changeset RA:2
INSERT INTO books (id, book_name, author_name) VALUES (1, 'Гарри Поттер', 'Джоан Роулинг');
INSERT INTO books (id, book_name, author_name) VALUES (2, 'Задача трех тел', 'Лю Цысинь');