package ru.mrhellko.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Copy;
import ru.mrhellko.library.Entity.StorageLocation;
import ru.mrhellko.library.Enum.Quality;
import ru.mrhellko.library.Enum.StatusCopy;

import java.util.List;

@Service
public class CopyDAO {
    private static final String GET_COPIES_BY_BOOK_ID_SQL = """
            select c.id, c.book_id, c.quality, c.status, c.storage_location_id, sl.building, sl.room, sl.shelf
            from copies c join storage_locations sl on c.storage_location_id = sl.id
            where book_id = ?
            order by quality
            """;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final RowMapper<Copy> copyRowMapper = (resultSet, _) -> {
        final Copy copy = new Copy();
        copy.setId(resultSet.getString("id"));
        copy.setBookId(resultSet.getLong("book_id"));
        copy.setQuality(Quality.parse(resultSet.getInt("quality")));
        copy.setStatus(StatusCopy.parse(resultSet.getInt("status")));
        final StorageLocation storageLocation = new StorageLocation();
        storageLocation.setId(resultSet.getLong("storage_location_id"));
        storageLocation.setBuilding(resultSet.getString("building"));
        storageLocation.setRoom(resultSet.getString("room"));
        storageLocation.setShelf(resultSet.getInt("shelf"));
        copy.setStorageLocation(storageLocation);
        return copy;
    };

    public List<Copy> getCopiesByBookId(Long bookId) {
        return jdbcTemplate.query(GET_COPIES_BY_BOOK_ID_SQL, copyRowMapper, bookId);
    }
}
