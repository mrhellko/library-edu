package ru.mrhellko.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorLoadedCopyDTO {
    private String id;
    private String errorMessage;
}
