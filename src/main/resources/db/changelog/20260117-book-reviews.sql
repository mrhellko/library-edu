--liquibase formatted sql
--changeset EA:3
CREATE SEQUENCE book_reviews_seq START WITH 1 INCREMENT BY 1;
--changeset EA:1
CREATE TABLE book_reviews
(
    id            bigint PRIMARY KEY,
    book_id       bigint,
    rating        smallint,
    reviewer_name character varying(16),
    review_text   character varying,

    constraint BOOK_REVIEWS_BOOKS_ID_fk
        foreign key (book_id) references books
);

--changeset EA:2
INSERT INTO book_reviews (id, book_id, rating, reviewer_name, review_text)
VALUES (nextval('book_reviews_seq'), 1, 8, 'Anna', 'Книга детства! Рекомендую!');
INSERT INTO book_reviews (id, book_id, rating, reviewer_name, review_text)
VALUES (nextval('book_reviews_seq'), 2, 7, 'Sergei', 'Не знаю о чем она, я все забыл.');
INSERT INTO book_reviews (id, book_id, rating, reviewer_name, review_text)
VALUES (nextval('book_reviews_seq'), 1, 3, 'Sergei', 'Скука, для детей.');