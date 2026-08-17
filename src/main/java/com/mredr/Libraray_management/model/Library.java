package com.mredr.Libraray_management.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mredr.Libraray_management.model.enums.Borrowed;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="Library")
public class Library {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transactionId;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    @JsonBackReference
    private User user;


    @ManyToOne
    @JoinColumn(name = "book_id",nullable = false)
    private Books book;

    @Enumerated(EnumType.STRING)
    private Borrowed borrowed;

    private LocalDate dueDate;

    private LocalDate returnDate;

    @ManyToOne
    @JoinColumn(name="librarian_id",nullable = false)
    private User librarian;

    public Library() {
    }

    public Library(LocalDate dueDate, User user, Books book, User librarian) {
        this.dueDate = dueDate;
        this.book=book;
        this.user=user;
        this.librarian=librarian;



    }

    public User getLibrarian() {
        return librarian;
    }

    public void setLibrarian(User librarian) {
        this.librarian = librarian;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Books getBook() {
        return book;
    }

    public void setBook(Books book) {
        this.book = book;
    }

    public Borrowed getBorrowed() {
        return borrowed;
    }

    public void setBorrowed(Borrowed borrowed) {
        this.borrowed = borrowed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "Library{" +
                "transactionId=" + transactionId +
                ", userId=" + user +
                ", bookId=" + book +
                ", borrowed=" + borrowed +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
