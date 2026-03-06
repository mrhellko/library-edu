package ru.mrhellko.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrhellko.library.Entity.StorageLocation;
import ru.mrhellko.library.assembler.StorageLocationService;
import ru.mrhellko.library.exception.NotFoundException;

@RestController
@RequestMapping("/storage-locations")
public class StorageLocationController {
    @Autowired
    private StorageLocationService storageLocationService;

    @GetMapping("/{id}")
    public ResponseEntity<StorageLocation> getStorageLocationById(@PathVariable Long id) {
        StorageLocation storageLocation = storageLocationService.getStorageLocationById(id);
        if (storageLocation != null) {
            return new ResponseEntity<>(storageLocation, HttpStatus.OK);
        } else {
            throw new NotFoundException(id);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StorageLocation> updateStorageLocation(@RequestBody StorageLocation storageLocation, @PathVariable Long id) {
        StorageLocation updatedStorageLocation = storageLocationService.updateStorageLocation(storageLocation, id);
        if (updatedStorageLocation != null) {
            return new ResponseEntity<>(updatedStorageLocation, HttpStatus.OK);
        } else {
            throw new NotFoundException(id);
        }
    }

    @PostMapping("/")
    public ResponseEntity<StorageLocation> saveStorageLocation(@RequestBody StorageLocation storageLocation) {
        StorageLocation savedStorageLocation = storageLocationService.saveStorageLocation(storageLocation);
        return new ResponseEntity<>(savedStorageLocation, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStorageLocationById(@PathVariable Long id) {
        storageLocationService.deleteStorageLocation(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
