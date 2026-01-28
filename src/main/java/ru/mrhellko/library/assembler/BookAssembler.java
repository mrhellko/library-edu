package ru.mrhellko.library.assembler;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Book;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.dao.BookDAO;
import ru.mrhellko.library.dao.BookReviewDAO;
import ru.mrhellko.library.dto.BookWithAverageRatingDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookAssembler {
    @Autowired
    private BookDAO bookDAO;
    @Autowired
    private BookReviewDAO bookReviewDAO;

    public ResponseEntity<BookWithAverageRatingDTO> getFullBookWithAverageRatingDTO(Long id) {
        Book book = bookDAO.getBookById(id);
        if (book != null) {
            BookWithAverageRatingDTO bookWithAverageRatingDTO = new BookWithAverageRatingDTO(book);
            bookWithAverageRatingDTO.setAverageRating(getAverageRating(id));
            return new ResponseEntity<>(bookWithAverageRatingDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<List<BookWithAverageRatingDTO>> getFullAllBooks() {
        List<Book> books = bookDAO.getAll();
        if (!books.isEmpty()) {
            List<BookWithAverageRatingDTO> bookWithAverageRatingDTOs = fillListOfBookWithAverageRatingDTO(books);
            return new ResponseEntity<>(bookWithAverageRatingDTOs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    public ResponseEntity<Book> updateBook(Book book, Long id) {
        Book updatedBook = bookDAO.getBookById(id);
        if (updatedBook != null) {
            updatedBook.setId(id);
            updatedBook.setBookName(book.getBookName());
            updatedBook.setAuthor(book.getAuthor());

            bookDAO.updateBook(updatedBook);

            return new ResponseEntity<>(updatedBook, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Book> saveBook(Book book) {
        try {
            Book savedBook = bookDAO.saveBook(book);
            return new ResponseEntity<>(savedBook, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Void> deleteBook(Long id) {
        try {
            bookReviewDAO.deleteBookReviewByBookId(id);
            int resultBook = bookDAO.deleteBookById(id);
            if (resultBook == 0) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<BookWithAverageRatingDTO>> getBooksByAuthorName(String authorName) {
        List<Book> books = bookDAO.getBooksByAuthorName(authorName);
        if (!books.isEmpty()) {
            List<BookWithAverageRatingDTO> bookWithAverageRatingDTOs = fillListOfBookWithAverageRatingDTO(books);
            return new ResponseEntity<>(bookWithAverageRatingDTOs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
}
