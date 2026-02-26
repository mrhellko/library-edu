package ru.mrhellko.library.assembler;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Author;
import ru.mrhellko.library.Entity.Book;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.dao.AuthorDAO;
import ru.mrhellko.library.dao.BookDAO;
import ru.mrhellko.library.dao.BookReviewDAO;
import ru.mrhellko.library.dto.BookAuthorDTO;
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

    public BookWithAverageRatingDTO getFullBookWithAverageRatingDTO(Long id) {
        Book book = bookDAO.getBookById(id);
        if (book != null) {
            List<Book> books = List.of(book);
            fillBooksWithAuthors(books);
            return fillListOfBookWithAverageRatingDTO(books).getFirst();
        } else {
            return null;
        }
    }

    public List<BookWithAverageRatingDTO> getFullAllBooks() {
        List<Book> books = bookDAO.getAll();
        fillBooksWithAuthors(books);
        return fillListOfBookWithAverageRatingDTO(books);
    }

    public Book updateBook(Book book, Long id) throws Exception {
        validateBook(book);
        Book updatedBook = bookDAO.getBookById(id);
        if (updatedBook != null) {
            fillBooksWithAuthors(List.of(updatedBook));
            updatedBook.setId(id);
            updatedBook.setBookName(book.getBookName());
            bookDAO.updateBook(updatedBook);

            Set<Author> oldSet = new HashSet<>(updatedBook.getAuthors());
            Set<Author> newSet = new HashSet<>(book.getAuthors());

            Set<Author> added = new HashSet<>(newSet);
            added.removeAll(oldSet);
            for (Author author : added) {
                bookDAO.saveBookAuthor(updatedBook.getId(), author.getId());
            }

            Set<Author> removed = new HashSet<>(oldSet);
            removed.removeAll(newSet);
            for (Author author : removed) {
                bookDAO.deleteBookAuthor(updatedBook.getId(), author.getId());
            }

            return updatedBook;
        } else {
            return null;
        }
    }

    public Book saveBook(Book book) throws Exception {
        validateBook(book);
        Book savedBook = bookDAO.saveBook(book);
        for (Author author : savedBook.getAuthors()) {
            bookDAO.saveBookAuthor(savedBook.getId(), author.getId());
        }
        return savedBook;
    }

    public void deleteBook(Long id) throws Exception {
        int resultBook = bookDAO.deleteBookById(id);
        if (resultBook == 0) {
            throw new NotFoundException(id);
        }
    }

    public List<BookWithAverageRatingDTO> getBooksByAuthorName(String authorName) {
        List<Book> books = bookDAO.getBooksByAuthorName(authorName);
        fillBooksWithAuthors(books);
        return fillListOfBookWithAverageRatingDTO(books);
    }

    public List<BookWithAverageRatingDTO> getBooksByAuthorId(Long authorId) {
        List<Book> books = bookDAO.getBooksByAuthorId(authorId);
        fillBooksWithAuthors(books);
        return fillListOfBookWithAverageRatingDTO(books);
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

    public void fillBooksWithAuthors(List<Book> books) {
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

    public void validateBook(Book book) throws IllegalArgumentException {
        if (book.getAuthors().isEmpty()) {
            throw new IllegalArgumentException("No authors");
        }
        for (Author author : book.getAuthors()) {
            if (authorDAO.getAuthorById(author.getId()) == null) {
                throw new IllegalArgumentException("Bad author");
            }
        }
    }
}
