package ru.mrhellko.library.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Genre;
import ru.mrhellko.library.dao.GenreDAO;
import ru.mrhellko.library.exception.NotFoundException;

import java.util.List;

@Service
public class GenreService {
    @Autowired
    private GenreDAO genreDAO;

    public List<Genre> getAllGenres() {
        return genreDAO.getAllGenres();
    }

    public Genre getGenreById(Long id) {
        return genreDAO.getGenreById(id);
    }

    public Genre updateGenre(Genre genre, Long id) {
        Genre updatedGenre = genreDAO.getGenreById(id);
        if (updatedGenre != null) {
            updatedGenre.setId(id);
            updatedGenre.setGenreName(genre.getGenreName());

            genreDAO.updateGenre(updatedGenre);
            return updatedGenre;
        } else {
            return null;
        }
    }

    public Genre saveGenre(Genre genre) {
        return genreDAO.saveGenre(genre);
    }

    public void deleteGenre(Long id) {
        int resultGenre = genreDAO.deleteGenreById(id);
        if (resultGenre == 0) {
            throw new NotFoundException(id);
        }
    }
}
