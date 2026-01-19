--liquibase formatted sql

--changeset RA:1
CREATE TABLE book_reviews (
    id bigint PRIMARY KEY,
    book_id bigint,
    rating smallint,
    reviewer_name character varying(16),
    review_text character varying,

    constraint BOOK_REVIEWS_BOOKS_ID_fk
        foreign key (book_id) references books
);

--changeset RA:2
INSERT INTO book_reviews (id, book_id, rating, reviewer_name, review_text) VALUES (1, 1, 8, 'Anna', 'Книга детства! Рекомендую!');
INSERT INTO book_reviews (id, book_id, rating, reviewer_name, review_text) VALUES (2, 2, 7, 'Sergei', 'Не знаю о чем она, я все забыл.');
INSERT INTO book_reviews (id, book_id, rating, reviewer_name, review_text) VALUES (3, 1, 3, 'Sergei', 'Скука, для детей.');