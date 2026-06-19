package ru.mrhellko.library.assembler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mrhellko.library.Entity.*;
import ru.mrhellko.library.Enum.Quality;
import ru.mrhellko.library.Enum.StatusCopy;
import ru.mrhellko.library.dao.BookDAO;
import ru.mrhellko.library.dao.CopyDAO;
import ru.mrhellko.library.dao.StorageLocationDAO;
import ru.mrhellko.library.dto.CopyStorageLocationIDDTO;
import ru.mrhellko.library.dto.ErrorLoadedCopyDTO;
import ru.mrhellko.library.dto.ResultLoadedCopyDTO;
import ru.mrhellko.library.dto.SuccessLoadedCopyDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CopyAssemblerTest {
    @Mock
    private CopyDAO copyDAO;

    @Mock
    private BookDAO bookDAO;

    @Mock
    private StorageLocationDAO storageLocationDAO;

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

    /**
     * Если bookId из pathVariable не существует, тогда сохранение не выполняется и возвращается null
     */
    @Test
    void saveCopiesBadBookIdTest() {
        List<CopyStorageLocationIDDTO> input = new ArrayList<>();

        CopyStorageLocationIDDTO a = new CopyStorageLocationIDDTO();
        a.setId("a");
        a.setBookId(1L);
        a.setQuality(Quality.EXCELLENT);
        a.setStatus(StatusCopy.ISSUED);
        a.setStorageLocationId(1L);
        input.add(a);


        when(bookDAO.getBookById(1L)).thenReturn(null);
        assertThat(copyAssembler.saveCopies(input, 1L)).isNull();
    }

    /**
     * Сохранение копий корректно валидируется, затем делегируется в DAO и возвращает результат сохранения.
     */
    @Test
    void saveCopiesTest() {
        List<CopyStorageLocationIDDTO> input = new ArrayList<>();

        //Подходит
        CopyStorageLocationIDDTO a = new CopyStorageLocationIDDTO();
        a.setId("a");
        a.setBookId(1L);
        a.setQuality(Quality.EXCELLENT);
        a.setStatus(StatusCopy.ISSUED);
        a.setStorageLocationId(1L);

        //Не походит bookId
        CopyStorageLocationIDDTO b = new CopyStorageLocationIDDTO();
        b.setId("b");
        b.setBookId(2L);
        b.setQuality(Quality.EXCELLENT);
        b.setStatus(StatusCopy.ISSUED);
        b.setStorageLocationId(1L);

        //Не подходит Id
        CopyStorageLocationIDDTO c = new CopyStorageLocationIDDTO();
        c.setId("c");
        c.setBookId(1L);
        c.setQuality(Quality.EXCELLENT);
        c.setStatus(StatusCopy.ISSUED);
        c.setStorageLocationId(1L);

        //Не подходит, так как в запросе есть подходящий с id "a"
        CopyStorageLocationIDDTO d = new CopyStorageLocationIDDTO();
        d.setId("a");
        d.setBookId(1L);
        d.setQuality(Quality.SATISFACTORY);
        d.setStatus(StatusCopy.ISSUED);
        d.setStorageLocationId(1L);

        //Подходит
        CopyStorageLocationIDDTO e = new CopyStorageLocationIDDTO();
        e.setId("b");
        e.setBookId(1L);
        e.setQuality(Quality.EXCELLENT);
        e.setStatus(StatusCopy.ISSUED);
        e.setStorageLocationId(1L);

        //Не подходит StorageLocationId
        CopyStorageLocationIDDTO f = new CopyStorageLocationIDDTO();
        f.setId("f");
        f.setBookId(1L);
        f.setQuality(Quality.EXCELLENT);
        f.setStatus(StatusCopy.ISSUED);
        f.setStorageLocationId(2L);

        input.add(a);
        input.add(b);
        input.add(c);
        input.add(d);
        input.add(e);
        input.add(f);

        List<CopyStorageLocationIDDTO> saved = new ArrayList<>();
        saved.add(a);
        saved.add(e);

        ResultLoadedCopyDTO excepted = new ResultLoadedCopyDTO();
        excepted.getSuccessLoadedCopyDTOS().add(new SuccessLoadedCopyDTO("a"));
        excepted.getSuccessLoadedCopyDTOS().add(new SuccessLoadedCopyDTO("b"));
        excepted.getErrorLoadedCopyDTOS().add(
                new ErrorLoadedCopyDTO("c", "Copy with id c already exist."));
        excepted.getErrorLoadedCopyDTOS().add(
                new ErrorLoadedCopyDTO("b", "Copy with id b has another bookId.\n"));
        excepted.getErrorLoadedCopyDTOS().add(
                new ErrorLoadedCopyDTO("f", "Storage location with id 2 doesn't exist.\n"));
        excepted.getErrorLoadedCopyDTOS().add(
                new ErrorLoadedCopyDTO("a", "Copy with id a already exist in this request."));

        when(copyDAO.saveCopies(input)).thenReturn(saved);
        when(bookDAO.getBookById(1L)).thenReturn(new Book());
        when(copyDAO.getExistIds(Set.of("a", "b", "c", "f"))).thenReturn(List.of("c"));
        when(storageLocationDAO.getStorageLocationById(1L)).thenReturn(new StorageLocation());
        when(storageLocationDAO.getStorageLocationById(2L)).thenReturn(null);

        ResultLoadedCopyDTO result = copyAssembler.saveCopies(input, 1L);
        assertThat(result).isEqualTo(excepted);
        verify(copyDAO).saveCopies(List.of(a, e));
    }
}
