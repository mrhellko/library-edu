DELETE FROM book_authors;
DELETE FROM book_reviews;
DELETE FROM book_genres;
DELETE FROM books;
DELETE FROM authors;
DELETE FROM genres;
DELETE FROM storage_locations;
DELETE FROM copies;

ALTER SEQUENCE books_seq RESTART WITH 1;
ALTER SEQUENCE authors_seq RESTART WITH 1;
ALTER SEQUENCE book_reviews_seq RESTART WITH 1;
ALTER SEQUENCE genres_seq RESTART WITH 1;
ALTER SEQUENCE storage_locations_seq RESTART WITH 1;