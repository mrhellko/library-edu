--liquibase formatted sql

--changeset EA:1
CREATE SEQUENCE authors_seq START WITH 1 INCREMENT BY 1;

--changeset EA:2
CREATE TABLE authors
(
    id          bigint PRIMARY KEY,
    author_name text
);

--changeset EA:4
--Промежуточная таблица для связи многие-ко-многим
CREATE TABLE book_authors
(
    book_id   bigint not null,
    author_id bigint not null,

    CONSTRAINT BOOK_AUTHORS_PK
        PRIMARY KEY (book_id, author_id),

    CONSTRAINT BOOK_AUTHORS_BOOK_ID_fk
        FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,

    CONSTRAINT BOOK_AUTHORS_AUTHOR_ID_fk
        FOREIGN KEY (author_id) REFERENCES authors (id) ON DELETE CASCADE
);

--changeset EA:6
ALTER TABLE books
DROP
COLUMN author_name;

--changeset EA:7
CREATE INDEX idx_book_authors_book_id ON book_authors (book_id);
CREATE INDEX idx_book_authors_author_id ON book_authors (author_id);
