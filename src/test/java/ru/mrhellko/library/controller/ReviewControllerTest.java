package ru.mrhellko.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.assembler.BookReviewAssembler;
import ru.mrhellko.library.dto.BookReviewByBookIdDTO;
import ru.mrhellko.library.dto.BookReviewByReviewerNameDTO;
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

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookReviewAssembler bookReviewAssembler;

    /**
     * Если по книге нет отзывов, то эндпоинт /reviews/book/{bookId} возвращает 204 No Content.
     */
    @Test
    void getReviewByBookIdNoContentTest() throws Exception {
        when(bookReviewAssembler.getReviewByBookId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/reviews/book/1"))
                .andExpect(status().isNoContent());
    }

    /**
     * Если по книге есть отзывы, то эндпоинт /reviews/book/{bookId} возвращает 200 OK и JSON со списком.
     */
    @Test
    void getReviewByBookIdOkTest() throws Exception {
        BookReview review = new BookReview();
        review.setReviewerName("Anna");
        review.setReviewText("text");
        review.setRating((byte) 8);

        BookReviewByBookIdDTO dto = new BookReviewByBookIdDTO(review);

        when(bookReviewAssembler.getReviewByBookId(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/reviews/book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reviewerName").value("Anna"))
                .andExpect(jsonPath("$[0].reviewText").value("text"))
                .andExpect(jsonPath("$[0].rating").value(8));
    }

    /**
     * Если по reviewerName отзывы не найдены, то эндпоинт /reviews?reviewerName=... возвращает 204 No Content.
     */
    @Test
    void getReviewByReviewerNameNoContentTest() throws Exception {
        when(bookReviewAssembler.getReviewByReviewerName("Sergei")).thenReturn(List.of());

        mockMvc.perform(get("/reviews").param("reviewerName", "Sergei"))
                .andExpect(status().isNoContent());
    }

    /**
     * Если по reviewerName отзывы найдены, то эндпоинт /reviews?reviewerName=... возвращает 200 OK и JSON со списком.
     */
    @Test
    void getReviewByReviewerNameOkTest() throws Exception {
        BookReviewByReviewerNameDTO dto = new BookReviewByReviewerNameDTO();
        dto.setReviewText("text");
        dto.setRating((byte) 8);
        dto.setBookName("b");
        dto.setAuthorName("a");

        when(bookReviewAssembler.getReviewByReviewerName("Sergei")).thenReturn(List.of(dto));

        mockMvc.perform(get("/reviews").param("reviewerName", "Sergei"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reviewText").value("text"))
                .andExpect(jsonPath("$[0].rating").value(8))
                .andExpect(jsonPath("$[0].bookName").value("b"))
                .andExpect(jsonPath("$[0].authorName").value("a"));
    }

    /**
     * Если отзыв для обновления не найден, то эндпоинт PUT /reviews/{id} возвращает 404 Not Found.
     */
    @Test
    void updateBookReviewNotFoundTest() throws Exception {
        when(bookReviewAssembler.updateBookReview(any(BookReview.class), eq(1L))).thenReturn(null);

        BookReview request = new BookReview();
        request.setBookId(1L);
        request.setRating((byte) 5);
        request.setReviewerName("n");
        request.setReviewText("t");

        mockMvc.perform(put("/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    /**
     * Если отзыв обновлён успешно, то эндпоинт PUT /reviews/{id} возвращает 200 OK и JSON с отзывом.
     */
    @Test
    void updateBookReviewOkTest() throws Exception {
        BookReview updated = new BookReview();
        updated.setId(1L);
        updated.setBookId(1L);
        updated.setRating((byte) 5);
        updated.setReviewerName("n");
        updated.setReviewText("t");

        when(bookReviewAssembler.updateBookReview(any(BookReview.class), eq(1L))).thenReturn(updated);

        BookReview request = new BookReview();
        request.setBookId(1L);
        request.setRating((byte) 5);
        request.setReviewerName("n");
        request.setReviewText("t");

        mockMvc.perform(put("/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.reviewerName").value("n"))
                .andExpect(jsonPath("$.reviewText").value("t"));
    }

    /**
     * Если при сохранении отзыва возникает ошибка, то эндпоинт POST /reviews/ возвращает 500 Internal Server Error.
     */
    @Test
    void saveBookReviewInternalServerErrorTest() throws Exception {
        when(bookReviewAssembler.saveBookReview(any(BookReview.class))).thenThrow(new RuntimeException("boom"));

        BookReview request = new BookReview();
        request.setBookId(1L);
        request.setRating((byte) 5);
        request.setReviewerName("n");
        request.setReviewText("t");

        mockMvc.perform(post("/reviews/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Если отзыв сохранён успешно, то эндпоинт POST /reviews/ возвращает 200 OK и JSON с сохранённым отзывом.
     */
    @Test
    void saveBookReviewOkTest() throws Exception {
        BookReview saved = new BookReview();
        saved.setId(10L);
        saved.setBookId(1L);
        saved.setRating((byte) 5);
        saved.setReviewerName("n");
        saved.setReviewText("t");

        when(bookReviewAssembler.saveBookReview(any(BookReview.class))).thenReturn(saved);

        BookReview request = new BookReview();
        request.setBookId(1L);
        request.setRating((byte) 5);
        request.setReviewerName("n");
        request.setReviewText("t");

        mockMvc.perform(post("/reviews/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.reviewerName").value("n"))
                .andExpect(jsonPath("$.reviewText").value("t"));
    }

    /**
     * Если отзыв для удаления не найден, то эндпоинт DELETE /reviews/{id} возвращает 404 Not Found.
     */
    @Test
    void deleteBookReviewNotFoundTest() throws Exception {
        doThrow(new NotFoundException(1)).when(bookReviewAssembler).deleteBookReviewById(1L);

        mockMvc.perform(delete("/reviews/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Если при удалении отзыва возникает ошибка, то эндпоинт DELETE /reviews/{id} возвращает 500 Internal Server Error.
     */
    @Test
    void deleteBookReviewInternalServerErrorTest() throws Exception {
        doThrow(new RuntimeException("boom")).when(bookReviewAssembler).deleteBookReviewById(1L);

        mockMvc.perform(delete("/reviews/1"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Если отзыв удалён успешно, то эндпоинт DELETE /reviews/{id} возвращает 200 OK.
     */
    @Test
    void deleteBookReviewOkTest() throws Exception {
        doNothing().when(bookReviewAssembler).deleteBookReviewById(1L);

        mockMvc.perform(delete("/reviews/1"))
                .andExpect(status().isOk());
    }
}
