package ru.mrhellko.library.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mrhellko.library.Entity.*;
import ru.mrhellko.library.Enum.Quality;
import ru.mrhellko.library.Enum.StatusCopy;
import ru.mrhellko.library.assembler.CopyAssembler;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CopyController.class)
public class CopyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CopyAssembler copyAssembler;

    /**
     * Если список копий пуст, то эндпоинт /books/{bookId}/copy возвращает 204 No Content.
     */
    @Test
    void getCopiesByBookIdNoContentTest() throws Exception {
        when(copyAssembler.getCopiesByBookId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/books/1/copy"))
                .andExpect(status().isNoContent());
    }

    /**
     * Если список книг не пуст, нет других ошибок, то эндпоинт /books/{bookId}/copy возвращает 200 OK и JSON со списком.
     */
    @Test
    void getCopiesByBookIdOkTest() throws Exception {
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

        when(copyAssembler.getCopiesByBookId(1L)).thenReturn(List.of(copy));

        mockMvc.perform(get("/books/1/copy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1A"))
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].quality").value("EXCELLENT"))
                .andExpect(jsonPath("$[0].status").value("ISSUED"))
                .andExpect(jsonPath("$[0].storageLocation.id").value(1))
                .andExpect(jsonPath("$[0].storageLocation.building").value("b"))
                .andExpect(jsonPath("$[0].storageLocation.room").value("r"))
                .andExpect(jsonPath("$[0].storageLocation.shelf").value(1));
    }
}
