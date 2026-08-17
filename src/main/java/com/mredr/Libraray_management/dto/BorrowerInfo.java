package com.mredr.Libraray_management.dto;

import com.mredr.Libraray_management.model.User;

import java.time.LocalDate;

public class BorrowerInfo {
    private User user;
    private LocalDate dueDate;

    public BorrowerInfo(User user, LocalDate dueDate) {
        this.user = user;
        this.dueDate = dueDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
