package ru.mrhellko.library.Entity;

import lombok.Data;

@Data
public class StorageLocation {
    private Long id;

    private String building;
    private String room;
    private Integer shelf;
}
