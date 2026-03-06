--liquibase formatted sql

--changeset EA:1
CREATE SEQUENCE storage_locations_seq START WITH 1 INCREMENT BY 1;

--changeset EA:2
CREATE TABLE storage_locations
(
    id       bigint PRIMARY KEY,
    building text,
    room     text,
    shelf    int
);

--changeset EA:3
CREATE TABLE copies
(
    id                  text PRIMARY KEY,
    book_id             bigint,
    quality             smallint,
    status              smallint,
    storage_location_id bigint,

    constraint COPIES_BOOKS_ID_fk
        foreign key (book_id) references books on delete cascade,
    constraint COPIES_STORAGE_LOCATIONS_ID_fk
        foreign key (storage_location_id) references storage_locations on delete cascade
);

--changeset EA:4
CREATE INDEX idx_copies_book_id ON copies (book_id);
CREATE INDEX idx_copies_storage_location_id ON copies (storage_location_id);