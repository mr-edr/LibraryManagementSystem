package com.mredr.Libraray_management.controller;

import com.mredr.Libraray_management.dto.BorrowedBookSummary;
import com.mredr.Libraray_management.dto.OverdueLoanSummary;
import com.mredr.Libraray_management.model.Library;
import com.mredr.Libraray_management.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/librarian/borrowed")
    public List<BorrowedBookSummary> getBorrowedSummary() {
        return libraryService.getBorrowedBooksSummary();
    }

    @GetMapping("/librarian/overdue")
    public List<OverdueLoanSummary> getOverdueLoans() {
        return libraryService.getOverdueBorrows();
    }

    @GetMapping("/librarian/borrows/{transactionId}")
    public Library getBorrowById(@PathVariable long transactionId) {
        return libraryService.getBorrowById(transactionId);
    }
}
