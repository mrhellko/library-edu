package ru.mrhellko.library.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mrhellko.library.Entity.Genre;
import ru.mrhellko.library.dto.BookGenreDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class GenreDAOTest extends AbstractDAOTest {
    @Autowired
    private GenreDAO genreDAO;

    /**
     * Получение всех жанров возвращает не пусток список с начальными данными.
     */
    @Test
    void getAllGenresTest() {
        List<Genre> genres = genreDAO.getAllGenres();
        assertThat(genres).hasSizeGreaterThanOrEqualTo(6);
        assertThat(genres)
                .map(Genre::getGenreName)
                .contains("Фэнтези")
                .contains("Драма")
                .contains("Научная фантастика")
                .contains("Средневековье")
                .contains("Роман")
                .contains("Юмор");
    }

    /**
     * Если жанр по id не найден, то возвращается null.
     */
    @Test
    void getGenreByIdNotFoundTest() {
        Genre genre = genreDAO.getGenreById(99999L);
        assertThat(genre).isNull();
    }

    /**
     * Если жанр по id найден, то возвращаются заполненные поля жанра.
     */
    @Test
    void getGenreByIdFoundTest() {
        Genre genre = genreDAO.getGenreById(1L);
        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1);
        assertThat(genre.getGenreName()).isNotBlank();
    }

    /**
     * Обновление жанра по id изменяет сохранённые значения в базе данных.
     */
    @Test
    void updateGenreTest() {
        Genre genre = genreDAO.getGenreById(1L);
        assertThat(genre).isNotNull();

        genre.setGenreName("name");

        genreDAO.updateGenre(genre);

        Genre updated = genreDAO.getGenreById(1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getGenreName()).isEqualTo("name");
    }

    /**
     * Сохранение нового жанра присваивает id и позволяет прочитать жанр из базы данных.
     */
    @Test
    void saveGenreTest() {
        Genre newGenre = new Genre();
        newGenre.setGenreName("name");

        Genre saved = genreDAO.saveGenre(newGenre);
        assertThat(saved.getId()).isNotNull();

        Genre found = genreDAO.getGenreById(saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getGenreName()).isEqualTo("name");
    }

    /**
     * Удаление существующего жанра возвращает 1 и жанр перестаёт находиться по id.
     */
    @Test
    void deleteGenreByIdTest() {
        Genre newGenre = new Genre();
        newGenre.setGenreName("name");

        Genre saved = genreDAO.saveGenre(newGenre);

        int deleted = genreDAO.deleteGenreById(saved.getId());
        assertThat(deleted).isEqualTo(1);
        assertThat(genreDAO.getGenreById(saved.getId())).isNull();
    }

    /**
     * Удаление несуществующего жанра возвращает 0.
     */
    @Test
    void deleteGenreByIdNotFoundTest() {
        int deleted = genreDAO.deleteGenreById(99999L);
        assertThat(deleted).isEqualTo(0);
    }

    /**
     * По сету из нескольких bookId возвращает List из BookGenreDTO
     */
    @Test
    void getGenresForBooksManyFound() {
        Set<Long> bookIds = new HashSet<>(Set.of(1L, 2L, 3L, 4L, 5L, 6L));

        List<BookGenreDTO> bookGenreDTOS = genreDAO.getGenresForBooks(bookIds);
        assertThat(bookGenreDTOS).isNotNull();
        assertThat(bookGenreDTOS.size()).isGreaterThanOrEqualTo(13);
        assertThat(bookGenreDTOS)
                .map(BookGenreDTO::getGenreName)
                .contains("Фэнтези")
                .contains("Драма")
                .contains("Научная фантастика")
                .contains("Средневековье")
                .contains("Роман")
                .contains("Юмор");
    }

    /**
     * По пустому сету bookIds возвращает пустую коллекцию.
     */
    @Test
    void getGenresForBooksEmptyBookId() {
        Set<Long> bookIds = new HashSet<>();
        List<BookGenreDTO> dto = genreDAO.getGenresForBooks(bookIds);

        assertThat(dto).isNotNull();
        assertThat(dto).isEmpty();
    }
}
