package ru.mrhellko.library.assembler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mrhellko.library.Entity.Author;
import ru.mrhellko.library.Entity.Book;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.dao.AuthorDAO;
import ru.mrhellko.library.dao.BookDAO;
import ru.mrhellko.library.dao.BookReviewDAO;
import ru.mrhellko.library.dto.BookAuthorDTO;
import ru.mrhellko.library.dto.BookWithAverageRatingDTO;
import ru.mrhellko.library.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookAssemblerTest {

    @Mock
    private BookDAO bookDAO;

    @Mock
    private BookReviewDAO bookReviewDAO;

    @Mock
    private AuthorDAO authorDAO;

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

        Author a1 = new Author(1L, "a1");
        Author a2 = new Author(2L, "a2");

        BookReview r1 = new BookReview();
        r1.setRating((byte) 8);
        BookReview r2 = new BookReview();
        r2.setRating((byte) 6);

        when(bookDAO.getBookById(1L)).thenReturn(book);
        when(bookReviewDAO.getReviewByBookId(1L)).thenReturn(Arrays.asList(r1, r2));
        when(authorDAO.getAuthorsForBooks(Set.of(1L))).thenReturn(Arrays.asList(
                new BookAuthorDTO(1L, 1L, "a1"),
                new BookAuthorDTO(1L, 2L, "a2")));

        BookWithAverageRatingDTO dto = bookAssembler.getFullBookWithAverageRatingDTO(1L);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getBookName()).isEqualTo("name");
        assertThat(dto.getAuthors()).isEqualTo(List.of(a1, a2));
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
        List<Author> authors = new ArrayList<>();
        authors.add(new Author(1L, "author"));
        book.setAuthors(authors);

        when(bookDAO.getBookById(1L)).thenReturn(book);
        when(bookReviewDAO.getReviewByBookId(1L)).thenReturn(Arrays.asList());
        when(authorDAO.getAuthorsForBooks(Set.of(1L))).thenReturn(Arrays.asList(
                new BookAuthorDTO(1L, 1L, "author")));

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
        Author a1 = new Author(1L, "a1");

        Book b2 = new Book();
        b2.setId(2L);
        b2.setBookName("b2");
        Author a2 = new Author(2L, "a2");

        BookReview r = new BookReview();
        r.setRating((byte) 10);

        when(bookDAO.getAll()).thenReturn(Arrays.asList(b1, b2));
        when(bookReviewDAO.getReviewByBookId(1L)).thenReturn(Arrays.asList(r));
        when(bookReviewDAO.getReviewByBookId(2L)).thenReturn(Arrays.asList());
        when(authorDAO.getAuthorsForBooks(Set.of(1L, 2L))).thenReturn(Arrays.asList(
                new BookAuthorDTO(1L, 1L, "a1"),
                new BookAuthorDTO(2L, 2L, "a2")));

        List<BookWithAverageRatingDTO> dtos = bookAssembler.getFullAllBooks();
        assertThat(dtos).hasSize(2);
        assertThat(dtos)
                .extracting(BookWithAverageRatingDTO::getId)
                .containsExactly(1L, 2L);
        assertThat(dtos.get(0).getAverageRating()).isEqualTo(10.0f);
        assertThat(dtos.get(1).getAverageRating()).isNull();
        assertThat(dtos.get(0).getAuthors()).isEqualTo(List.of(a1));
        assertThat(dtos.get(1).getAuthors()).isEqualTo(List.of(a2));
    }

    /**
     * Если авторы отсутствуют в новой книге, то выбрасывается IllegalArgumentException.
     */
    @Test
    void updateBookNoAuthorsTest() {
        Book input = new Book();
        input.setId(1L);
        input.setBookName("n");

        assertThatThrownBy(() -> bookAssembler.updateBook(input, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(bookDAO, never()).updateBook(any());
    }

    /**
     * Если авторы в новой книге не найдены, то выбрасывается IllegalArgumentException.
     */
    @Test
    void updateBookNotFoundAuthorTest() {
        Book input = new Book();
        input.setId(1L);
        input.setBookName("n");
        input.setAuthors(List.of(new Author(1L, "a")));

        when(authorDAO.getAuthorById(1L)).thenReturn(null);

        assertThatThrownBy(() -> bookAssembler.updateBook(input, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(bookDAO, never()).updateBook(any());
    }

    /**
     * Если книга для обновления не найдена, то возвращается null и update не вызывается.
     */
    @Test
    void updateBookNotFoundTest() throws Exception {
        Book input = new Book();
        input.setBookName("n");
        Author author = new Author(1L, "a");
        input.setAuthors(List.of(author));

        when(bookDAO.getBookById(1L)).thenReturn(null);
        when(authorDAO.getAuthorById(1L)).thenReturn(author);

        Book updated = bookAssembler.updateBook(input, 1L);
        assertThat(updated).isNull();

        verify(bookDAO).getBookById(1L);
        verify(bookDAO, never()).updateBook(any());
    }

    /**
     * Если книга найдена, то поля обновляются и update вызывается для существующей книги.
     */
    @Test
    void updateBookFoundTest() throws Exception {
        Book existing = new Book();
        existing.setId(1L);
        existing.setBookName("old");

        Book input = new Book();
        input.setBookName("new");
        Author a2 = new Author(2L, "newAuthor");
        List<Author> newAuthors = new ArrayList<>();
        newAuthors.add(a2);
        input.setAuthors(newAuthors);

        when(bookDAO.getBookById(1L)).thenReturn(existing);
        when(authorDAO.getAuthorsForBooks(Set.of(1L))).thenReturn(Arrays.asList(
                new BookAuthorDTO(1L, 1L, "old")
        ));
        when(authorDAO.getAuthorById(2L)).thenReturn(a2);
        when(bookDAO.deleteBookAuthor(1L, 1L)).thenReturn(1);

        Book updated = bookAssembler.updateBook(input, 1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getBookName()).isEqualTo("new");
        assertThat(updated.getAuthors()).isEqualTo(List.of(a2));

        verify(bookDAO).updateBook(existing);
        verify(bookDAO).saveBookAuthor(1L, 2L);
        verify(bookDAO).deleteBookAuthor(1L, 1L);
    }

    /**
     * Сохранение книги делегируется в DAO и возвращает результат сохранения.
     */
    @Test
    void saveBookTest() throws Exception {
        Book input = new Book();
        input.setBookName("n");
        Author author = new Author(1L, "a");
        input.setAuthors(List.of(author));

        Book saved = new Book();
        saved.setId(10L);
        saved.setBookName("n");
        saved.setAuthors(List.of(author));

        when(bookDAO.saveBook(input)).thenReturn(saved);
        when(authorDAO.getAuthorById(1L)).thenReturn(author);

        Book result = bookAssembler.saveBook(input);
        assertThat(result).isSameAs(saved);
        verify(bookDAO).saveBookAuthor(10L, 1L);
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
        b.setAuthors(List.of(new Author(1L, "a")));

        when(bookDAO.getBooksByAuthorName("a")).thenReturn(List.of(b));
        when(bookReviewDAO.getReviewByBookId(1L)).thenReturn(List.of());

        List<BookWithAverageRatingDTO> dtos = bookAssembler.getBooksByAuthorName("a");
        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst().getId()).isEqualTo(1L);
    }

    /**
     * Поиск книг по id автора возвращает DTO и корректно проставляет averageRating.
     */
    @Test
    void getBooksByAuthorIdTest() {
        Book b = new Book();
        b.setId(1L);
        b.setBookName("b");
        b.setAuthors(List.of(new Author(1L, "a")));

        when(bookDAO.getBooksByAuthorId(1L)).thenReturn(List.of(b));
        when(bookReviewDAO.getReviewByBookId(1L)).thenReturn(List.of());

        List<BookWithAverageRatingDTO> dtos = bookAssembler.getBooksByAuthorId(1L);
        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst().getId()).isEqualTo(1L);
    }
}
