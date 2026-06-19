package ru.mrhellko.library.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mrhellko.library.Entity.Copy;
import ru.mrhellko.library.Enum.Quality;
import ru.mrhellko.library.Enum.StatusCopy;
import ru.mrhellko.library.dto.CopyStorageLocationIDDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CopyDAOTest extends AbstractDAOTest {
    @Autowired
    private CopyDAO copyDAO;

    /**
     * Если копий по bookId не нашлось, то возвращается пустой список.
     */
    @Test
    void getCopiesByBookIdNotFoundTest() {
        List<Copy> copyList = copyDAO.getCopiesByBookId(99999L);
        assertThat(copyList).isEmpty();
    }

    /**
     * Если копии по bookId нашлись, то они возвращаются отсортированными по quality.
     */
    @Test
    void getCopiesByBookIdManyFoundTest() {
        List<Copy> copies = copyDAO.getCopiesByBookId(2L);
        assertThat(copies).hasSizeGreaterThanOrEqualTo(2);
        assertThat(copies).isSortedAccordingTo(Comparator.comparingInt(o -> o.getQuality().getValue()));
        assertThat(copies)
                .map(Copy::getId)
                .contains("d1")
                .contains("d2");
    }

    /**
     * Если копия по bookId нашлась, то она возвращается заполненая.
     */
    @Test
    void getCopiesByBookIdOneFoundTest() {
        List<Copy> copies = copyDAO.getCopiesByBookId(4L);
        assertThat(copies).hasSize(1);

        Copy copy = copies.getFirst();
        assertThat(copy.getId()).isEqualTo("KFE");
        assertThat(copy.getQuality()).isEqualTo(Quality.EXCELLENT);
        assertThat(copy.getStatus()).isEqualTo(StatusCopy.UNDER_RESTORATION);
        assertThat(copy.getStorageLocation().getId()).isEqualTo(3);
        assertThat(copy.getStorageLocation().getBuilding()).isEqualTo("ул. Великих писателей, д. 1");
        assertThat(copy.getStorageLocation().getRoom()).isEqualTo("VIP-зал");
        assertThat(copy.getStorageLocation().getShelf()).isEqualTo(1);
    }

    /**
     * Если не нашлись существующие копии, то возвращается пустой список.
     */
    @Test
    void getExistIdsNotFoundTest() {
        List<String> copyList = copyDAO.getExistIds(Set.of("99999"));
        assertThat(copyList).isEmpty();
    }

    /**
     * Если существующие копии нашлись, то они возвращаются.
     */
    @Test
    void getExistIdsManyFoundTest() {
        List<String> copies = copyDAO.getExistIds(Set.of("d1", "d2", "99999"));
        assertThat(copies).hasSize(2);
        assertThat(copies)
                .contains("d1")
                .contains("d2");
    }

    /**
     * Сохранение новых копий проходит корректно и позволяет прочитать копии из базы данных.
     */
    @Test
    void saveCopiesTest() {
        List<CopyStorageLocationIDDTO> newList = new ArrayList<>();

        CopyStorageLocationIDDTO a = new CopyStorageLocationIDDTO();
        a.setId("a");
        a.setBookId(6L);
        a.setQuality(Quality.EXCELLENT);
        a.setStatus(StatusCopy.ISSUED);
        a.setStorageLocationId(1L);

        CopyStorageLocationIDDTO b = new CopyStorageLocationIDDTO();
        b.setId("b");
        b.setBookId(6L);
        b.setQuality(Quality.EXCELLENT);
        b.setStatus(StatusCopy.ISSUED);
        b.setStorageLocationId(1L);
        newList.add(a);
        newList.add(b);

        List<CopyStorageLocationIDDTO> saved = copyDAO.saveCopies(newList);
        assertThat(saved).isNotEmpty();

        List<Copy> found = copyDAO.getCopiesByBookId(6L);
        assertThat(found).isNotNull();
        assertThat(found).hasSize(2);
        assertThat(found.get(0).getId()).isEqualTo("a");
        assertThat(found.get(1).getId()).isEqualTo("b");
    }
}
