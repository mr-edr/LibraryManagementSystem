package com.mredr.Libraray_management.dto;

import com.mredr.Libraray_management.model.Books;

import java.util.List;

public class BorrowedBookSummary {

        private Books book;
        private long borrowerCount;
        private List<BorrowerInfo> borrowers;


    public BorrowedBookSummary(Books book, long borrowerCount, List<BorrowerInfo> borrowers) {
        this.book = book;
        this.borrowerCount = borrowerCount;
        this.borrowers = borrowers;
    }

    public Books getBook() {
        return book;
    }

    public void setBook(Books book) {
        this.book = book;
    }

    public long getBorrowerCount() {
        return borrowerCount;
    }

    public void setBorrowerCount(long borrowerCount) {
        this.borrowerCount = borrowerCount;
    }

    public List<BorrowerInfo> getBorrowers() {
        return borrowers;
    }

    public void setBorrowers(List<BorrowerInfo> borrowers) {
        this.borrowers = borrowers;
    }
}
