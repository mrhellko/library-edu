package ru.mrhellko.library.assembler;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Author;
import ru.mrhellko.library.Entity.Book;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.Entity.Genre;
import ru.mrhellko.library.dao.AuthorDAO;
import ru.mrhellko.library.dao.BookDAO;
import ru.mrhellko.library.dao.BookReviewDAO;
import ru.mrhellko.library.dao.GenreDAO;
import ru.mrhellko.library.dto.BookAuthorDTO;
import ru.mrhellko.library.dto.BookGenreDTO;
import ru.mrhellko.library.dto.BookWithAverageRatingDTO;
import ru.mrhellko.library.exception.NotFoundException;

import java.util.*;

@Service
public class BookAssembler {
    @Autowired
    private BookDAO bookDAO;
    @Autowired
    private BookReviewDAO bookReviewDAO;
    @Autowired
    private AuthorDAO authorDAO;
    @Autowired
    private GenreDAO genreDAO;

    public BookWithAverageRatingDTO getFullBookWithAverageRatingDTO(Long id) {
        Book book = bookDAO.getBookById(id);
        if (book != null) {
            List<Book> books = List.of(book);
            fillBooksWithAuthors(books);
            fillBooksWithGenres(books);
            return fillListOfBookWithAverageRatingDTO(books).getFirst();
        } else {
            return null;
        }
    }

    public List<BookWithAverageRatingDTO> getFullAllBooks() {
        List<Book> books = bookDAO.getAll();
        fillBooksWithAuthors(books);
        fillBooksWithGenres(books);
        return fillListOfBookWithAverageRatingDTO(books);
    }

    public Book updateBook(Book book, Long id) {
        validateBook(book);
        Book updatedBook = bookDAO.getBookById(id);
        if (updatedBook != null) {
            fillBooksWithAuthors(List.of(updatedBook));
            fillBooksWithGenres(List.of(updatedBook));
            updatedBook.setId(id);
            updatedBook.setBookName(book.getBookName());
            bookDAO.updateBook(updatedBook);

            //Логика с добавлением и удалением связей книги и автора
            Set<Author> oldSetAuthors = new HashSet<>(updatedBook.getAuthors());
            Set<Author> newSetAuthors = new HashSet<>(book.getAuthors());

            Set<Author> addedAuthors = new HashSet<>(newSetAuthors);
            addedAuthors.removeAll(oldSetAuthors);
            for (Author author : addedAuthors) {
                bookDAO.saveBookAuthor(updatedBook.getId(), author.getId());
                updatedBook.getAuthors().add(author);
            }

            Set<Author> removedAuthors = new HashSet<>(oldSetAuthors);
            removedAuthors.removeAll(newSetAuthors);
            for (Author author : removedAuthors) {
                bookDAO.deleteBookAuthor(updatedBook.getId(), author.getId());
                updatedBook.getAuthors().remove(author);
            }

            //Логика с добавлением и удалением связей книги и жанра
            Set<Genre> oldSetGenres = new HashSet<>(updatedBook.getGenres());
            Set<Genre> newSetGenres = new HashSet<>(book.getGenres());

            Set<Genre> addedGenres = new HashSet<>(newSetGenres);
            addedGenres.removeAll(oldSetGenres);
            for (Genre genre : addedGenres) {
                bookDAO.saveBookGenre(updatedBook.getId(), genre.getId());
                updatedBook.getGenres().add(genre);
            }

            Set<Genre> removedGenres = new HashSet<>(oldSetGenres);
            removedGenres.removeAll(newSetGenres);
            for (Genre genre : removedGenres) {
                bookDAO.deleteBookGenre(updatedBook.getId(), genre.getId());
                updatedBook.getGenres().remove(genre);
            }

            return updatedBook;
        } else {
            return null;
        }
    }

    public Book saveBook(Book book) {
        validateBook(book);
        Book savedBook = bookDAO.saveBook(book);
        for (Author author : savedBook.getAuthors()) {
            bookDAO.saveBookAuthor(savedBook.getId(), author.getId());
        }
        for (Genre genre : savedBook.getGenres()) {
            bookDAO.saveBookGenre(savedBook.getId(), genre.getId());
        }
        return savedBook;
    }

    public void deleteBook(Long id) {
        int resultBook = bookDAO.deleteBookById(id);
        if (resultBook == 0) {
            throw new NotFoundException(id);
        }
    }

    public List<BookWithAverageRatingDTO> getBooksByAuthorName(String authorName) {
        List<Book> books = bookDAO.getBooksByAuthorName(authorName);
        fillBooksWithAuthors(books);
        fillBooksWithGenres(books);
        return fillListOfBookWithAverageRatingDTO(books);
    }

    public List<BookWithAverageRatingDTO> getBooksByAuthorId(Long authorId) {
        List<Book> books = bookDAO.getBooksByAuthorId(authorId);
        fillBooksWithAuthors(books);
        fillBooksWithGenres(books);
        return fillListOfBookWithAverageRatingDTO(books);
    }

    public List<BookWithAverageRatingDTO> getBooksByGenreId(Long genreId) {
        List<Book> books = bookDAO.getBooksByGenreId(genreId);
        fillBooksWithAuthors(books);
        fillBooksWithGenres(books);
        return fillListOfBookWithAverageRatingDTO(books);
    }

    public List<BookWithAverageRatingDTO> getBooksByAvgRating(Float avgRating, Long genreId) {
        List<BookWithAverageRatingDTO> bookWithAverageRatingDTOS;
        if (genreId == null) {
            bookWithAverageRatingDTOS = bookDAO.getBooksByAvgRating(avgRating);
        } else {
            bookWithAverageRatingDTOS = bookDAO.getBooksByAvgRating(avgRating, genreId);
        }
        fillBooksWithAuthors(bookWithAverageRatingDTOS);
        fillBooksWithGenres(bookWithAverageRatingDTOS);
        return bookWithAverageRatingDTOS;
    }

    private @NonNull List<BookWithAverageRatingDTO> fillListOfBookWithAverageRatingDTO(List<Book> books) {
        List<BookWithAverageRatingDTO> bookWithAverageRatingDTOs = new ArrayList<>();
        for (Book book : books) {
            BookWithAverageRatingDTO bookWithAverageRatingDTO = new BookWithAverageRatingDTO(book);
            bookWithAverageRatingDTO.setAverageRating(getAverageRating(bookWithAverageRatingDTO.getId()));
            bookWithAverageRatingDTOs.add(bookWithAverageRatingDTO);
        }
        return bookWithAverageRatingDTOs;
    }

    private Float getAverageRating(long bookId) {
        List<BookReview> bookReviews = bookReviewDAO.getReviewByBookId(bookId);
        int sum = 0;
        for (BookReview bookReview : bookReviews) {
            sum += bookReview.getRating();
        }
        return bookReviews.isEmpty() ? null : (float) sum / bookReviews.size();
    }

    private void fillBooksWithAuthors(List<? extends Book> books) {
        Map<Long, Book> bookIndex = new HashMap<>();
        for (Book book : books) {
            bookIndex.put(book.getId(), book);
        }
        List<BookAuthorDTO> bookAuthorDTOS = authorDAO.getAuthorsForBooks(bookIndex.keySet());
        for (BookAuthorDTO bookAuthorDTO : bookAuthorDTOS) {
            Long bookId = bookAuthorDTO.getBookId();
            Book book = bookIndex.get(bookId);
            book.getAuthors().add(new Author(bookAuthorDTO.getAuthorId(), bookAuthorDTO.getAuthorName()));
        }
    }

    private void fillBooksWithGenres(List<? extends Book> books) {
        Map<Long, Book> bookIndex = new HashMap<>();
        for (Book book : books) {
            bookIndex.put(book.getId(), book);
        }
        List<BookGenreDTO> bookGenreDTOS = genreDAO.getGenresForBooks(bookIndex.keySet());
        for (BookGenreDTO bookGenreDTO : bookGenreDTOS) {
            Long bookId = bookGenreDTO.getBookId();
            Book book = bookIndex.get(bookId);
            book.getGenres().add(new Genre(bookGenreDTO.getGenreId(), bookGenreDTO.getGenreName()));
        }
    }

    private void validateBook(Book book) throws IllegalArgumentException {
        if (book.getAuthors().isEmpty()) {
            throw new IllegalArgumentException("No authors");
        }
        if (book.getGenres().isEmpty()) {
            throw new IllegalArgumentException("No genres");
        }
        for (Author author : book.getAuthors()) {
            if (authorDAO.getAuthorById(author.getId()) == null) {
                throw new IllegalArgumentException("Bad author: " + author.getId() + " "
                        + author.getAuthorName() +
                        "\nYou need to create new author before updating book");
            }
        }
        for (Genre genre : book.getGenres()) {
            if (genreDAO.getGenreById(genre.getId()) == null) {
                throw new IllegalArgumentException("Bad genre: " + genre.getId() + " "
                        + genre.getGenreName() +
                        "\nYou need to create new genre before updating book;");
            }
        }
    }
}
