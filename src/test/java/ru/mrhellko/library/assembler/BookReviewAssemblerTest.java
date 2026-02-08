package ru.mrhellko.library.assembler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.dao.BookReviewDAO;
import ru.mrhellko.library.dto.BookReviewByBookIdDTO;
import ru.mrhellko.library.dto.BookReviewByReviewerNameDTO;
import ru.mrhellko.library.exception.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookReviewAssemblerTest {

    @Mock
    private BookReviewDAO bookReviewDAO;

    @InjectMocks
    private BookReviewAssembler bookReviewAssembler;

    /**
     * Если по bookId нет отзывов, то возвращается пустой список.
     */
    @Test
    void getReviewByBookIdNotFoundTest() {
        when(bookReviewDAO.getReviewByBookId(1L)).thenReturn(List.of());

        List<BookReviewByBookIdDTO> dtos = bookReviewAssembler.getReviewByBookId(1L);
        assertThat(dtos).isEmpty();
    }

    /**
     * Если по bookId есть отзывы, то возвращается список DTO с корректно заполненными полями.
     */
    @Test
    void getReviewByBookIdFoundTest() {
        BookReview r = new BookReview();
        r.setReviewerName("Anna");
        r.setReviewText("text");
        r.setRating((byte) 8);

        when(bookReviewDAO.getReviewByBookId(1L)).thenReturn(List.of(r));

        List<BookReviewByBookIdDTO> dtos = bookReviewAssembler.getReviewByBookId(1L);
        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst().getReviewerName()).isEqualTo("Anna");
        assertThat(dtos.getFirst().getReviewText()).isEqualTo("text");
        assertThat(dtos.getFirst().getRating()).isEqualTo((byte) 8);
    }

    /**
     * Поиск отзывов по reviewerName делегируется в DAO и возвращает список DTO.
     */
    @Test
    void getReviewByReviewerNameTest() {
        when(bookReviewDAO.getReviewByReviewerName("Sergei")).thenReturn(List.of(new BookReviewByReviewerNameDTO()));

        List<BookReviewByReviewerNameDTO> result = bookReviewAssembler.getReviewByReviewerName("Sergei");
        assertThat(result).hasSize(1);

        verify(bookReviewDAO).getReviewByReviewerName("Sergei");
    }

    /**
     * Если отзыв для обновления не найден, то возвращается null и update не вызывается.
     */
    @Test
    void updateBookReviewNotFoundTest() {
        when(bookReviewDAO.getReviewById(1L)).thenReturn(null);

        BookReview input = new BookReview();
        input.setBookId(1L);

        BookReview updated = bookReviewAssembler.updateBookReview(input, 1L);
        assertThat(updated).isNull();

        verify(bookReviewDAO).getReviewById(1L);
        verify(bookReviewDAO, never()).updateBookReview(any());
    }

    /**
     * Если отзыв найден, то поля обновляются и update вызывается.
     */
    @Test
    void updateBookReviewFoundTest() {
        BookReview existing = new BookReview();
        existing.setId(1L);

        when(bookReviewDAO.getReviewById(1L)).thenReturn(existing);

        BookReview input = new BookReview();
        input.setBookId(2L);
        input.setRating((byte) 10);
        input.setReviewerName("name");
        input.setReviewText("text");

        BookReview updated = bookReviewAssembler.updateBookReview(input, 1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getBookId()).isEqualTo(2L);
        assertThat(updated.getRating()).isEqualTo((byte) 10);
        assertThat(updated.getReviewerName()).isEqualTo("name");
        assertThat(updated.getReviewText()).isEqualTo("text");

        verify(bookReviewDAO).updateBookReview(existing);
    }

    /**
     * Сохранение отзыва делегируется в DAO и возвращает результат сохранения.
     */
    @Test
    void saveBookReviewTest() throws Exception {
        BookReview input = new BookReview();
        BookReview saved = new BookReview();
        saved.setId(10L);

        when(bookReviewDAO.saveBookReview(input)).thenReturn(saved);

        BookReview result = bookReviewAssembler.saveBookReview(input);
        assertThat(result).isSameAs(saved);
    }

    /**
     * Если удаление отзыва в DAO вернуло 0, то выбрасывается NotFoundException.
     */
    @Test
    void deleteBookReviewByIdNotFoundTest() throws Exception {
        when(bookReviewDAO.deleteBookReviewById(1L)).thenReturn(0);

        assertThatThrownBy(() -> bookReviewAssembler.deleteBookReviewById(1L))
                .isInstanceOf(NotFoundException.class);

        verify(bookReviewDAO).deleteBookReviewById(1L);
    }

    /**
     * Если удаление отзыва прошло успешно, то исключение не выбрасывается.
     */
    @Test
    void deleteBookReviewByIdOkTest() throws Exception {
        when(bookReviewDAO.deleteBookReviewById(1L)).thenReturn(1);

        bookReviewAssembler.deleteBookReviewById(1L);

        verify(bookReviewDAO).deleteBookReviewById(1L);
    }
}
