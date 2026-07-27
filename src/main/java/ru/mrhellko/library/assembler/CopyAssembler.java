package ru.mrhellko.library.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Copy;
import ru.mrhellko.library.dao.CopyDAO;

import java.util.List;

@Service
public class CopyAssembler {
    @Autowired
    private CopyDAO copyDAO;

    public List<Copy> getCopiesByBookId(Long bookId) {
        return copyDAO.getCopiesByBookId(bookId);
    }
}
