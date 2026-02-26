package ru.mrhellko.library.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mrhellko.library.Entity.Book;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class BookDAOTest {
    @Autowired
    private BookDAO bookDAO;

    /**
     * Если автор не нашелся, то возвращается пустой список
     */
    @Test
    void notFoundTest() {
        List<Book> books = bookDAO.getBooksByAuthorName("not existence author");
        assertThat(books).isEmpty();
    }

    /**
     * По запросу "И" возвращает как минимум двух авторов
     */
    @Test
    void foundManyTest() {
        List<Book> books = bookDAO.getBooksByAuthorName("И");
        assertThat(books)
                .map(Book::getAuthor)
                .contains("Лю Цысинь")
                .contains("Джордж Мартин");
    }

    /**
     * По запросу "Joan Rowling" возращает только те книги в которых автор Joan Rowling.
     */
    @Test
    void foundOneTest() {
        List<Book> books = bookDAO.getBooksByAuthorName("Joan Rowling");
        assertThat(books)
                .map(Book::getAuthor)
                .allMatch(author -> author.equals("Joan Rowling"));
    }

    /**
     * Если книга по id не найдена, то возвращается null.
     */
    @Test
    void getBookByIdNotFoundTest() {
        Book book = bookDAO.getBookById(99999);
        assertThat(book).isNull();
    }

    /**
     * Если книга по id найдена, то возвращаются корректные поля книги.
     */
    @Test
    void getBookByIdFoundTest() {
        Book book = bookDAO.getBookById(1);
        assertThat(book).isNotNull();
        assertThat(book.getId()).isEqualTo(1);
        assertThat(book.getBookName()).isEqualTo("Гарри Поттер");
        assertThat(book.getAuthor()).isEqualTo("Joan Rowling");
    }

    /**
     * Получение всех книг возвращает не пустой список с начальными данными.
     */
    @Test
    void getAllTest() {
        List<Book> books = bookDAO.getAll();
        assertThat(books).hasSizeGreaterThanOrEqualTo(3);
        assertThat(books)
                .map(Book::getAuthor)
                .contains("Joan Rowling")
                .contains("Лю Цысинь")
                .contains("Джордж Мартин");
    }

    /**
     * Обновление книги по id изменяет сохранённые значения в базе данных.
     */
    @Test
    void updateBookTest() {
        Book book = bookDAO.getBookById(1);
        assertThat(book).isNotNull();

        book.setBookName("Updated name");
        book.setAuthor("Updated author");
        bookDAO.updateBook(book);

        Book updatedBook = bookDAO.getBookById(1);
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getBookName()).isEqualTo("Updated name");
        assertThat(updatedBook.getAuthor()).isEqualTo("Updated author");
    }

    /**
     * Сохранение новой книги присваивает id и позволяет прочитать книгу из базы данных.
     */
    @Test
    void saveBookTest() throws Exception {
        Book newBook = new Book();
        newBook.setBookName("New book");
        newBook.setAuthor("New author");

        Book saved = bookDAO.saveBook(newBook);
        assertThat(saved.getId()).isNotNull();

        Book found = bookDAO.getBookById(saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getBookName()).isEqualTo("New book");
        assertThat(found.getAuthor()).isEqualTo("New author");
    }

    /**
     * Удаление существующей книги возвращает 1 и книга перестаёт находиться по id.
     */
    @Test
    void deleteBookByIdTest() throws Exception {
        Book newBook = new Book();
        newBook.setBookName("Book to delete");
        newBook.setAuthor("Author");

        Book saved = bookDAO.saveBook(newBook);

        int deleted = bookDAO.deleteBookById(saved.getId());
        assertThat(deleted).isEqualTo(1);
        assertThat(bookDAO.getBookById(saved.getId())).isNull();
    }

    /**
     * Удаление несуществующей книги возвращает 0.
     */
    @Test
    void deleteBookByIdNotFoundTest() throws Exception {
        int deleted = bookDAO.deleteBookById(99999L);
        assertThat(deleted).isEqualTo(0);
    }
}
