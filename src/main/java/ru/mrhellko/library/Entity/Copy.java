package ru.mrhellko.library.Entity;

import lombok.Data;
import ru.mrhellko.library.Enum.Quality;
import ru.mrhellko.library.Enum.StatusCopy;

@Data
public class Copy {
    private String id;
    private Long bookId;

    private Quality quality;
    private StatusCopy status;
    private StorageLocation storageLocation;
}
