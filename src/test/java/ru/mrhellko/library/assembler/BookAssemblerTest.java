package ru.mrhellko.library.assembler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mrhellko.library.Entity.Book;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.dao.BookDAO;
import ru.mrhellko.library.dao.BookReviewDAO;
import ru.mrhellko.library.dto.BookWithAverageRatingDTO;
import ru.mrhellko.library.exception.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookAssemblerTest {

    @Mock
    private BookDAO bookDAO;

    @Mock
    private BookReviewDAO bookReviewDAO;

    @InjectMocks
    private BookAssembler bookAssembler;

    /**
     * Если книга по id не найдена, то возвращается null и запросов к отзывам не происходит.
     */
    @Test
    void getFullBookWithAverageRatingDTONotFoundTest() {
        when(bookDAO.getBookById(1L)).thenReturn(null);

        BookWithAverageRatingDTO dto = bookAssembler.getFullBookWithAverageRatingDTO(1L);
        assertThat(dto).isNull();

        verify(bookDAO).getBookById(1L);
        verifyNoMoreInteractions(bookReviewDAO);
    }

    /**
     * Если книга найдена и есть отзывы, то averageRating рассчитывается как среднее значение рейтингов.
     */
    @Test
    void getFullBookWithAverageRatingDTOFoundWithAverageTest() {
        Book book = new Book();
        book.setId(1L);
        book.setBookName("name");
        book.setAuthor("author");

        BookReview r1 = new BookReview();
        r1.setRating((byte) 8);
        BookReview r2 = new BookReview();
        r2.setRating((byte) 6);

        when(bookDAO.getBookById(1L)).thenReturn(book);
        when(bookReviewDAO.getReviewByBookId(1L)).thenReturn(List.of(r1, r2));

        BookWithAverageRatingDTO dto = bookAssembler.getFullBookWithAverageRatingDTO(1L);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getBookName()).isEqualTo("name");
        assertThat(dto.getAuthor()).isEqualTo("author");
        assertThat(dto.getAverageRating()).isEqualTo(7.0f);
    }

    /**
     * Если книга найдена, но отзывов нет, то averageRating равен null.
     */
    @Test
    void getFullBookWithAverageRatingDTOFoundWithoutReviewsTest() {
        Book book = new Book();
        book.setId(1L);
        book.setBookName("name");
        book.setAuthor("author");

        when(bookDAO.getBookById(1L)).thenReturn(book);
        when(bookReviewDAO.getReviewByBookId(1L)).thenReturn(List.of());

        BookWithAverageRatingDTO dto = bookAssembler.getFullBookWithAverageRatingDTO(1L);
        assertThat(dto).isNotNull();
        assertThat(dto.getAverageRating()).isNull();
    }

    /**
     * Получение всех книг возвращает DTO для каждой книги и корректно проставляет averageRating.
     */
    @Test
    void getFullAllBooksTest() {
        Book b1 = new Book();
        b1.setId(1L);
        b1.setBookName("b1");
        b1.setAuthor("a1");

        Book b2 = new Book();
        b2.setId(2L);
        b2.setBookName("b2");
        b2.setAuthor("a2");

        BookReview r = new BookReview();
        r.setRating((byte) 10);

        when(bookDAO.getAll()).thenReturn(List.of(b1, b2));
        when(bookReviewDAO.getReviewByBookId(1L)).thenReturn(List.of(r));
        when(bookReviewDAO.getReviewByBookId(2L)).thenReturn(List.of());

        List<BookWithAverageRatingDTO> dtos = bookAssembler.getFullAllBooks();
        assertThat(dtos).hasSize(2);
        assertThat(dtos)
                .extracting(BookWithAverageRatingDTO::getId)
                .containsExactly(1L, 2L);
        assertThat(dtos.get(0).getAverageRating()).isEqualTo(10.0f);
        assertThat(dtos.get(1).getAverageRating()).isNull();
    }

    /**
     * Если книга для обновления не найдена, то возвращается null и update не вызывается.
     */
    @Test
    void updateBookNotFoundTest() {
        when(bookDAO.getBookById(1L)).thenReturn(null);

        Book input = new Book();
        input.setBookName("n");
        input.setAuthor("a");

        Book updated = bookAssembler.updateBook(input, 1L);
        assertThat(updated).isNull();

        verify(bookDAO).getBookById(1L);
        verify(bookDAO, never()).updateBook(any());
    }

    /**
     * Если книга найдена, то поля обновляются и update вызывается для существующей книги.
     */
    @Test
    void updateBookFoundTest() {
        Book existing = new Book();
        existing.setId(1L);
        existing.setBookName("old");
        existing.setAuthor("old");

        when(bookDAO.getBookById(1L)).thenReturn(existing);

        Book input = new Book();
        input.setBookName("new");
        input.setAuthor("new");

        Book updated = bookAssembler.updateBook(input, 1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getBookName()).isEqualTo("new");
        assertThat(updated.getAuthor()).isEqualTo("new");

        verify(bookDAO).updateBook(existing);
    }

    /**
     * Сохранение книги делегируется в DAO и возвращает результат сохранения.
     */
    @Test
    void saveBookTest() throws Exception {
        Book input = new Book();
        input.setBookName("n");
        input.setAuthor("a");

        Book saved = new Book();
        saved.setId(10L);
        saved.setBookName("n");
        saved.setAuthor("a");

        when(bookDAO.saveBook(input)).thenReturn(saved);

        Book result = bookAssembler.saveBook(input);
        assertThat(result).isSameAs(saved);
    }

    /**
     * Если удаление книги в DAO вернуло 0, то выбрасывается NotFoundException.
     */
    @Test
    void deleteBookNotFoundTest() throws Exception {
        when(bookDAO.deleteBookById(1L)).thenReturn(0);

        assertThatThrownBy(() -> bookAssembler.deleteBook(1L))
                .isInstanceOf(NotFoundException.class);

        verify(bookDAO).deleteBookById(1L);
    }

    /**
     * Если удаление книги прошло успешно, то исключение не выбрасывается.
     */
    @Test
    void deleteBookOkTest() throws Exception {
        when(bookDAO.deleteBookById(1L)).thenReturn(1);

        bookAssembler.deleteBook(1L);

        verify(bookDAO).deleteBookById(1L);
    }

    /**
     * Поиск книг по автору возвращает DTO и корректно проставляет averageRating.
     */
    @Test
    void getBooksByAuthorNameTest() {
        Book b = new Book();
        b.setId(1L);
        b.setBookName("b");
        b.setAuthor("a");

        when(bookDAO.getBooksByAuthorName("a")).thenReturn(List.of(b));
        when(bookReviewDAO.getReviewByBookId(1L)).thenReturn(List.of());

        List<BookWithAverageRatingDTO> dtos = bookAssembler.getBooksByAuthorName("a");
        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst().getId()).isEqualTo(1L);
    }
}
