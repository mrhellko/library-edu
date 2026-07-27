package ru.mrhellko.library.assembler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mrhellko.library.Entity.Copy;
import ru.mrhellko.library.Entity.StorageLocation;
import ru.mrhellko.library.Enum.Quality;
import ru.mrhellko.library.Enum.StatusCopy;
import ru.mrhellko.library.dao.CopyDAO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CopyAssemblerTest {
    @Mock
    private CopyDAO copyDAO;

    @InjectMocks
    private CopyAssembler copyAssembler;

    /**
     * Если по bookId нет копии, то возвращается пустой список.
     */
    @Test
    void getCopiesByBookIdNotFoundTest() {
        when(copyDAO.getCopiesByBookId(1L)).thenReturn(List.of());

        List<Copy> copies = copyAssembler.getCopiesByBookId(1L);
        assertThat(copies).isEmpty();
    }

    /**
     * Если по bookId есть копии, то возвращается заполненный список.
     */
    @Test
    void getCopiesByBookIdFoundTest() {
        Copy copy = new Copy();
        copy.setId("1A");
        copy.setBookId(1L);
        copy.setQuality(Quality.EXCELLENT);
        copy.setStatus(StatusCopy.ISSUED);
        StorageLocation storageLocation = new StorageLocation();
        storageLocation.setId(1L);
        storageLocation.setBuilding("b");
        storageLocation.setRoom("r");
        storageLocation.setShelf(1);
        copy.setStorageLocation(storageLocation);

        when(copyDAO.getCopiesByBookId(1L)).thenReturn(List.of(copy));

        List<Copy> copies = copyAssembler.getCopiesByBookId(1L);
        assertThat(copies).hasSize(1);
        assertThat(copies.getFirst().getId()).isEqualTo("1A");
    }
}
