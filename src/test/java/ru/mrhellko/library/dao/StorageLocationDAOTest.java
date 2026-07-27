package ru.mrhellko.library.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mrhellko.library.Entity.StorageLocation;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class StorageLocationDAOTest extends AbstractDAOTest {
    @Autowired
    private StorageLocationDAO storageLocationDAO;

    /**
     * Если место хранения по id не найден, то возвращается null.
     */
    @Test
    void getStorageLocationByIdNotFoundTest() {
        StorageLocation storageLocation = storageLocationDAO.getStorageLocationById(99999L);
        assertThat(storageLocation).isNull();
    }

    /**
     * Если место хранения по id найден, то возвращаются заполненные поля места хранения.
     */
    @Test
    void getStorageLocationByIdFoundTest() {
        StorageLocation storageLocation = storageLocationDAO.getStorageLocationById(1L);
        assertThat(storageLocation).isNotNull();
        assertThat(storageLocation.getId()).isEqualTo(1);
        assertThat(storageLocation.getBuilding()).isNotBlank();
        assertThat(storageLocation.getRoom()).isNotBlank();
        assertThat(storageLocation.getShelf()).isEqualTo(1);
    }

    /**
     * Обновление места хранения по id изменяет сохранённые значения в базе данных.
     */
    @Test
    void updateStorageLocationTest() {
        StorageLocation storageLocation = storageLocationDAO.getStorageLocationById(1L);
        assertThat(storageLocation).isNotNull();

        storageLocation.setBuilding("building");
        storageLocation.setRoom("room");
        storageLocation.setShelf(2);

        storageLocationDAO.updateStorageLocation(storageLocation);

        StorageLocation updated = storageLocationDAO.getStorageLocationById(1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getBuilding()).isEqualTo("building");
        assertThat(updated.getRoom()).isEqualTo("room");
        assertThat(updated.getShelf()).isEqualTo(2);
    }

    /**
     * Сохранение нового места хранения присваивает id и позволяет прочитать место хранения из базы данных.
     */
    @Test
    void saveStorageLocationTest() {
        StorageLocation newStorageLocation = new StorageLocation();
        newStorageLocation.setBuilding("building");
        newStorageLocation.setRoom("room");
        newStorageLocation.setShelf(1);

        StorageLocation saved = storageLocationDAO.saveStorageLocation(newStorageLocation);
        assertThat(saved.getId()).isNotNull();

        StorageLocation found = storageLocationDAO.getStorageLocationById(saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getBuilding()).isEqualTo("building");
        assertThat(found.getRoom()).isEqualTo("room");
        assertThat(found.getShelf()).isEqualTo(1);
    }

    /**
     * Удаление существующего места хранения возвращает 1 и место хранения перестаёт находиться по id.
     */
    @Test
    void deleteStorageLocationByIdTest() {
        StorageLocation newStorageLocation = new StorageLocation();
        newStorageLocation.setBuilding("name");
        newStorageLocation.setRoom("room");
        newStorageLocation.setShelf(1);

        StorageLocation saved = storageLocationDAO.saveStorageLocation(newStorageLocation);

        int deleted = storageLocationDAO.deleteStorageLocationById(saved.getId());
        assertThat(deleted).isEqualTo(1);
        assertThat(storageLocationDAO.getStorageLocationById(saved.getId())).isNull();
    }

    /**
     * Удаление несуществующего места хранения возвращает 0.
     */
    @Test
    void deleteStorageLocationByIdNotFoundTest() {
        int deleted = storageLocationDAO.deleteStorageLocationById(99999L);
        assertThat(deleted).isEqualTo(0);
    }
}
