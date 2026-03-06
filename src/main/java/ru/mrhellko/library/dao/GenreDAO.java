package ru.mrhellko.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Genre;
import ru.mrhellko.library.dto.BookGenreDTO;

import java.util.*;

@Service
public class GenreDAO {
    private static final String GET_ALL_GENRES_SQL = "select g.id, g.genre_name from genres g";
    private static final String GET_GENRE_BY_ID_SQL = "select g.id, g.genre_name from genres g where g.id = ?";
    private static final String UPDATE_GENRE_BY_ID_SQL = "update genres set genre_name = ? where id = ?";
    private static final String GET_NEXT_SEQUENCE_ID_SQL = "select nextval('genres_seq') as id";
    private static final String SAVE_GENRE_SQL = "insert into genres (id, genre_name) values (?, ?)";
    private static final String DELETE_GENRE_BY_ID_SQL = "delete from genres where id = ?";
    private static final String GET_GENRES_FOR_BOOKS_SQL = """
            select b.id as book_id, g.id as genre_id, g.genre_name
                        from genres g
                            left join book_genres bg
                                on g.id = bg.genre_id
                            left join books b
                                on bg.book_id = b.id
                        where b.id IN (:bookIds)""";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RowMapper<Genre> genreRowMapper = (resultSet, _) -> {
        final Genre genre = new Genre();
        genre.setId(resultSet.getLong("id"));
        genre.setGenreName(resultSet.getString("genre_name"));
        return genre;
    };
    private final RowMapper<BookGenreDTO> bookGenreDTORowMapper = (resultSet, _) -> {
        final BookGenreDTO bookGenreDTO = new BookGenreDTO();
        bookGenreDTO.setBookId(resultSet.getLong("book_id"));
        bookGenreDTO.setGenreId(resultSet.getLong("genre_id"));
        bookGenreDTO.setGenreName(resultSet.getString("genre_name"));
        return bookGenreDTO;
    };
    private final RowMapper<Long> idRowMapper = (resultSet, _) -> (Long) resultSet.getLong("id");

    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(GET_ALL_GENRES_SQL, genreRowMapper);
    }

    public Genre getGenreById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    GET_GENRE_BY_ID_SQL,
                    genreRowMapper,
                    id
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public void updateGenre(Genre genre) {
        jdbcTemplate.update(UPDATE_GENRE_BY_ID_SQL, genre.getGenreName(), genre.getId());
    }

    public Genre saveGenre(Genre genre) {
        genre.setId(jdbcTemplate.queryForObject(GET_NEXT_SEQUENCE_ID_SQL, idRowMapper));
        jdbcTemplate.update(SAVE_GENRE_SQL, genre.getId(), genre.getGenreName());
        return genre;
    }

    public int deleteGenreById(Long id) {
        return jdbcTemplate.update(DELETE_GENRE_BY_ID_SQL, id);
    }

    public List<BookGenreDTO> getGenresForBooks(Set<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("bookIds", bookIds);
        return namedParameterJdbcTemplate.query(GET_GENRES_FOR_BOOKS_SQL, params, bookGenreDTORowMapper);
    }
}
