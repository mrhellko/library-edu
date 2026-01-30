package ru.mrhellko.library.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mrhellko.library.Entity.Book;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BookDAOTest {
    @Autowired
    private BookDAO bookDAO;

    /**
     * Если автор не нашелся, то возвращается пустой список
     */
    @Test
    void notFoundTest() {
        List<Book> notExistenceAuthor = bookDAO.getBooksByAuthorName("not existence author");
        assertThat(notExistenceAuthor).isEmpty();
    }

    /**
     * По запросу "И" возвращает как минимум двух авторов
     */
    @Test
    void foundManyTest() {
        List<Book> notExistenceAuthor = bookDAO.getBooksByAuthorName("И");
        assertThat(notExistenceAuthor)
                .map(Book::getAuthor)
                .contains("Лю Цысинь")
                .contains("Джордж Мартин");
    }

    /**
     * По запросу "Joan Rowling" возращает только те книги в которых автор Joan Rowling.
     */
    @Test
    void foundOneTest() {
        List<Book> notExistenceAuthor = bookDAO.getBooksByAuthorName("Joan Rowling");
        assertThat(notExistenceAuthor)
                .map(Book::getAuthor)
                .allMatch(author -> author.equals("Joan Rowling"));
    }
}
