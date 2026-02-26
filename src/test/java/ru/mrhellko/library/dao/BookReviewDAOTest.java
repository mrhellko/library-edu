package ru.mrhellko.library.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.dto.BookReviewByReviewerNameDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class BookReviewDAOTest {
    @Autowired
    private BookReviewDAO bookReviewDAO;

    /**
     * Если отзыв по id не найден, то возвращается null.
     */
    @Test
    void getReviewByIdNotFoundTest() {
        BookReview review = bookReviewDAO.getReviewById(99999);
        assertThat(review).isNull();
    }

    /**
     * Если отзыв по id найден, то возвращаются заполненные поля отзыва.
     */
    @Test
    void getReviewByIdFoundTest() {
        BookReview review = bookReviewDAO.getReviewById(1);
        assertThat(review).isNotNull();
        assertThat(review.getId()).isEqualTo(1);
        assertThat(review.getBookId()).isNotNull();
        assertThat(review.getRating()).isNotNull();
        assertThat(review.getReviewerName()).isNotBlank();
        assertThat(review.getReviewText()).isNotBlank();
    }

    /**
     * Если у книги нет отзывов, то возвращается пустой список.
     */
    @Test
    void getReviewByBookIdNotFoundTest() {
        List<BookReview> reviews = bookReviewDAO.getReviewByBookId(99999);
        assertThat(reviews).isEmpty();
    }

    /**
     * Если у книги есть отзывы, то возвращается список отзывов и у всех отзывов корректный bookId.
     */
    @Test
    void getReviewByBookIdFoundTest() {
        List<BookReview> reviews = bookReviewDAO.getReviewByBookId(1);
        assertThat(reviews).isNotEmpty();
        assertThat(reviews)
                .allMatch(r -> r.getBookId().equals(1L));
    }

    /**
     * Если по reviewerName не найдено отзывов, то возвращается пустой список.
     */
    @Test
    void getReviewByReviewerNameNotFoundTest() {
        List<BookReviewByReviewerNameDTO> reviews = bookReviewDAO.getReviewByReviewerName("not existence reviewer");
        assertThat(reviews).isEmpty();
    }

    /**
     * Если по reviewerName найдены отзывы, то возвращаются DTO с заполненными полями текста/рейтинга/книги/автора.
     */
    @Test
    void getReviewByReviewerNameFoundTest() {
        List<BookReviewByReviewerNameDTO> reviews = bookReviewDAO.getReviewByReviewerName("Sergei");
        assertThat(reviews).isNotEmpty();
        assertThat(reviews)
                .allMatch(r -> r.getReviewText() != null)
                .allMatch(r -> r.getRating() != null)
                .allMatch(r -> r.getBookName() != null)
                .allMatch(r -> r.getAuthorName() != null);
    }

    /**
     * Обновление отзыва по id изменяет сохранённые значения в базе данных.
     */
    @Test
    void updateBookReviewTest() {
        BookReview review = bookReviewDAO.getReviewById(1);
        assertThat(review).isNotNull();

        review.setBookId(2L);
        review.setRating((byte) 10);
        review.setReviewerName("Updated reviewer");
        review.setReviewText("Updated text");

        bookReviewDAO.updateBookReview(review);

        BookReview updated = bookReviewDAO.getReviewById(1);
        assertThat(updated).isNotNull();
        assertThat(updated.getBookId()).isEqualTo(2L);
        assertThat(updated.getRating()).isEqualTo((byte) 10);
        assertThat(updated.getReviewerName()).isEqualTo("Updated reviewer");
        assertThat(updated.getReviewText()).isEqualTo("Updated text");
    }

    /**
     * Сохранение нового отзыва присваивает id и позволяет прочитать отзыв из базы данных.
     */
    @Test
    void saveBookReviewTest() throws Exception {
        BookReview newReview = new BookReview();
        newReview.setBookId(1L);
        newReview.setRating((byte) 9);
        newReview.setReviewerName("Test");
        newReview.setReviewText("Good book");

        BookReview saved = bookReviewDAO.saveBookReview(newReview);
        assertThat(saved.getId()).isNotNull();

        BookReview found = bookReviewDAO.getReviewById(saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getBookId()).isEqualTo(1L);
        assertThat(found.getRating()).isEqualTo((byte) 9);
        assertThat(found.getReviewerName()).isEqualTo("Test");
        assertThat(found.getReviewText()).isEqualTo("Good book");
    }

    /**
     * Удаление существующего отзыва возвращает 1 и отзыв перестаёт находиться по id.
     */
    @Test
    void deleteBookReviewByIdTest() throws Exception {
        BookReview newReview = new BookReview();
        newReview.setBookId(1L);
        newReview.setRating((byte) 5);
        newReview.setReviewerName("To delete");
        newReview.setReviewText("Delete me");

        BookReview saved = bookReviewDAO.saveBookReview(newReview);

        int deleted = bookReviewDAO.deleteBookReviewById(saved.getId());
        assertThat(deleted).isEqualTo(1);
        assertThat(bookReviewDAO.getReviewById(saved.getId())).isNull();
    }

    /**
     * Удаление несуществующего отзыва возвращает 0.
     */
    @Test
    void deleteBookReviewByIdNotFoundTest() throws Exception {
        int deleted = bookReviewDAO.deleteBookReviewById(99999L);
        assertThat(deleted).isEqualTo(0);
    }
}
