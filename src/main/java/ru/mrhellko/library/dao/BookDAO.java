package ru.mrhellko.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Book;

import java.util.List;

@Service
public class BookDAO {

    private static final String GET_BOOK_BY_ID_SQL = "select b.id, b.book_name from books b where b.id = ?";
    private static final String GET_ALL_BOOKS_SQL = "select b.id, b.book_name from books b";
    private static final String UPDATE_BOOK_BY_ID_SQL = "update books set book_name = ? where id = ?";
    private static final String SAVE_BOOK_SQL = "insert into books (id, book_name) values (?, ?)";
    private static final String SAVE_BOOK_AUTHOR_SQL = "insert into book_authors (book_id, author_id) values (?, ?)";
    private static final String SAVE_BOOK_GENRE_SQL = "insert into book_genres (book_id, genre_id) values (?, ?)";
    private static final String DELETE_BOOK_BY_ID_SQL = "delete from books where id = ?";
    private static final String DELETE_BOOK_AUTHOR_SQL = "delete from book_authors where book_id = ? and author_id = ?";
    private static final String DELETE_BOOK_GENRE_SQL = "delete from book_genres where book_id = ? and genre_id = ?";
    private static final String GET_BOOKS_BY_AUTHOR_NAME_SQL = """
            select distinct b.id, b.book_name from books b
                left join book_authors ba on b.id = ba.book_id
                join authors a on ba.author_id = a.id
                                     where a.author_name ilike '%' || ? || '%'""";
    private static final String GET_NEXT_BOOK_SEQUENCE_ID_SQL = "select nextval('books_seq') as id";
    private static final String GET_BOOKS_BY_AUTHOR_ID_SQL = """
            select b.id, b.book_name from books b
                left join book_authors ba on b.id = ba.book_id
                                     where ba.author_id = ?""";
    private static final String GET_BOOKS_BY_GENRE_ID_SQL = """
            select b.id, b.book_name from books b
                left join book_genres bg on b.id = bg.book_id
                                    where bg.genre_id = ?""";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final RowMapper<Book> bookRowMapper = (resultSet, _) -> {
        final Book book = new Book();
        book.setId(resultSet.getLong("id"));
        book.setBookName(resultSet.getString("book_name"));
        return book;
    };
    private final RowMapper<Long> idRowMapper = (resultSet, _) -> (Long) resultSet.getLong("id");

    public Book getBookById(long id) {
        try {
            return jdbcTemplate.queryForObject(
                    GET_BOOK_BY_ID_SQL,
                    bookRowMapper,
                    id
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public List<Book> getAll() {
        return jdbcTemplate.query(GET_ALL_BOOKS_SQL, bookRowMapper);
    }

    public void updateBook(Book book) {
        jdbcTemplate.update(UPDATE_BOOK_BY_ID_SQL, book.getBookName(), book.getId());
    }

    public Book saveBook(Book book) {
        book.setId(jdbcTemplate.queryForObject(GET_NEXT_BOOK_SEQUENCE_ID_SQL, idRowMapper));
        jdbcTemplate.update(SAVE_BOOK_SQL, book.getId(), book.getBookName());
        return book;
    }

    public int deleteBookById(Long id) {
        return jdbcTemplate.update(DELETE_BOOK_BY_ID_SQL, id);
    }

    public List<Book> getBooksByAuthorName(String authorName) {
        return jdbcTemplate.query(GET_BOOKS_BY_AUTHOR_NAME_SQL, bookRowMapper, authorName);
    }

    public void saveBookAuthor(Long bookId, Long authorId) {
        jdbcTemplate.update(SAVE_BOOK_AUTHOR_SQL, bookId, authorId);
    }

    public int deleteBookAuthor(Long bookId, Long authorId) {
        return jdbcTemplate.update(DELETE_BOOK_AUTHOR_SQL, bookId, authorId);
    }

    public void saveBookGenre(Long bookId, Long genreId) {
        jdbcTemplate.update(SAVE_BOOK_GENRE_SQL, bookId, genreId);
    }

    public int deleteBookGenre(Long bookId, Long genreId) {
        return jdbcTemplate.update(DELETE_BOOK_GENRE_SQL, bookId, genreId);
    }

    public List<Book> getBooksByAuthorId(Long authorId) {
        return jdbcTemplate.query(GET_BOOKS_BY_AUTHOR_ID_SQL, bookRowMapper, authorId);
    }

    public List<Book> getBooksByGenreId(Long genreId) {
        return jdbcTemplate.query(GET_BOOKS_BY_GENRE_ID_SQL, bookRowMapper, genreId);
    }
}
