package ru.mrhellko.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mrhellko.library.Entity.Author;
import ru.mrhellko.library.assembler.AuthorService;
import ru.mrhellko.library.exception.NotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    /**
     * Если автор не найден, то эндпоинт /authors/{id} возвращает 404 Not Found.
     */
    @Test
    void getAuthorByIdNotFoundTest() throws Exception {
        when(authorService.getAuthorById(1L)).thenReturn(null);

        mockMvc.perform(get("/authors/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Если автор найден, то эндпоинт /authors/{id} возвращает 200 OK и JSON с автором.
     */
    @Test
    void getAuthorByIdOkTest() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setAuthorName("name");

        when(authorService.getAuthorById(1L)).thenReturn(author);

        mockMvc.perform(get("/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.authorName").value("name"));
    }

    /**
     * Если автор для обновления не найден, то эндпоинт PUT /authors/{id} возвращает 404 Not Found.
     */
    @Test
    void updateAuthorNotFoundTest() throws Exception {
        when(authorService.updateAuthor(any(Author.class), eq(1L))).thenReturn(null);

        Author request = new Author();
        request.setId(1L);
        request.setAuthorName("name");

        mockMvc.perform(put("/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    /**
     * Если автор обновлён успешно, то эндпоинт PUT /authors/{id} возвращает 200 OK и JSON с автором.
     */
    @Test
    void updateAuthorOkTest() throws Exception {
        Author updated = new Author();
        updated.setId(1L);
        updated.setAuthorName("name");

        when(authorService.updateAuthor(any(Author.class), eq(1L))).thenReturn(updated);

        Author request = new Author();
        request.setId(1L);
        request.setAuthorName("name");

        mockMvc.perform(put("/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.authorName").value("name"));
    }

    /**
     * Если при сохранении автора возникает ошибка, то эндпоинт POST /authors/ возвращает 500 Internal Server Error.
     */
    @Test
    void saveAuthorInternalServerErrorTest() throws Exception {
        when(authorService.saveAuthor(any(Author.class))).thenThrow(new RuntimeException("boom"));

        Author request = new Author();
        request.setId(1L);
        request.setAuthorName("name");

        mockMvc.perform(post("/authors/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Если автор сохранён успешно, то эндпоинт POST /authors/ возвращает 200 OK и JSON с сохранённым автором.
     */
    @Test
    void saveAuthorOkTest() throws Exception {
        Author saved = new Author();
        saved.setId(1L);
        saved.setAuthorName("name");

        when(authorService.saveAuthor(any(Author.class))).thenReturn(saved);

        Author request = new Author();
        request.setId(1L);
        request.setAuthorName("name");

        mockMvc.perform(post("/authors/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.authorName").value("name"));
    }

    /**
     * Если автор для удаления не найден, то эндпоинт DELETE /authors/{id} возвращает 404 Not Found.
     */
    @Test
    void deleteAuthorNotFoundTest() throws Exception {
        doThrow(new NotFoundException(1)).when(authorService).deleteAuthor(1L);

        mockMvc.perform(delete("/authors/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Если при удалении автора возникает ошибка, то эндпоинт DELETE /authors/{id} возвращает 500 Internal Server Error.
     */
    @Test
    void deleteAuthorInternalServerErrorTest() throws Exception {
        doThrow(new RuntimeException("boom")).when(authorService).deleteAuthor(1L);

        mockMvc.perform(delete("/authors/1"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Если автор удалён успешно, то эндпоинт DELETE /authors/{id} возвращает 200 OK.
     */
    @Test
    void deleteAuthorOkTest() throws Exception {
        doNothing().when(authorService).deleteAuthor(1L);

        mockMvc.perform(delete("/authors/1"))
                .andExpect(status().isOk());
    }
}
