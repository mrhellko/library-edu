package ru.mrhellko.library.dto;

import lombok.Data;
import ru.mrhellko.library.Enum.Quality;
import ru.mrhellko.library.Enum.StatusCopy;

@Data
public class CopyStorageLocationIDDTO {
    private String id;
    private Long bookId;

    private Quality quality;
    private StatusCopy status;
    private Long storageLocationId;
}
