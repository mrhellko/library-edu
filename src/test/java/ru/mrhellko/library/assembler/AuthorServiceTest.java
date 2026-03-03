package ru.mrhellko.library.assembler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mrhellko.library.Entity.Author;
import ru.mrhellko.library.dao.AuthorDAO;
import ru.mrhellko.library.exception.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @Mock
    private AuthorDAO authorDAO;

    @InjectMocks
    private AuthorService authorService;

    /**
     * Если по id нет автора, то возвращается null.
     */
    @Test
    void getAuthorByIdNotFoundTest() {
        when(authorDAO.getAuthorById(1L)).thenReturn(null);

        Author author = authorService.getAuthorById(1L);
        assertThat(author).isNull();
    }

    /**
     * Если по id есть автор, то возвращается найденный автор.
     */
    @Test
    void getAuthorByIdFoundTest() {
        Author author = new Author();
        author.setId(1L);
        author.setAuthorName("name");

        when(authorDAO.getAuthorById(1L)).thenReturn(author);

        Author foundAuthor = authorService.getAuthorById(1L);
        assertThat(foundAuthor.getId()).isEqualTo(1);
        assertThat(foundAuthor.getAuthorName()).isEqualTo("name");
    }

    /**
     * Если автор для обновления не найден, то возвращается null и update не вызывается.
     */
    @Test
    void updateAuthorNotFoundTest() {
        when(authorDAO.getAuthorById(1L)).thenReturn(null);

        Author input = new Author();
        input.setId(1L);

        Author updated = authorService.updateAuthor(input, 1L);
        assertThat(updated).isNull();

        verify(authorDAO).getAuthorById(1L);
        verify(authorDAO, never()).updateAuthor(any());
    }

    /**
     * Если автор найден, то поля обновляются и update вызывается.
     */
    @Test
    void updateAuthorFoundTest() {
        Author existing = new Author();
        existing.setId(1L);

        when(authorDAO.getAuthorById(1L)).thenReturn(existing);

        Author input = new Author();
        input.setAuthorName("name");

        Author updated = authorService.updateAuthor(input, 1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getAuthorName()).isEqualTo("name");

        verify(authorDAO).updateAuthor(existing);
    }

    /**
     * Сохранение автора делегируется в DAO и возвращает результат сохранения.
     */
    @Test
    void saveAuthorTest() throws Exception {
        Author input = new Author();
        Author saved = new Author();
        saved.setId(10L);

        when(authorDAO.saveAuthor(input)).thenReturn(saved);

        Author result = authorService.saveAuthor(input);
        assertThat(result).isSameAs(saved);
    }

    /**
     * Если удаление автора в DAO вернуло 0, то выбрасывается NotFoundException.
     */
    @Test
    void deleteAuthorByIdNotFoundTest() throws Exception {
        when(authorDAO.deleteAuthorById(1L)).thenReturn(0);

        assertThatThrownBy(() -> authorService.deleteAuthor(1L))
                .isInstanceOf(NotFoundException.class);

        verify(authorDAO).deleteAuthorById(1L);
    }

    /**
     * Если удаление автора прошло успешно, то исключение не выбрасывается.
     */
    @Test
    void deleteAuthorByIdOkTest() throws Exception {
        when(authorDAO.deleteAuthorById(1L)).thenReturn(1);

        authorService.deleteAuthor(1L);

        verify(authorDAO).deleteAuthorById(1L);
    }
}
