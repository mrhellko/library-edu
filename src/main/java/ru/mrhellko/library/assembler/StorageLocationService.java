package ru.mrhellko.library.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.StorageLocation;
import ru.mrhellko.library.dao.StorageLocationDAO;
import ru.mrhellko.library.exception.NotFoundException;

@Service
public class StorageLocationService {
    @Autowired
    private StorageLocationDAO storageLocationDAO;

    public StorageLocation getStorageLocationById(Long id) {
        return storageLocationDAO.getStorageLocationById(id);
    }

    public StorageLocation updateStorageLocation(StorageLocation storageLocation, Long id) {
        StorageLocation updatedStorageLocation = storageLocationDAO.getStorageLocationById(id);
        if (updatedStorageLocation != null) {
            updatedStorageLocation.setId(id);
            updatedStorageLocation.setBuilding(storageLocation.getBuilding());
            updatedStorageLocation.setRoom(storageLocation.getRoom());
            updatedStorageLocation.setShelf(storageLocation.getShelf());

            storageLocationDAO.updateStorageLocation(updatedStorageLocation);
            return updatedStorageLocation;
        } else {
            return null;
        }
    }

    public StorageLocation saveStorageLocation(StorageLocation storageLocation) {
        return storageLocationDAO.saveStorageLocation(storageLocation);
    }

    public void deleteStorageLocation(Long id) {
        int resultStorageLocation = storageLocationDAO.deleteStorageLocationById(id);
        if (resultStorageLocation == 0) {
            throw new NotFoundException(id);
        }
    }
}
