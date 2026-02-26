package ru.mrhellko.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.mrhellko.library.Entity.Book;
import ru.mrhellko.library.assembler.BookAssembler;
import ru.mrhellko.library.dto.BookWithAverageRatingDTO;
import ru.mrhellko.library.exception.NotFoundException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookAssembler bookAssembler;

    /**
     * Если список книг пуст, то эндпоинт /books/ возвращает 204 No Content.
     */
    @Test
    void getAllNoContentTest() throws Exception {
        when(bookAssembler.getFullAllBooks()).thenReturn(List.of());

        mockMvc.perform(get("/books/"))
                .andExpect(status().isNoContent());
    }

    /**
     * Если список книг не пуст, то эндпоинт /books/ возвращает 200 OK и JSON со списком.
     */
    @Test
    void getAllOkTest() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setBookName("b");
        book.setAuthor("a");

        BookWithAverageRatingDTO dto = new BookWithAverageRatingDTO(book);
        dto.setAverageRating(7.0f);

        when(bookAssembler.getFullAllBooks()).thenReturn(List.of(dto));

        mockMvc.perform(get("/books/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookName").value("b"))
                .andExpect(jsonPath("$[0].author").value("a"))
                .andExpect(jsonPath("$[0].averageRating").value(7.0));
    }

    /**
     * Если книга по id не найдена, то эндпоинт /books/{id} возвращает 404 Not Found.
     */
    @Test
    void getBookByIdNotFoundTest() throws Exception {
        when(bookAssembler.getFullBookWithAverageRatingDTO(1L)).thenReturn(null);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Если книга по id найдена, то эндпоинт /books/{id} возвращает 200 OK и JSON с книгой.
     */
    @Test
    void getBookByIdOkTest() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setBookName("b");
        book.setAuthor("a");

        BookWithAverageRatingDTO dto = new BookWithAverageRatingDTO(book);
        dto.setAverageRating(null);

        when(bookAssembler.getFullBookWithAverageRatingDTO(1L)).thenReturn(dto);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookName").value("b"))
                .andExpect(jsonPath("$.author").value("a"));
    }

    /**
     * Если книга для обновления не найдена, то эндпоинт PUT /books/{id} возвращает 404 Not Found.
     */
    @Test
    void updateBookNotFoundTest() throws Exception {
        when(bookAssembler.updateBook(any(Book.class), eq(1L))).thenReturn(null);

        Book request = new Book();
        request.setBookName("b");
        request.setAuthor("a");

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    /**
     * Если книга обновлена успешно, то эндпоинт PUT /books/{id} возвращает 200 OK и JSON с книгой.
     */
    @Test
    void updateBookOkTest() throws Exception {
        Book updated = new Book();
        updated.setId(1L);
        updated.setBookName("b");
        updated.setAuthor("a");

        when(bookAssembler.updateBook(any(Book.class), eq(1L))).thenReturn(updated);

        Book request = new Book();
        request.setBookName("b");
        request.setAuthor("a");

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookName").value("b"))
                .andExpect(jsonPath("$.author").value("a"));
    }

    /**
     * Если при сохранении книги возникает ошибка, то эндпоинт POST /books/ возвращает 500 Internal Server Error.
     */
    @Test
    void saveBookInternalServerErrorTest() throws Exception {
        when(bookAssembler.saveBook(any(Book.class))).thenThrow(new RuntimeException("boom"));

        Book request = new Book();
        request.setBookName("b");
        request.setAuthor("a");

        mockMvc.perform(post("/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Если книга сохранена успешно, то эндпоинт POST /books/ возвращает 200 OK и JSON с сохранённой книгой.
     */
    @Test
    void saveBookOkTest() throws Exception {
        Book saved = new Book();
        saved.setId(10L);
        saved.setBookName("b");
        saved.setAuthor("a");

        when(bookAssembler.saveBook(any(Book.class))).thenReturn(saved);

        Book request = new Book();
        request.setBookName("b");
        request.setAuthor("a");

        mockMvc.perform(post("/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.bookName").value("b"))
                .andExpect(jsonPath("$.author").value("a"));
    }

    /**
     * Если книга для удаления не найдена, то эндпоинт DELETE /books/{id} возвращает 404 Not Found.
     */
    @Test
    void deleteBookNotFoundTest() throws Exception {
        doThrow(new NotFoundException(1)).when(bookAssembler).deleteBook(1L);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Если при удалении книги возникает ошибка, то эндпоинт DELETE /books/{id} возвращает 500 Internal Server Error.
     */
    @Test
    void deleteBookInternalServerErrorTest() throws Exception {
        doThrow(new RuntimeException("boom")).when(bookAssembler).deleteBook(1L);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Если книга удалена успешно, то эндпоинт DELETE /books/{id} возвращает 200 OK.
     */
    @Test
    void deleteBookOkTest() throws Exception {
        doNothing().when(bookAssembler).deleteBook(1L);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isOk());
    }

    /**
     * Если по authorName книги не найдены, то эндпоинт GET /books?authorName=... возвращает 204 No Content.
     */
    @Test
    void getBooksByAuthorNameNoContentTest() throws Exception {
        when(bookAssembler.getBooksByAuthorName("a")).thenReturn(List.of());

        mockMvc.perform(get("/books").param("authorName", "a"))
                .andExpect(status().isNoContent());
    }

    /**
     * Если по authorName книги найдены, то эндпоинт GET /books?authorName=... возвращает 200 OK и JSON со списком.
     */
    @Test
    void getBooksByAuthorNameOkTest() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setBookName("b");
        book.setAuthor("a");

        BookWithAverageRatingDTO dto = new BookWithAverageRatingDTO(book);
        dto.setAverageRating(null);

        when(bookAssembler.getBooksByAuthorName("a")).thenReturn(List.of(dto));

        mockMvc.perform(get("/books").param("authorName", "a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookName").value("b"))
                .andExpect(jsonPath("$[0].author").value("a"));
    }
}
