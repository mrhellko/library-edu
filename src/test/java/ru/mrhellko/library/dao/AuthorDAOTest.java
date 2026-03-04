package ru.mrhellko.library.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mrhellko.library.Entity.Author;
import ru.mrhellko.library.dto.BookAuthorDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class AuthorDAOTest {
    @Autowired
    private AuthorDAO authorDAO;

    /**
     * Если автор по id не найден, то возвращается null.
     */
    @Test
    void getAuthorByIdNotFoundTest() {
        Author author = authorDAO.getAuthorById(99999L);
        assertThat(author).isNull();
    }

    /**
     * Если автор по id найден, то возвращаются заполненные поля автора.
     */
    @Test
    void getAuthorByIdFoundTest() {
        Author author = authorDAO.getAuthorById(1L);
        assertThat(author).isNotNull();
        assertThat(author.getId()).isEqualTo(1);
        assertThat(author.getAuthorName()).isNotBlank();
    }

    /**
     * Обновление автора по id изменяет сохранённые значения в базе данных.
     */
    @Test
    void updateAuthorTest() {
        Author author = authorDAO.getAuthorById(1L);
        assertThat(author).isNotNull();

        author.setAuthorName("name");

        authorDAO.updateAuthor(author);

        Author updated = authorDAO.getAuthorById(1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getAuthorName()).isEqualTo("name");
    }

    /**
     * Сохранение нового автора присваивает id и позволяет прочитать автора из базы данных.
     */
    @Test
    void saveAuthorTest() {
        Author newAuthor = new Author();
        newAuthor.setAuthorName("name");

        Author saved = authorDAO.saveAuthor(newAuthor);
        assertThat(saved.getId()).isNotNull();

        Author found = authorDAO.getAuthorById(saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getAuthorName()).isEqualTo("name");
    }

    /**
     * Удаление существующего автора возвращает 1 и автор перестаёт находиться по id.
     */
    @Test
    void deleteAuthorByIdTest() {
        Author newAuthor = new Author();
        newAuthor.setAuthorName("name");

        Author saved = authorDAO.saveAuthor(newAuthor);

        int deleted = authorDAO.deleteAuthorById(saved.getId());
        assertThat(deleted).isEqualTo(1);
        assertThat(authorDAO.getAuthorById(saved.getId())).isNull();
    }

    /**
     * Удаление несуществующего автора возвращает 0.
     */
    @Test
    void deleteAuthorByIdNotFoundTest() {
        int deleted = authorDAO.deleteAuthorById(99999L);
        assertThat(deleted).isEqualTo(0);
    }

    /**
     * По сету из нескольких bookId возвращает List из BookAuthorDTO
     */
    @Test
    void getAuthorsForBooksManyFound() {
        Set<Long> bookIds = new HashSet<>(Set.of(1L, 2L, 3L, 4L, 5L, 6L));

        List<BookAuthorDTO> bookAuthorDTOS = authorDAO.getAuthorsForBooks(bookIds);
        assertThat(bookAuthorDTOS).isNotNull();
        assertThat(bookAuthorDTOS.size()).isGreaterThanOrEqualTo(9);
        assertThat(bookAuthorDTOS)
                .map(BookAuthorDTO::getAuthorName)
                .contains("Joan Rowling")
                .contains("Лю Цысинь")
                .contains("Джордж Мартин")
                .contains("Терри Пратчетт")
                .contains("Нил Гейман")
                .contains("Стивен Бакстер")
                .contains("Илья Ильф")
                .contains("Евгений Петров");
    }

    /**
     * По пустому сету bookIds возвращает пустую коллекцию.
     */
    @Test
    void getAuthorsForBooksEmptyBookId() {
        Set<Long> bookIds = new HashSet<>();
        List<BookAuthorDTO> dto = authorDAO.getAuthorsForBooks(bookIds);

        assertThat(dto).isNotNull();
        assertThat(dto).isEmpty();
    }
}
