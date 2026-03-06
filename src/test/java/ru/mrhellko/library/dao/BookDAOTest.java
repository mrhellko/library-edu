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
public class BookDAOTest extends AbstractDAOTest {
    @Autowired
    private BookDAO bookDAO;

    /**
     * Если книги по имени автора не нашлись, то возвращается пустой список
     */
    @Test
    void getBooksByAuthorNameNotFoundTest() {
        List<Book> books = bookDAO.getBooksByAuthorName("not existence author");
        assertThat(books).isEmpty();
    }

    /**
     * По запросу "И" возвращает как минимум 5 книг
     */
    @Test
    void getBooksByAuthorNameFoundManyTest() {
        List<Book> books = bookDAO.getBooksByAuthorName("И");
        assertThat(books).hasSize(5);
        assertThat(books)
                .map(Book::getBookName)
                .contains("Задача трех тел")
                .contains("Игра престолов")
                .contains("Благие знамения")
                .contains("Бесконечная земля")
                .contains("Одноэтажная Америка");
    }

    /**
     * По запросу "Joan Rowling" возращает только те книги в которых автор Joan Rowling.
     */
    @Test
    void getBooksByAuthorNameFoundOneTest() {
        List<Book> books = bookDAO.getBooksByAuthorName("Joan Rowling");
        assertThat(books)
                .map(Book::getBookName)
                .allMatch(book -> book.equals("Гарри Поттер"));
    }

    /**
     * Если книга по id автора не нашлась, то возвращается пустой список
     */
    @Test
    void getBooksByAuthorIdNotFoundTest() {
        List<Book> books = bookDAO.getBooksByAuthorId(99999L);
        assertThat(books).isEmpty();
    }

    /**
     * По запросу 4 возвращает как минимум две книги
     */
    @Test
    void getBooksByAuthorIdFoundManyTest() {
        List<Book> books = bookDAO.getBooksByAuthorId(4L);
        assertThat(books)
                .map(Book::getBookName)
                .contains("Благие знамения")
                .contains("Бесконечная земля");
    }

    /**
     * По запросу 1 возращает только те книги в которых автор Joan Rowling.
     */
    @Test
    void getBooksByAuthorIdFoundOneTest() {
        List<Book> books = bookDAO.getBooksByAuthorId(1L);
        assertThat(books)
                .map(Book::getBookName)
                .allMatch(book -> book.equals("Гарри Поттер"));
    }

    /**
     * Если книга по id жанра не нашлась, то возвращается пустой список
     */
    @Test
    void getBooksByGenreIdNotFoundTest() {
        List<Book> books = bookDAO.getBooksByGenreId(99999L);
        assertThat(books).isEmpty();
    }

    /**
     * По запросу 1 возвращает как минимум четыре книги
     */
    @Test
    void getBooksByGenreIdFoundManyTest() {
        List<Book> books = bookDAO.getBooksByGenreId(1L);
        assertThat(books)
                .map(Book::getBookName)
                .contains("Гарри Поттер")
                .contains("Игра престолов")
                .contains("Бесконечная земля");
    }

    /**
     * По запросу 2 возращает только те книги в которых жанр драма.
     */
    @Test
    void getBooksByGenreIdFoundOneTest() {
        List<Book> books = bookDAO.getBooksByGenreId(2L);
        assertThat(books)
                .map(Book::getBookName)
                .allMatch(book -> book.equals("Гарри Поттер"));
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
    }

    /**
     * Получение всех книг возвращает не пустой список с начальными данными.
     */
    @Test
    void getAllTest() {
        List<Book> books = bookDAO.getAll();
        assertThat(books).hasSizeGreaterThanOrEqualTo(6);
        assertThat(books)
                .map(Book::getBookName)
                .contains("Гарри Поттер")
                .contains("Задача трех тел")
                .contains("Игра престолов")
                .contains("Благие знамения")
                .contains("Бесконечная земля")
                .contains("Одноэтажная Америка");
    }

    /**
     * Обновление книги по id изменяет сохранённые значения в базе данных.
     */
    @Test
    void updateBookTest() {
        Book book = bookDAO.getBookById(1);
        assertThat(book).isNotNull();

        book.setBookName("Updated name");
        bookDAO.updateBook(book);

        Book updatedBook = bookDAO.getBookById(1);
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getBookName()).isEqualTo("Updated name");
    }

    /**
     * Сохранение новой книги присваивает id и позволяет прочитать книгу из базы данных.
     */
    @Test
    void saveBookTest() {
        Book newBook = new Book();
        newBook.setBookName("New book");

        Book saved = bookDAO.saveBook(newBook);
        assertThat(saved.getId()).isNotNull();

        Book found = bookDAO.getBookById(saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getBookName()).isEqualTo("New book");
    }

    /**
     * Удаление существующей книги возвращает 1 и книга перестаёт находиться по id.
     */
    @Test
    void deleteBookByIdTest() {
        Book newBook = new Book();
        newBook.setBookName("Book to delete");

        Book saved = bookDAO.saveBook(newBook);

        int deleted = bookDAO.deleteBookById(saved.getId());
        assertThat(deleted).isEqualTo(1);
        assertThat(bookDAO.getBookById(saved.getId())).isNull();
    }

    /**
     * Удаление несуществующей книги возвращает 0.
     */
    @Test
    void deleteBookByIdNotFoundTest() {
        int deleted = bookDAO.deleteBookById(99999L);
        assertThat(deleted).isEqualTo(0);
    }
}
