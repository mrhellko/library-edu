package ru.mrhellko.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Copy;
import ru.mrhellko.library.Entity.StorageLocation;
import ru.mrhellko.library.Enum.Quality;
import ru.mrhellko.library.Enum.StatusCopy;
import ru.mrhellko.library.dto.CopyStorageLocationIDDTO;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CopyDAO {
    private static final String GET_COPIES_BY_BOOK_ID_SQL = """
            select c.id, c.book_id, c.quality, c.status, c.storage_location_id, sl.building, sl.room, sl.shelf
            from copies c join storage_locations sl on c.storage_location_id = sl.id
            where book_id = ?
            order by quality
            """;
    private static final String GET_EXIST_IDS_SQL = "select c.id from copies c where id IN (:copyIds)";
    private static final String SAVE_COPIES_SQL =
            "insert into copies (id, book_id, quality, status, storage_location_id) values (?, ?, ?, ?, ?)";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
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
    private final RowMapper<String> idRowMapper = (resultSet, _) ->
            (String) resultSet.getString("id");

    public List<Copy> getCopiesByBookId(Long bookId) {
        return jdbcTemplate.query(GET_COPIES_BY_BOOK_ID_SQL, copyRowMapper, bookId);
    }

    public List<String> getExistIds(Set<String> copyIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("copyIds", copyIds);
        return namedParameterJdbcTemplate.query(GET_EXIST_IDS_SQL, params, idRowMapper);
    }

    public List<CopyStorageLocationIDDTO> saveCopies(List<CopyStorageLocationIDDTO> copies) {
        jdbcTemplate.batchUpdate(SAVE_COPIES_SQL, copies, copies.size(), (PreparedStatement ps, CopyStorageLocationIDDTO copy) -> {
            ps.setString(1, copy.getId());
            ps.setLong(2, copy.getBookId());
            ps.setByte(3, (byte) copy.getQuality().getValue());
            ps.setByte(4, (byte) copy.getStatus().getValue());
            ps.setLong(5, copy.getStorageLocationId());
        });
        return copies;
    }
}
