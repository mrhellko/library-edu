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
        foreign key (book_id) references books on delete cascade
);
--changeset EA:4
CREATE INDEX idx_book_reviews_book_id ON book_reviews (book_id);