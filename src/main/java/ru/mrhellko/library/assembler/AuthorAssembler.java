package ru.mrhellko.library.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Author;
import ru.mrhellko.library.dao.AuthorDAO;
import ru.mrhellko.library.exception.NotFoundException;

@Service
public class AuthorAssembler {
    @Autowired
    private AuthorDAO authorDAO;

    public Author getAuthorById(Long id) {
        return authorDAO.getAuthorById(id);
    }

    public Author updateAuthor(Author author, Long id) {
        Author updatedAuthor = authorDAO.getAuthorById(id);
        if (updatedAuthor != null) {
            updatedAuthor.setId(id);
            updatedAuthor.setAuthorName(author.getAuthorName());

            authorDAO.updateAuthor(updatedAuthor);
            return updatedAuthor;
        } else {
            return null;
        }
    }

    public Author saveAuthor(Author author) throws Exception {
        return authorDAO.saveAuthor(author);
    }

    public void deleteAuthor(Long id) throws Exception {
        int resultAuthor = authorDAO.deleteAuthorById(id);
        if (resultAuthor == 0) {
            throw new NotFoundException(id);
        }
    }
}
