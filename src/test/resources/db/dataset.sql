INSERT INTO books (id, book_name)
VALUES (nextval('books_seq'), 'Гарри Поттер');
INSERT INTO books (id, book_name)
VALUES (nextval('books_seq'), 'Задача трех тел');
INSERT INTO books (id, book_name)
VALUES (nextval('books_seq'), 'Игра престолов');

INSERT INTO book_reviews (id, book_id, rating, reviewer_name, review_text)
VALUES (nextval('book_reviews_seq'),(select id from books where book_name = 'Гарри Поттер'), 8, 'Anna', 'Книга детства! Рекомендую!');
INSERT INTO book_reviews (id, book_id, rating, reviewer_name, review_text)
VALUES (nextval('book_reviews_seq'), (select id from books where book_name = 'Задача трех тел'), 7, 'Sergei', 'Не знаю о чем она, я все забыл.');
INSERT INTO book_reviews (id, book_id, rating, reviewer_name, review_text)
VALUES (nextval('book_reviews_seq'), (select id from books where book_name = 'Гарри Поттер'), 3, 'Sergei', 'Скука, для детей.');

INSERT INTO authors (id, author_name) VALUES (nextval('authors_seq'), 'Joan Rowling');
INSERT INTO authors (id, author_name) VALUES (nextval('authors_seq'), 'Лю Цысинь');
INSERT INTO authors (id, author_name) VALUES (nextval('authors_seq'), 'Джордж Мартин');

INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
         JOIN authors a ON a.author_name = 'Лю Цысинь'
WHERE b.book_name = 'Задача трех тел';

INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
         JOIN authors a ON a.author_name = 'Joan Rowling'
WHERE b.book_name = 'Гарри Поттер';

INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
         JOIN authors a ON a.author_name = 'Джордж Мартин'
WHERE b.book_name = 'Игра престолов';

INSERT INTO books (id, book_name)
VALUES (nextval('books_seq'), 'Благие знамения');
INSERT INTO authors (id, author_name)
VALUES (nextval('authors_seq'), 'Терри Пратчетт');
INSERT INTO authors (id, author_name)
VALUES (nextval('authors_seq'), 'Нил Гейман');

INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
         JOIN authors a on a.author_name = 'Терри Пратчетт'
where b.book_name = 'Благие знамения';

INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
         JOIN authors a on a.author_name = 'Нил Гейман'
where b.book_name = 'Благие знамения';

INSERT INTO books (id, book_name)
VALUES (nextval('books_seq'), 'Бесконечная земля');
INSERT INTO authors (id, author_name)
VALUES (nextval('authors_seq'), 'Стивен Бакстер');

INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
         JOIN authors a on a.author_name = 'Терри Пратчетт'
where b.book_name = 'Бесконечная земля';

INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
         JOIN authors a on a.author_name = 'Стивен Бакстер'
where b.book_name = 'Бесконечная земля';

INSERT INTO books (id, book_name)
VALUES (nextval('books_seq'), 'Одноэтажная Америка');
INSERT INTO authors (id, author_name)
VALUES (nextval('authors_seq'), 'Илья Ильф');
INSERT INTO authors (id, author_name)
VALUES (nextval('authors_seq'), 'Евгений Петров');

INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
         JOIN authors a on a.author_name = 'Илья Ильф'
where b.book_name = 'Одноэтажная Америка';

INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
         JOIN authors a on a.author_name = 'Евгений Петров'
where b.book_name = 'Одноэтажная Америка';

INSERT INTO book_reviews (id, book_id, rating, reviewer_name, review_text)
VALUES (nextval('book_reviews_seq'), (select id from books where book_name = 'Благие знамения'), 10, 'Fiona', 'Перечитываю каждый месяц!');

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