--changeset id:1 author:RA
CREATE TABLE books (
    id bigint PRIMARY KEY,
    book_name text
);
--changeset id:2 author:RA
INSERT INTO books (id, book_name) VALUES (1, 'Гарри Поттер');
INSERT INTO books (id, book_name) VALUES (2, 'Задача трех тел');