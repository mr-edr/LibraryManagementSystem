package com.mredr.Libraray_management.model;

import com.mredr.Libraray_management.model.enums.RequestStatus;
import com.mredr.Libraray_management.model.enums.RequestType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class TransactionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long requestId;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="book_id")
    private Books book;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Library transaction;

    @Enumerated(EnumType.STRING)
    private RequestType type;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDate requestDate;


    public TransactionRequest(Books book, RequestType type, User user, RequestStatus status, LocalDate requestDate) {
        this.book = book;
        this.type = type;
        this.user = user;
        this.status = status;
        this.requestDate = requestDate;
    }

    public TransactionRequest() {
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
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

    public Library getTransaction() {
        return transaction;
    }

    public void setTransaction(Library transaction) {
        this.transaction = transaction;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }
}
