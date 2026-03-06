package ru.mrhellko.library.assembler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mrhellko.library.Entity.Genre;
import ru.mrhellko.library.dao.GenreDAO;
import ru.mrhellko.library.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {
    @Mock
    private GenreDAO genreDAO;

    @InjectMocks
    private GenreService genreService;

    /**
     * Получение всех жанров возвращается корректно
     */
    @Test
    void GetAllGenresTest() {
        Genre g1 = new Genre();
        g1.setId(1L);
        g1.setGenreName("g1");

        Genre g2 = new Genre();
        g2.setId(2L);
        g2.setGenreName("g2");

        when(genreDAO.getAllGenres()).thenReturn(Arrays.asList(g1, g2));

        List<Genre> genreList = genreDAO.getAllGenres();
        assertThat(genreList).hasSize(2);
        assertThat(genreList)
                .extracting(Genre::getId)
                .containsExactly(1L, 2L);
    }

    /**
     * Если по id нет жанра, то возвращается null.
     */
    @Test
    void getGenreByIdNotFoundTest() {
        when(genreDAO.getGenreById(1L)).thenReturn(null);

        Genre genre = genreService.getGenreById(1L);
        assertThat(genre).isNull();
    }

    /**
     * Если по id есть жанр, то возвращается найденный жанр.
     */
    @Test
    void getGenreByIdFoundTest() {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setGenreName("name");

        when(genreDAO.getGenreById(1L)).thenReturn(genre);

        Genre foundGenre = genreService.getGenreById(1L);
        assertThat(foundGenre.getId()).isEqualTo(1);
        assertThat(foundGenre.getGenreName()).isEqualTo("name");
    }

    /**
     * Если жанр для обновления не найден, то возвращается null и update не вызывается.
     */
    @Test
    void updateGenreNotFoundTest() {
        when(genreDAO.getGenreById(1L)).thenReturn(null);

        Genre input = new Genre();
        input.setId(1L);

        Genre updated = genreService.updateGenre(input, 1L);
        assertThat(updated).isNull();

        verify(genreDAO).getGenreById(1L);
        verify(genreDAO, never()).updateGenre(any());
    }

    /**
     * Если жанр найден, то поля обновляются и update вызывается.
     */
    @Test
    void updateGenreFoundTest() {
        Genre existing = new Genre();
        existing.setId(1L);

        when(genreDAO.getGenreById(1L)).thenReturn(existing);

        Genre input = new Genre();
        input.setGenreName("name");

        Genre updated = genreService.updateGenre(input, 1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getGenreName()).isEqualTo("name");

        verify(genreDAO).updateGenre(existing);
    }

    /**
     * Сохранение жанра делегируется в DAO и возвращает результат сохранения.
     */
    @Test
    void saveGenreTest() {
        Genre input = new Genre();
        Genre saved = new Genre();
        saved.setId(10L);

        when(genreDAO.saveGenre(input)).thenReturn(saved);

        Genre result = genreService.saveGenre(input);
        assertThat(result).isSameAs(saved);
    }

    /**
     * Если удаление жанра в DAO вернуло 0, то выбрасывается NotFoundException.
     */
    @Test
    void deleteGenreByIdNotFoundTest() {
        when(genreDAO.deleteGenreById(1L)).thenReturn(0);

        assertThatThrownBy(() -> genreService.deleteGenre(1L))
                .isInstanceOf(NotFoundException.class);

        verify(genreDAO).deleteGenreById(1L);
    }

    /**
     * Если удаление жанра прошло успешно, то исключение не выбрасывается.
     */
    @Test
    void deleteGenreByIdOkTest() {
        when(genreDAO.deleteGenreById(1L)).thenReturn(1);

        genreService.deleteGenre(1L);

        verify(genreDAO).deleteGenreById(1L);
    }
}
