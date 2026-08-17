package com.mredr.Libraray_management.controller;


import com.mredr.Libraray_management.dto.BorrowedBookSummary;
import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LibraryController {

    @Autowired
    LibraryService libraryService;

    //librarian
    @GetMapping("/librarian/borrowed")
    public List<BorrowedBookSummary> getBorrowedSummary() {
        return libraryService.getBorrowedBooksSummary();
    }
}
