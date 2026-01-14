package ru.mrhellko.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Book;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class BookDAO {

    private static final String GET_BOOK_BY_ID_SQL = "select * from books b where b.id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Book getBookById(long id) {
        return jdbcTemplate.queryForObject(
                GET_BOOK_BY_ID_SQL,
                new RowMapper<Book>() {
                    @Override
                    public Book mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                        final Book book = new Book();
                        book.setId(resultSet.getLong("id"));
                        book.setBookName(resultSet.getString("book_name"));
                        return book;
                    }
                },
                id
        );
    }
}
