package ru.mrhellko.library.assembler;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Book;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.dao.BookDAO;
import ru.mrhellko.library.dao.BookReviewDAO;
import ru.mrhellko.library.dto.BookWithAverageRatingDTO;
import ru.mrhellko.library.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookAssembler {
    @Autowired
    private BookDAO bookDAO;
    @Autowired
    private BookReviewDAO bookReviewDAO;

    public BookWithAverageRatingDTO getFullBookWithAverageRatingDTO(Long id) {
        Book book = bookDAO.getBookById(id);
        if (book != null) {
            BookWithAverageRatingDTO bookWithAverageRatingDTO = new BookWithAverageRatingDTO(book);
            bookWithAverageRatingDTO.setAverageRating(getAverageRating(id));
            return bookWithAverageRatingDTO;
        } else {
            return null;
        }
    }

    public List<BookWithAverageRatingDTO> getFullAllBooks() {
        List<Book> books = bookDAO.getAll();
        return fillListOfBookWithAverageRatingDTO(books);
    }

    public Book updateBook(Book book, Long id) {
        Book updatedBook = bookDAO.getBookById(id);
        if (updatedBook != null) {
            updatedBook.setId(id);
            updatedBook.setBookName(book.getBookName());
            updatedBook.setAuthor(book.getAuthor());

            bookDAO.updateBook(updatedBook);
            return updatedBook;
        } else {
            return null;
        }
    }

    public Book saveBook(Book book) throws Exception {
        return bookDAO.saveBook(book);
    }

    public void deleteBook(Long id) throws Exception {
        int resultBook = bookDAO.deleteBookById(id);
        if (resultBook == 0) {
            throw new NotFoundException(id);
        }
    }

    public List<BookWithAverageRatingDTO> getBooksByAuthorName(String authorName) {
        List<Book> books = bookDAO.getBooksByAuthorName(authorName);
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
}
