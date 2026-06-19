package ru.mrhellko.library.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Copy;
import ru.mrhellko.library.dao.BookDAO;
import ru.mrhellko.library.dao.CopyDAO;
import ru.mrhellko.library.dao.StorageLocationDAO;
import ru.mrhellko.library.dto.CopyStorageLocationIDDTO;
import ru.mrhellko.library.dto.ErrorLoadedCopyDTO;
import ru.mrhellko.library.dto.ResultLoadedCopyDTO;
import ru.mrhellko.library.dto.SuccessLoadedCopyDTO;

import java.util.*;

@Service
public class CopyAssembler {
    @Autowired
    private CopyDAO copyDAO;
    @Autowired
    private BookDAO bookDAO;
    @Autowired
    private StorageLocationDAO storageLocationDAO;

    public List<Copy> getCopiesByBookId(Long bookId) {
        return copyDAO.getCopiesByBookId(bookId);
    }

    public ResultLoadedCopyDTO saveCopies(List<CopyStorageLocationIDDTO> copies, Long bookId) {
        if (bookDAO.getBookById(bookId) == null) {
            return null;
        }

        ResultLoadedCopyDTO resultLoadedCopyDTO = new ResultLoadedCopyDTO();
        areCopiesExist(copies, resultLoadedCopyDTO);
        validateCopies(copies, resultLoadedCopyDTO, bookId);

        List<CopyStorageLocationIDDTO> savedCopies = copyDAO.saveCopies(copies);
        for (CopyStorageLocationIDDTO copy : savedCopies) {
            resultLoadedCopyDTO.getSuccessLoadedCopyDTOS().add(new SuccessLoadedCopyDTO(copy.getId()));
        }

        return resultLoadedCopyDTO;
    }

    private void areCopiesExist(List<CopyStorageLocationIDDTO> copies, ResultLoadedCopyDTO resultLoadedCopyDTO) {
        Set<String> ids = new HashSet<>();
        for (CopyStorageLocationIDDTO copy : copies) {
            ids.add(copy.getId());
        }
        List<String> existIds = copyDAO.getExistIds(ids);
        for (String id : existIds) {
            resultLoadedCopyDTO.getErrorLoadedCopyDTOS().add(
                    new ErrorLoadedCopyDTO(id, "Copy with id " + id + " already exist."));
            copies.removeIf(copy -> copy.getId().equals(id));
        }
    }

    private void validateCopies(List<CopyStorageLocationIDDTO> copies, ResultLoadedCopyDTO resultLoadedCopyDTO, Long bookId) {
        Iterator<CopyStorageLocationIDDTO> iterator = copies.iterator();
        while (iterator.hasNext()) {
            CopyStorageLocationIDDTO copy = iterator.next();
            boolean isRemove = false;
            String errorMessage = "";

            if (!copy.getBookId().equals(bookId)) {
                isRemove = true;
                errorMessage += "Copy with id " + copy.getId() + " has another bookId.\n";
            }

            if (storageLocationDAO.getStorageLocationById(copy.getStorageLocationId()) == null) {
                isRemove = true;
                errorMessage += "Storage location with id " + copy.getStorageLocationId() + " doesn't exist.\n";
            }

            if (isRemove) {
                resultLoadedCopyDTO.getErrorLoadedCopyDTOS().add(
                        new ErrorLoadedCopyDTO(copy.getId(), errorMessage));
                iterator.remove();
            }
        }
        checkDuplicates(copies, resultLoadedCopyDTO);
    }

    private void checkDuplicates(List<CopyStorageLocationIDDTO> copies,
                                 ResultLoadedCopyDTO resultLoadedCopyDTO) {
        Set<String> uniqueIds = new HashSet<>();
        Iterator<CopyStorageLocationIDDTO> iterator = copies.iterator();

        while (iterator.hasNext()) {
            CopyStorageLocationIDDTO copy = iterator.next();

            if (!uniqueIds.add(copy.getId())) {
                resultLoadedCopyDTO.getErrorLoadedCopyDTOS().add(
                        new ErrorLoadedCopyDTO(copy.getId(),
                                "Copy with id " + copy.getId() + " already exist in this request."));
                iterator.remove();
            }
        }
    }
}
