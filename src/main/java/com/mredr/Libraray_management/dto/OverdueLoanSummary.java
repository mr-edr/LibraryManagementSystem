package com.mredr.Libraray_management.dto;

import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.model.User;

import java.time.LocalDate;

public class OverdueLoanSummary {
    private long transactionId;
    private Books book;
    private User borrower;
    private LocalDate dueDate;
    private long overdueDays;
    private double fineAmount;

    public OverdueLoanSummary() {
    }

    public OverdueLoanSummary(long transactionId, Books book, User borrower, LocalDate dueDate, long overdueDays, double fineAmount) {
        this.transactionId = transactionId;
        this.book = book;
        this.borrower = borrower;
        this.dueDate = dueDate;
        this.overdueDays = overdueDays;
        this.fineAmount = fineAmount;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public Books getBook() {
        return book;
    }

    public void setBook(Books book) {
        this.book = book;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public long getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(long overdueDays) {
        this.overdueDays = overdueDays;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }
}
