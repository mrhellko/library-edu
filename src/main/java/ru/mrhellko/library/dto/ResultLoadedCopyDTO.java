package ru.mrhellko.library.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResultLoadedCopyDTO {
    private List<SuccessLoadedCopyDTO> successLoadedCopyDTOS = new ArrayList<>();
    private List<ErrorLoadedCopyDTO> errorLoadedCopyDTOS = new ArrayList<>();
}
