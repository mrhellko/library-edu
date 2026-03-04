package ru.mrhellko.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.mrhellko.library.Entity.Author;
import ru.mrhellko.library.Entity.Book;
import ru.mrhellko.library.Entity.Genre;
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
        Author a1 = new Author(1L, "a1");
        Author a2 = new Author(2L, "a2");
        book.setAuthors(List.of(a1, a2));
        Genre g1 = new Genre(1L, "g1");
        Genre g2 = new Genre(2L, "g2");
        book.setGenres(List.of(g1, g2));

        BookWithAverageRatingDTO dto = new BookWithAverageRatingDTO(book);
        dto.setAverageRating(7.0f);

        when(bookAssembler.getFullAllBooks()).thenReturn(List.of(dto));

        mockMvc.perform(get("/books/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookName").value("b"))
                .andExpect(jsonPath("$[0].authors[0].id").value(1))
                .andExpect(jsonPath("$[0].authors[0].authorName").value("a1"))
                .andExpect(jsonPath("$[0].authors[1].id").value(2))
                .andExpect(jsonPath("$[0].authors[1].authorName").value("a2"))
                .andExpect(jsonPath("$[0].genres[0].id").value(1))
                .andExpect(jsonPath("$[0].genres[0].genreName").value("g1"))
                .andExpect(jsonPath("$[0].genres[1].id").value(2))
                .andExpect(jsonPath("$[0].genres[1].genreName").value("g2"))
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
        Author author = new Author(1L, "a");
        book.setAuthors(List.of(author));
        Genre genre = new Genre(1L, "g");
        book.setGenres(List.of(genre));

        BookWithAverageRatingDTO dto = new BookWithAverageRatingDTO(book);
        dto.setAverageRating(null);

        when(bookAssembler.getFullBookWithAverageRatingDTO(1L)).thenReturn(dto);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookName").value("b"))
                .andExpect(jsonPath("$.authors[0].id").value(1))
                .andExpect(jsonPath("$.authors[0].authorName").value("a"))
                .andExpect(jsonPath("$.genres[0].id").value(1))
                .andExpect(jsonPath("$.genres[0].genreName").value("g"));
    }

    /**
     * Если книга для обновления не найдена, то эндпоинт PUT /books/{id} возвращает 404 Not Found.
     */
    @Test
    void updateBookNotFoundTest() throws Exception {
        when(bookAssembler.updateBook(any(Book.class), eq(1L))).thenReturn(null);

        Book request = new Book();
        request.setBookName("b");
        Author author = new Author(1L, "a");
        request.setAuthors(List.of(author));
        Genre genre = new Genre(1L, "g");
        request.setGenres(List.of(genre));

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    /**
     * Если авторы/жанры в книге не найдены или отсутствуют, то эндпоинт PUT /books/{id} возвращает 400 Bad Request.
     */
    @Test
    void updateBookNotFoundAuthorsTest() throws Exception {
        when(bookAssembler.updateBook(any(Book.class), eq(1L))).thenThrow(new IllegalArgumentException("no authors"));

        Book request = new Book();
        request.setBookName("b");
        Author author = new Author(1L, "a");
        request.setAuthors(List.of(author));
        Genre genre = new Genre(1L, "g");
        request.setGenres(List.of(genre));

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Если книга обновлена успешно, то эндпоинт PUT /books/{id} возвращает 200 OK и JSON с книгой.
     */
    @Test
    void updateBookOkTest() throws Exception {
        Book updated = new Book();
        updated.setId(1L);
        updated.setBookName("b");
        Author author = new Author(1L, "a");
        updated.setAuthors(List.of(author));
        Genre genre = new Genre(1L, "g");
        updated.setGenres(List.of(genre));

        when(bookAssembler.updateBook(any(Book.class), eq(1L))).thenReturn(updated);

        Book request = new Book();
        request.setBookName("b");
        request.setAuthors(List.of(author));
        request.setGenres(List.of(genre));

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookName").value("b"))
                .andExpect(jsonPath("$.authors[0].id").value(1))
                .andExpect(jsonPath("$.authors[0].authorName").value("a"))
                .andExpect(jsonPath("$.genres[0].id").value(1))
                .andExpect(jsonPath("$.genres[0].genreName").value("g"));
    }

    /**
     * Если при сохранении книги возникает IllegalArgumentException ошибка, то эндпоинт POST /books/ возвращает 400 Bad Request.
     */
    @Test
    void saveBookBadRequestErrorTest() throws Exception {
        when(bookAssembler.saveBook(any(Book.class))).thenThrow(new IllegalArgumentException("no authors"));

        Book request = new Book();
        request.setBookName("b");
        Author author = new Author(1L, "a");
        request.setAuthors(List.of(author));
        Genre genre = new Genre(1L, "g");
        request.setGenres(List.of(genre));

        mockMvc.perform(post("/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Если при сохранении книги возникает иная ошибка, то эндпоинт POST /books/ возвращает 500 Internal Server Error.
     */
    @Test
    void saveBookInternalServerErrorTest() throws Exception {
        when(bookAssembler.saveBook(any(Book.class))).thenThrow(new RuntimeException("boom"));

        Book request = new Book();
        request.setBookName("b");
        Author author = new Author(1L, "a");
        request.setAuthors(List.of(author));
        Genre genre = new Genre(1L, "g");
        request.setGenres(List.of(genre));

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
        Author author = new Author(1L, "a");
        saved.setAuthors(List.of(author));
        Genre genre = new Genre(1L, "g");
        saved.setGenres(List.of(genre));

        when(bookAssembler.saveBook(any(Book.class))).thenReturn(saved);

        Book request = new Book();
        request.setBookName("b");
        request.setAuthors(List.of(author));
        request.setGenres(List.of(genre));

        mockMvc.perform(post("/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.bookName").value("b"))
                .andExpect(jsonPath("$.authors[0].id").value(1))
                .andExpect(jsonPath("$.authors[0].authorName").value("a"))
                .andExpect(jsonPath("$.genres[0].id").value(1))
                .andExpect(jsonPath("$.genres[0].genreName").value("g"));
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
        Author author = new Author(1L, "a");
        book.setAuthors(List.of(author));
        Genre genre = new Genre(1L, "g");
        book.setGenres(List.of(genre));

        BookWithAverageRatingDTO dto = new BookWithAverageRatingDTO(book);
        dto.setAverageRating(null);

        when(bookAssembler.getBooksByAuthorName("a")).thenReturn(List.of(dto));

        mockMvc.perform(get("/books").param("authorName", "a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookName").value("b"))
                .andExpect(jsonPath("$[0].authors[0].id").value(1))
                .andExpect(jsonPath("$[0].authors[0].authorName").value("a"))
                .andExpect(jsonPath("$[0].genres[0].id").value(1))
                .andExpect(jsonPath("$[0].genres[0].genreName").value("g"));
    }

    /**
     * Если по authorId книги не найдены, то эндпоинт GET /books/by-author/{authorId} возвращает 204 No Content.
     */
    @Test
    void getBooksByAuthorIdNoContentTest() throws Exception {
        when(bookAssembler.getBooksByAuthorId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/books/by-author/1"))
                .andExpect(status().isNoContent());
    }

    /**
     * Если по authorId книги найдены, то эндпоинт GET /books/by-author/{authorId} возвращает 200 OK и JSON со списком.
     */
    @Test
    void getBooksByAuthorIdOkTest() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setBookName("b");
        Author author = new Author(1L, "a");
        book.setAuthors(List.of(author));
        Genre genre = new Genre(1L, "g");
        book.setGenres(List.of(genre));

        BookWithAverageRatingDTO dto = new BookWithAverageRatingDTO(book);
        dto.setAverageRating(null);

        when(bookAssembler.getBooksByAuthorId(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/books/by-author/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookName").value("b"))
                .andExpect(jsonPath("$[0].authors[0].id").value(1))
                .andExpect(jsonPath("$[0].authors[0].authorName").value("a"))
                .andExpect(jsonPath("$[0].genres[0].id").value(1))
                .andExpect(jsonPath("$[0].genres[0].genreName").value("g"));
    }

    /**
     * Если по genreId книги не найдены, то эндпоинт GET /books/genre/{genreId} возвращает 204 No Content.
     */
    @Test
    void getBooksByGenreIdNoContentTest() throws Exception {
        when(bookAssembler.getBooksByGenreId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/books/genre/1"))
                .andExpect(status().isNoContent());
    }

    /**
     * Если по genreId книги найдены, то эндпоинт GET /books/genre/{genreId} возвращает 200 OK и JSON со списком.
     */
    @Test
    void getBooksByGenreIdOkTest() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setBookName("b");
        Author author = new Author(1L, "a");
        book.setAuthors(List.of(author));
        Genre genre = new Genre(1L, "g");
        book.setGenres(List.of(genre));

        BookWithAverageRatingDTO dto = new BookWithAverageRatingDTO(book);
        dto.setAverageRating(null);

        when(bookAssembler.getBooksByGenreId(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/books/genre/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookName").value("b"))
                .andExpect(jsonPath("$[0].authors[0].id").value(1))
                .andExpect(jsonPath("$[0].authors[0].authorName").value("a"))
                .andExpect(jsonPath("$[0].genres[0].id").value(1))
                .andExpect(jsonPath("$[0].genres[0].genreName").value("g"));
    }
}
