package ru.mrhellko.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mrhellko.library.Entity.StorageLocation;
import ru.mrhellko.library.assembler.StorageLocationService;
import ru.mrhellko.library.exception.NotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StorageLocationController.class)
public class StorageLocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StorageLocationService storageLocationService;

    /**
     * Если место хранения не найдено, то эндпоинт /storage-locations/{id} возвращает 404 Not Found.
     */
    @Test
    void getStorageLocationByIdNotFoundTest() throws Exception {
        when(storageLocationService.getStorageLocationById(1L)).thenReturn(null);

        mockMvc.perform(get("/storage-locations/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Если место хранения найдено, то эндпоинт /storage-locations/{id} возвращает 200 OK и JSON с местом хранения.
     */
    @Test
    void getStorageLocationByIdOkTest() throws Exception {
        StorageLocation storageLocation = new StorageLocation();
        storageLocation.setId(1L);
        storageLocation.setBuilding("building");
        storageLocation.setRoom("room");
        storageLocation.setShelf(1);

        when(storageLocationService.getStorageLocationById(1L)).thenReturn(storageLocation);

        mockMvc.perform(get("/storage-locations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.building").value("building"))
                .andExpect(jsonPath("$.room").value("room"))
                .andExpect(jsonPath("$.shelf").value(1));
    }

    /**
     * Если место хранения для обновления не найдено, то эндпоинт PUT /storage-locations/{id} возвращает 404 Not Found.
     */
    @Test
    void updateStorageLocationNotFoundTest() throws Exception {
        when(storageLocationService.updateStorageLocation(any(StorageLocation.class), eq(1L))).thenReturn(null);

        StorageLocation request = new StorageLocation();
        request.setId(1L);
        request.setBuilding("building");
        request.setRoom("room");
        request.setShelf(1);

        mockMvc.perform(put("/storage-locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    /**
     * Если место хранения обновлёно успешно, то эндпоинт PUT /storage-locations/{id} возвращает 200 OK и JSON с местом хранения.
     */
    @Test
    void updateStorageLocationOkTest() throws Exception {
        StorageLocation updated = new StorageLocation();
        updated.setId(1L);
        updated.setBuilding("building");
        updated.setRoom("room");
        updated.setShelf(1);

        when(storageLocationService.updateStorageLocation(any(StorageLocation.class), eq(1L))).thenReturn(updated);

        StorageLocation request = new StorageLocation();
        request.setId(1L);
        request.setBuilding("building");
        request.setRoom("room");
        request.setShelf(1);

        mockMvc.perform(put("/storage-locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.building").value("building"))
                .andExpect(jsonPath("$.room").value("room"))
                .andExpect(jsonPath("$.shelf").value(1));
    }

    /**
     * Если при сохранении места хранения возникает ошибка, то эндпоинт POST /storage-locations/ возвращает 500 Internal Server Error.
     */
    @Test
    void saveStorageLocationInternalServerErrorTest() throws Exception {
        when(storageLocationService.saveStorageLocation(any(StorageLocation.class))).thenThrow(new RuntimeException("boom"));

        StorageLocation request = new StorageLocation();
        request.setId(1L);
        request.setBuilding("building");
        request.setRoom("room");
        request.setShelf(1);

        mockMvc.perform(post("/storageLocations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Если место хранения сохранёно успешно, то эндпоинт POST /storage-locations/ возвращает 200 OK и JSON с сохранённым местом хранения.
     */
    @Test
    void saveStorageLocationOkTest() throws Exception {
        StorageLocation saved = new StorageLocation();
        saved.setId(1L);
        saved.setBuilding("building");
        saved.setRoom("room");
        saved.setShelf(1);

        when(storageLocationService.saveStorageLocation(any(StorageLocation.class))).thenReturn(saved);

        StorageLocation request = new StorageLocation();
        request.setId(1L);
        request.setBuilding("building");
        request.setRoom("room");
        request.setShelf(1);

        mockMvc.perform(post("/storage-locations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.building").value("building"))
                .andExpect(jsonPath("$.room").value("room"))
                .andExpect(jsonPath("$.shelf").value(1));
    }

    /**
     * Если место хранения для удаления не найдено, то эндпоинт DELETE /storage-locations/{id} возвращает 404 Not Found.
     */
    @Test
    void deleteStorageLocationNotFoundTest() throws Exception {
        doThrow(new NotFoundException(1)).when(storageLocationService).deleteStorageLocation(1L);

        mockMvc.perform(delete("/storage-locations/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Если при удалении места хранения возникает ошибка, то эндпоинт DELETE /storage-locations/{id} возвращает 500 Internal Server Error.
     */
    @Test
    void deleteStorageLocationInternalServerErrorTest() throws Exception {
        doThrow(new RuntimeException("boom")).when(storageLocationService).deleteStorageLocation(1L);

        mockMvc.perform(delete("/storage-locations/1"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Если место хранения удалёно успешно, то эндпоинт DELETE /storage-locations/{id} возвращает 200 OK.
     */
    @Test
    void deleteStorageLocationOkTest() throws Exception {
        doNothing().when(storageLocationService).deleteStorageLocation(1L);

        mockMvc.perform(delete("/storage-locations/1"))
                .andExpect(status().isOk());
    }
}
