--liquibase formatted sql

--changeset EA:1
CREATE SEQUENCE genres_seq START WITH 1 INCREMENT BY 1;

--changeset EA:2
CREATE TABLE genres
(
    id         bigint PRIMARY KEY,
    genre_name text
);

--changeset EA:4
--Промежуточная таблица для связи многие-ко-многим
CREATE TABLE book_genres
(
    book_id  bigint not null,
    genre_id bigint not null,

    CONSTRAINT BOOK_GENRES_PK
        PRIMARY KEY (book_id, genre_id),

    CONSTRAINT BOOK_GENRES_BOOK_ID_fk
        FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,

    CONSTRAINT BOOK_GENRES_GENRE_ID_fk
        FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
);