--liquibase formatted sql

--changeset EA:1
CREATE SEQUENCE genres_seq START WITH 1 INCREMENT BY 1;

--changeset EA:2
CREATE TABLE genres
(
    id         bigint PRIMARY KEY,
    genre_name text
);

--changeset EA:3
INSERT INTO genres (id, genre_name)
VALUES (nextval('genres_seq'), 'Фэнтези');
INSERT INTO genres (id, genre_name)
VALUES (nextval('genres_seq'), 'Драма');
INSERT INTO genres (id, genre_name)
VALUES (nextval('genres_seq'), 'Научная фантастика');
INSERT INTO genres (id, genre_name)
VALUES (nextval('genres_seq'), 'Средневековье');
INSERT INTO genres (id, genre_name)
VALUES (nextval('genres_seq'), 'Роман');
INSERT INTO genres (id, genre_name)
VALUES (nextval('genres_seq'), 'Юмор');

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

--changeset EA:5
--Добавление связей книг и жанров
INSERT INTO book_genres (book_id, genre_id)
SELECT b.id, g.id
FROM books b
         CROSS JOIN genres g
where b.book_name = 'Гарри Поттер'
  AND g.genre_name IN ('Фэнтези', 'Роман', 'Драма', 'Юмор');

INSERT INTO book_genres (book_id, genre_id)
SELECT b.id, g.id
FROM books b
         CROSS JOIN genres g
where b.book_name = 'Задача трех тел'
  AND g.genre_name IN ('Научная фантастика');

INSERT INTO book_genres (book_id, genre_id)
SELECT b.id, g.id
FROM books b
         CROSS JOIN genres g
where b.book_name = 'Игра престолов'
  AND g.genre_name IN ('Фэнтези', 'Роман', 'Средневековье');

INSERT INTO book_genres (book_id, genre_id)
SELECT b.id, g.id
FROM books b
         CROSS JOIN genres g
where b.book_name = 'Буря мечей'
  AND g.genre_name IN ('Фэнтези', 'Роман', 'Средневековье');

INSERT INTO book_genres (book_id, genre_id)
SELECT b.id, g.id
FROM books b
         CROSS JOIN genres g
where b.book_name = 'Благие знамения'
  AND g.genre_name IN ('Юмор');

INSERT INTO book_genres (book_id, genre_id)
SELECT b.id, g.id
FROM books b
         CROSS JOIN genres g
where b.book_name = 'Бесконечная земля'
  AND g.genre_name IN ('Научная фантастика', 'Роман', 'Фэнтези');

INSERT INTO book_genres (book_id, genre_id)
SELECT b.id, g.id
FROM books b
         CROSS JOIN genres g
where b.book_name = 'Одноэтажная Америка'
  AND g.genre_name IN ('Юмор');