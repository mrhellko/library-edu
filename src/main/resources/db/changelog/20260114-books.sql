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
--changeset RA:2
INSERT INTO books (id, book_name, author_name)
VALUES (nextval('books_seq'), 'Гарри Поттер', 'Joan Rowling');
INSERT INTO books (id, book_name, author_name)
VALUES (nextval('books_seq'), 'Задача трех тел', 'Лю Цысинь');
INSERT INTO books (id, book_name, author_name)
VALUES (nextval('books_seq'), 'Игра престолов', 'Джордж Мартин');