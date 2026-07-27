package ru.mrhellko.library.assembler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mrhellko.library.Entity.StorageLocation;
import ru.mrhellko.library.dao.StorageLocationDAO;
import ru.mrhellko.library.exception.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StorageLocationServiceTest {
    @Mock
    private StorageLocationDAO storageLocationDAO;

    @InjectMocks
    private StorageLocationService storageLocationService;

    /**
     * Если по id нет места хранения, то возвращается null.
     */
    @Test
    void getStorageLocationByIdNotFoundTest() {
        when(storageLocationDAO.getStorageLocationById(1L)).thenReturn(null);

        StorageLocation storageLocation = storageLocationService.getStorageLocationById(1L);
        assertThat(storageLocation).isNull();
    }

    /**
     * Если по id есть место хранения, то возвращается найденное место хранения.
     */
    @Test
    void getStorageLocationByIdFoundTest() {
        StorageLocation storageLocation = new StorageLocation();
        storageLocation.setId(1L);
        storageLocation.setBuilding("building");
        storageLocation.setRoom("room");
        storageLocation.setShelf(1);

        when(storageLocationDAO.getStorageLocationById(1L)).thenReturn(storageLocation);

        StorageLocation foundStorageLocation = storageLocationService.getStorageLocationById(1L);
        assertThat(foundStorageLocation.getId()).isEqualTo(1);
        assertThat(foundStorageLocation.getBuilding()).isEqualTo("building");
        assertThat(foundStorageLocation.getRoom()).isEqualTo("room");
        assertThat(foundStorageLocation.getShelf()).isEqualTo(1);
    }

    /**
     * Если место хранения для обновления не найдено, то возвращается null и update не вызывается.
     */
    @Test
    void updateStorageLocationNotFoundTest() {
        when(storageLocationDAO.getStorageLocationById(1L)).thenReturn(null);

        StorageLocation input = new StorageLocation();
        input.setId(1L);

        StorageLocation updated = storageLocationService.updateStorageLocation(input, 1L);
        assertThat(updated).isNull();

        verify(storageLocationDAO).getStorageLocationById(1L);
        verify(storageLocationDAO, never()).updateStorageLocation(any());
    }

    /**
     * Если место хранения найдено, то поля обновляются и update вызывается.
     */
    @Test
    void updateStorageLocationFoundTest() {
        StorageLocation existing = new StorageLocation();
        existing.setId(1L);

        when(storageLocationDAO.getStorageLocationById(1L)).thenReturn(existing);

        StorageLocation input = new StorageLocation();
        input.setBuilding("building");
        input.setRoom("room");
        input.setShelf(1);

        StorageLocation updated = storageLocationService.updateStorageLocation(input, 1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getBuilding()).isEqualTo("building");
        assertThat(updated.getRoom()).isEqualTo("room");
        assertThat(updated.getShelf()).isEqualTo(1);

        verify(storageLocationDAO).updateStorageLocation(existing);
    }

    /**
     * Сохранение места хранения делегируется в DAO и возвращает результат сохранения.
     */
    @Test
    void saveStorageLocationTest() {
        StorageLocation input = new StorageLocation();
        StorageLocation saved = new StorageLocation();
        saved.setId(10L);

        when(storageLocationDAO.saveStorageLocation(input)).thenReturn(saved);

        StorageLocation result = storageLocationService.saveStorageLocation(input);
        assertThat(result).isSameAs(saved);
    }

    /**
     * Если удаление места хранения в DAO вернуло 0, то выбрасывается NotFoundException.
     */
    @Test
    void deleteStorageLocationByIdNotFoundTest() {
        when(storageLocationDAO.deleteStorageLocationById(1L)).thenReturn(0);

        assertThatThrownBy(() -> storageLocationService.deleteStorageLocation(1L))
                .isInstanceOf(NotFoundException.class);

        verify(storageLocationDAO).deleteStorageLocationById(1L);
    }

    /**
     * Если удаление места хранения прошло успешно, то исключение не выбрасывается.
     */
    @Test
    void deleteStorageLocationByIdOkTest() {
        when(storageLocationDAO.deleteStorageLocationById(1L)).thenReturn(1);

        storageLocationService.deleteStorageLocation(1L);

        verify(storageLocationDAO).deleteStorageLocationById(1L);
    }

}
