--liquibase formatted sql

--changeset EA:1
CREATE SEQUENCE authors_seq START WITH 1 INCREMENT BY 1;

--changeset EA:2
CREATE TABLE authors
(
    id          bigint PRIMARY KEY,
    author_name text
);

--changeset EA:3
INSERT INTO authors (id, author_name)
SELECT DISTINCT nextval('authors_seq'),
                author_name
FROM books;

--changeset EA:4
--Промежуточная таблица для связи многие-ко-многим
CREATE SEQUENCE book_authors_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE book_authors
(
    id        bigint PRIMARY KEY,
    book_id   bigint,
    author_id bigint,

    CONSTRAINT BOOK_AUTHORS_BOOK_ID_fk
        FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,

    CONSTRAINT BOOK_AUTHORS_AUTHOR_ID_fk
        FOREIGN KEY (author_id) REFERENCES authors (id) ON DELETE CASCADE,

    CONSTRAINT BOOK_AUTHORS_UNIQUE
        UNIQUE (book_id, author_id)
);

--changeset EA:5
INSERT INTO book_authors (id, book_id, author_id)
SELECT nextval('book_authors_seq'),
       books.id,
       authors.id
FROM books
         JOIN authors ON books.author_name = authors.author_name;

--changeset EA:6
ALTER TABLE books
DROP
COLUMN author_name;

--changeset EA:7
CREATE INDEX idx_book_authors_book_id ON book_authors (book_id);
CREATE INDEX idx_book_authors_author_id ON book_authors (author_id);

--changeset EA:8
INSERT INTO books (id, book_name)
VALUES (nextval('books_seq'), 'Благие знамения');
INSERT INTO authors (id, author_name)
VALUES (nextval('authors_seq'), 'Терри Пратчетт');
INSERT INTO authors (id, author_name)
VALUES (nextval('authors_seq'), 'Нил Гейман');

INSERT INTO book_authors (id, book_id, author_id)
SELECT nextval('book_authors_seq'), b.id, a.id
FROM books b
         JOIN authors a on a.author_name = 'Терри Пратчетт'
where b.book_name = 'Благие знамения'
;
INSERT INTO book_authors (id, book_id, author_id)
SELECT nextval('book_authors_seq'), b.id, a.id
FROM books b
         JOIN authors a on a.author_name = 'Нил Гейман'
where b.book_name = 'Благие знамения'
;
--changeset EA:9
INSERT INTO books (id, book_name)
VALUES (nextval('books_seq'), 'Бесконечная земля');
INSERT INTO authors (id, author_name)
VALUES (nextval('authors_seq'), 'Стивен Бакстер');

INSERT INTO book_authors (id, book_id, author_id)
SELECT nextval('book_authors_seq'), b.id, a.id
FROM books b
         JOIN authors a on a.author_name = 'Терри Пратчетт'
where b.book_name = 'Бесконечная земля'
;
INSERT INTO book_authors (id, book_id, author_id)
SELECT nextval('book_authors_seq'), b.id, a.id
FROM books b
         JOIN authors a on a.author_name = 'Стивен Бакстер'
where b.book_name = 'Бесконечная земля'
;
--changeset EA:10
INSERT INTO books (id, book_name)
VALUES (nextval('books_seq'), 'Одноэтажная Америка');
INSERT INTO authors (id, author_name)
VALUES (nextval('authors_seq'), 'Илья Ильф');
INSERT INTO authors (id, author_name)
VALUES (nextval('authors_seq'), 'Евгений Петров');

INSERT INTO book_authors (id, book_id, author_id)
SELECT nextval('book_authors_seq'), b.id, a.id
FROM books b
         JOIN authors a on a.author_name = 'Илья Ильф'
where b.book_name = 'Одноэтажная Америка'
;
INSERT INTO book_authors (id, book_id, author_id)
SELECT nextval('book_authors_seq'), b.id, a.id
FROM books b
         JOIN authors a on a.author_name = 'Евгений Петров'
where b.book_name = 'Одноэтажная Америка'
;