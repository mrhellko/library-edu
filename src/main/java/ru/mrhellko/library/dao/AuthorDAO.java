package ru.mrhellko.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Author;
import ru.mrhellko.library.dto.BookAuthorDTO;

import java.util.*;

@Service
public class AuthorDAO {
    private static final String GET_AUTHOR_BY_ID_SQL = "select a.id, a.author_name from authors a where a.id = ?";
    private static final String UPDATE_AUTHOR_BY_ID_SQL = "update authors set author_name = ? where id = ?";
    private static final String GET_NEXT_SEQUENCE_ID_SQL = "select nextval('authors_seq') as id";
    private static final String SAVE_AUTHOR_SQL = "insert into authors (id, author_name) values (?, ?)";
    private static final String DELETE_AUTHOR_BY_ID_SQL = "delete from authors where id = ?";
    private static final String GET_AUTHORS_FOR_BOOKS_SQL = "select b.id as book_id, a.id as author_id, a.author_name\n" +
            "from AUTHORS a\n" +
            "    left join book_authors ba\n" +
            "        on a.id = ba.AUTHOR_ID\n" +
            "    left join books b\n" +
            "        on ba.BOOK_ID = b.id\n" +
            "where b.id IN (:bookIds)";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RowMapper<Author> authorRowMapper = (resultSet, _) -> {
        final Author author = new Author();
        author.setId(resultSet.getLong("id"));
        author.setAuthorName(resultSet.getString("author_name"));
        return author;
    };
    private final RowMapper<BookAuthorDTO> bookAuthorDTORowMapper = (resultSet, _) -> {
        final BookAuthorDTO bookAuthorDTO = new BookAuthorDTO();
        bookAuthorDTO.setBookId(resultSet.getLong("book_id"));
        bookAuthorDTO.setAuthorId(resultSet.getLong("author_id"));
        bookAuthorDTO.setAuthorName(resultSet.getString("author_name"));
        return bookAuthorDTO;
    };
    private final RowMapper<Long> idRowMapper = (resultSet, _) -> (Long) resultSet.getLong("id");

    public Author getAuthorById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    GET_AUTHOR_BY_ID_SQL,
                    authorRowMapper,
                    id
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public void updateAuthor(Author author) {
        jdbcTemplate.update(UPDATE_AUTHOR_BY_ID_SQL, author.getAuthorName(), author.getId());
    }

    public Author saveAuthor(Author author) throws Exception {
        author.setId(jdbcTemplate.queryForObject(GET_NEXT_SEQUENCE_ID_SQL, idRowMapper));
        jdbcTemplate.update(SAVE_AUTHOR_SQL, author.getId(), author.getAuthorName());
        return author;
    }

    public int deleteAuthorById(Long id) {
        return jdbcTemplate.update(DELETE_AUTHOR_BY_ID_SQL, id);
    }

    public List<BookAuthorDTO> getAuthorsForBooks(Set<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("bookIds", bookIds);
        return namedParameterJdbcTemplate.query(GET_AUTHORS_FOR_BOOKS_SQL, params, bookAuthorDTORowMapper);
    }
}
