package ru.mrhellko.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrhellko.library.Entity.Copy;
import ru.mrhellko.library.assembler.CopyAssembler;
import ru.mrhellko.library.dto.CopyStorageLocationIDDTO;
import ru.mrhellko.library.dto.ResultLoadedCopyDTO;

import java.util.List;

@RestController
@RequestMapping("/books")
public class CopyController {
    @Autowired
    private CopyAssembler copyAssembler;

    @GetMapping("/{bookId}/copy")
    public ResponseEntity<List<Copy>> getCopiesByBookId(@PathVariable Long bookId) {
        List<Copy> copies = copyAssembler.getCopiesByBookId(bookId);
        if (!copies.isEmpty()) {
            return new ResponseEntity<>(copies, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("/{bookId}/copy")
    public ResponseEntity<ResultLoadedCopyDTO> saveCopies(@RequestBody List<CopyStorageLocationIDDTO> copies, @PathVariable Long bookId) {
        ResultLoadedCopyDTO resultLoadedCopyDTO = copyAssembler.saveCopies(copies, bookId);
        if (resultLoadedCopyDTO != null) {
            return new ResponseEntity<>(resultLoadedCopyDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
}
