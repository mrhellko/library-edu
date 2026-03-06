package ru.mrhellko.library.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mrhellko.library.Entity.Copy;
import ru.mrhellko.library.Enum.Quality;
import ru.mrhellko.library.Enum.StatusCopy;

import java.util.Comparator;
import java.util.List;

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
}
