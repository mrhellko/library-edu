package ru.mrhellko.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.StorageLocation;

import java.util.*;

@Service
public class StorageLocationDAO {

    private static final String GET_STORAGE_LOCATION_BY_ID_SQL =
            "select s.id, s.building, s.room, s.shelf from storage_locations s where s.id = ?";
    private static final String UPDATE_STORAGE_LOCATION_BY_ID_SQL =
            "update storage_locations set building = ?, room = ?, shelf = ? where id = ?";
    private static final String GET_NEXT_SEQUENCE_ID_SQL = "select nextval('storage_locations_seq') as id";
    private static final String SAVE_STORAGE_LOCATION_SQL =
            "insert into storage_locations (id, building, room, shelf) values (?, ?, ?, ?)";
    private static final String DELETE_STORAGE_LOCATION_BY_ID_SQL = "delete from storage_locations where id = ?";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final RowMapper<StorageLocation> storageLocationRowMapper = (resultSet, _) -> {
        final StorageLocation storageLocation = new StorageLocation();
        storageLocation.setId(resultSet.getLong("id"));
        storageLocation.setBuilding(resultSet.getString("building"));
        storageLocation.setRoom(resultSet.getString("room"));
        storageLocation.setShelf(resultSet.getInt("shelf"));
        return storageLocation;
    };
    private final RowMapper<Long> idRowMapper = (resultSet, _) -> (Long) resultSet.getLong("id");

    public StorageLocation getStorageLocationById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    GET_STORAGE_LOCATION_BY_ID_SQL,
                    storageLocationRowMapper,
                    id
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public void updateStorageLocation(StorageLocation storageLocation) {
        jdbcTemplate.update(UPDATE_STORAGE_LOCATION_BY_ID_SQL,
                storageLocation.getBuilding(),
                storageLocation.getRoom(),
                storageLocation.getShelf(),
                storageLocation.getId());
    }

    public StorageLocation saveStorageLocation(StorageLocation storageLocation) {
        storageLocation.setId(jdbcTemplate.queryForObject(GET_NEXT_SEQUENCE_ID_SQL, idRowMapper));
        jdbcTemplate.update(SAVE_STORAGE_LOCATION_SQL,
                storageLocation.getId(),
                storageLocation.getBuilding(),
                storageLocation.getRoom(),
                storageLocation.getShelf());
        return storageLocation;
    }

    public int deleteStorageLocationById(Long id) {
        return jdbcTemplate.update(DELETE_STORAGE_LOCATION_BY_ID_SQL, id);
    }
}
