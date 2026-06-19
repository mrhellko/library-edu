package ru.mrhellko.library.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionApiHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        String stackTrace = ExceptionUtils.getStackTrace(e);
        return new ResponseEntity<>(stackTrace, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e) {
        String message = "Ошибка формата данных";
        if (e.getCause() instanceof InvalidFormatException ife) {

            // Получаем имя поля
            String fieldName = ife.getPath().isEmpty() ?
                    "неизвестное поле" :
                    ife.getPath().getFirst().getFieldName();

            // Получаем недопустимое значение
            Object invalidValue = ife.getValue();

            // Определяем тип enum
            Class<?> targetType = ife.getTargetType();

            // Получаем допустимые значения для enum
            String validValues = "";
            if (targetType.isEnum()) {
                Object[] enumConstants = targetType.getEnumConstants();
                validValues = Arrays.stream(enumConstants)
                        .map(ex -> ex.toString() + "(" + ((Enum<?>) ex).ordinal() + ")")
                        .collect(Collectors.joining(", "));
            }

            message = String.format(
                    "Недопустимое значение '%s' для поля '%s'. Допустимые значения: %s",
                    invalidValue,
                    fieldName,
                    validValues
            );
        }

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
