package ru.mrhellko.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mrhellko.library.Entity.*;
import ru.mrhellko.library.Enum.Quality;
import ru.mrhellko.library.Enum.StatusCopy;
import ru.mrhellko.library.assembler.CopyAssembler;
import ru.mrhellko.library.dto.CopyStorageLocationIDDTO;
import ru.mrhellko.library.dto.ErrorLoadedCopyDTO;
import ru.mrhellko.library.dto.ResultLoadedCopyDTO;
import ru.mrhellko.library.dto.SuccessLoadedCopyDTO;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CopyController.class)
public class CopyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
     * Если список копий не пуст, нет других ошибок, то эндпоинт /books/{bookId}/copy возвращает 200 OK и JSON со списком.
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

    /**
     * Если при сохранении копий возникает HttpMessageNotReadableException ошибка, то эндпоинт POST /books/{bookId}/copy
     * возвращает 400 Bad Request.
     */
    @Test
    void saveCopiesBadRequestErrorTest() throws Exception {
        when(copyAssembler.saveCopies(any(List.class), any(Long.class))).thenThrow(
                new HttpMessageNotReadableException("bad input"));

        CopyStorageLocationIDDTO request = new CopyStorageLocationIDDTO();
        request.setId("c");
        request.setBookId(1L);
        request.setQuality(Quality.EXCELLENT);
        request.setStatus(StatusCopy.ISSUED);
        request.setStorageLocationId(1L);

        mockMvc.perform(post("/books/1/copy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Если при сохранении копий возникает иная ошибка, то эндпоинт POST /books/{bookId}/copy
     * возвращает 500 Internal Server Error.
     */
    @Test
    void saveCopiesInternalServerErrorTest() throws Exception {
        when(copyAssembler.saveCopies(any(List.class), any(Long.class))).thenThrow(new RuntimeException("boom"));

        CopyStorageLocationIDDTO c = new CopyStorageLocationIDDTO();
        c.setId("c");
        c.setBookId(1L);
        c.setQuality(Quality.EXCELLENT);
        c.setStatus(StatusCopy.ISSUED);
        c.setStorageLocationId(1L);

        List<CopyStorageLocationIDDTO> request = List.of(c);

        mockMvc.perform(post("/books/1/copy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Если копия сохранена успешно, то эндпоинт POST /books/{bookId}/copy возвращает 200 OK и JSON с результатами
     * сохранений.
     */
    @Test
    void saveCopiesOkTest() throws Exception {
        ResultLoadedCopyDTO result = new ResultLoadedCopyDTO();
        SuccessLoadedCopyDTO successLoadedCopyDTO = new SuccessLoadedCopyDTO("a");
        ErrorLoadedCopyDTO errorLoadedCopyDTO = new ErrorLoadedCopyDTO("b", "e");
        result.setSuccessLoadedCopyDTOS(List.of(successLoadedCopyDTO));
        result.setErrorLoadedCopyDTOS(List.of(errorLoadedCopyDTO));

        when(copyAssembler.saveCopies(any(List.class), any(Long.class))).thenReturn(result);

        CopyStorageLocationIDDTO c = new CopyStorageLocationIDDTO();
        c.setId("a");
        c.setBookId(1L);
        c.setQuality(Quality.EXCELLENT);
        c.setStatus(StatusCopy.ISSUED);
        c.setStorageLocationId(1L);

        CopyStorageLocationIDDTO e = new CopyStorageLocationIDDTO();
        e.setId("a");
        e.setBookId(1L);
        e.setQuality(Quality.EXCELLENT);
        e.setStatus(StatusCopy.ISSUED);
        e.setStorageLocationId(1L);

        List<CopyStorageLocationIDDTO> request = List.of(c, e);

        mockMvc.perform(post("/books/1/copy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successLoadedCopyDTOS[0].id").value("a"))
                .andExpect(jsonPath("$.errorLoadedCopyDTOS[0].id").value("b"))
                .andExpect(jsonPath("$.errorLoadedCopyDTOS[0].errorMessage").value("e"));
    }
}
