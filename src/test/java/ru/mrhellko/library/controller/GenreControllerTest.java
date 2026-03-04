package ru.mrhellko.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mrhellko.library.Entity.Genre;
import ru.mrhellko.library.assembler.GenreService;
import ru.mrhellko.library.exception.NotFoundException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
public class GenreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GenreService genreService;

    /**
     * Если список жанров пуст, то эндпоминт /genres/ возвращает 204 No Content.
     */
    @Test
    void getAllNoContentTest() throws Exception {
        when(genreService.getAllGenres()).thenReturn(List.of());

        mockMvc.perform(get("/genres/"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllOkTest() throws Exception {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setGenreName("g");

        when(genreService.getAllGenres()).thenReturn(List.of(genre));

        mockMvc.perform(get("/genres/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].genreName").value("g"));
    }

    /**
     * Если жанр не найден, то эндпоинт /genres/{id} возвращает 404 Not Found.
     */
    @Test
    void getGenreByIdNotFoundTest() throws Exception {
        when(genreService.getGenreById(1L)).thenReturn(null);

        mockMvc.perform(get("/genres/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Если жанр найден, то эндпоинт /genres/{id} возвращает 200 OK и JSON с жанром.
     */
    @Test
    void getGenreByIdOkTest() throws Exception {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setGenreName("name");

        when(genreService.getGenreById(1L)).thenReturn(genre);

        mockMvc.perform(get("/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.genreName").value("name"));
    }

    /**
     * Если жанр для обновления не найден, то эндпоинт PUT /genres/{id} возвращает 404 Not Found.
     */
    @Test
    void updateGenreNotFoundTest() throws Exception {
        when(genreService.updateGenre(any(Genre.class), eq(1L))).thenReturn(null);

        Genre genre = new Genre();
        genre.setId(1L);
        genre.setGenreName("name");

        mockMvc.perform(put("/genres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genre)))
                .andExpect(status().isNotFound());
    }

    /**
     * Если жанр обновлён успешно, то эндпоинт PUT /genres/{id} возвращает 200 OK и JSON с жанром.
     */
    @Test
    void updateGenreOkTest() throws Exception {
        Genre updated = new Genre();
        updated.setId(1L);
        updated.setGenreName("name");

        when(genreService.updateGenre(any(Genre.class), eq(1L))).thenReturn(updated);

        Genre request = new Genre();
        request.setId(1L);
        request.setGenreName("name");

        mockMvc.perform(put("/genres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.genreName").value("name"));
    }

    /**
     * Если при сохранении жанра возникает ошибка, то эндпоинт POST /genres/ возвращает 500 Internal Server Error.
     */
    @Test
    void saveGenreInternalServerErrorTest() throws Exception {
        when(genreService.saveGenre(any(Genre.class))).thenThrow(new RuntimeException("boom"));

        Genre request = new Genre();
        request.setId(1L);
        request.setGenreName("name");

        mockMvc.perform(post("/genres/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Если жанр сохранён успешно, то эндпоинт POST /genres/ возвращает 200 OK и JSON с сохранённым жанром.
     */
    @Test
    void saveGenreOkTest() throws Exception {
        Genre saved = new Genre();
        saved.setId(1L);
        saved.setGenreName("name");

        when(genreService.saveGenre(any(Genre.class))).thenReturn(saved);

        Genre request = new Genre();
        request.setId(1L);
        request.setGenreName("name");

        mockMvc.perform(post("/genres/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.genreName").value("name"));
    }

    /**
     * Если жанр для удаления не найден, то эндпоинт DELETE /genres/{id} возвращает 404 Not Found.
     */
    @Test
    void deleteGenreNotFoundTest() throws Exception {
        doThrow(new NotFoundException(1)).when(genreService).deleteGenre(1L);

        mockMvc.perform(delete("/genres/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Если при удалении жанра возникает ошибка, то эндпоинт DELETE /genres/{id} возвращает 500 Internal Server Error.
     */
    @Test
    void deleteGenreInternalServerErrorTest() throws Exception {
        doThrow(new RuntimeException("boom")).when(genreService).deleteGenre(1L);

        mockMvc.perform(delete("/genres/1"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Если жанр удалён успешно, то эндпоинт DELETE /genres/{id} возвращает 200 OK.
     */
    @Test
    void deleteGenreOkTest() throws Exception {
        doNothing().when(genreService).deleteGenre(1L);

        mockMvc.perform(delete("/genres/1"))
                .andExpect(status().isOk());
    }
}
