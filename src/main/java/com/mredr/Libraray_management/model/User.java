package com.mredr.Libraray_management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mredr.Libraray_management.model.enums.Role;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true,nullable = false)
    private String userName;


    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Library> borrowedBooks;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false,columnDefinition = "BOOLEAN DEFAULT TRUE")
    @JsonIgnore
    private boolean nonExpired=true;

    @Column(nullable = false,columnDefinition = "BOOLEAN DEFAULT TRUE")
    @JsonIgnore
    private boolean nonLocked=true;

    @Column(nullable = false,columnDefinition = "BOOLEAN DEFAULT TRUE")
    @JsonIgnore
    private boolean credsNonExpired=true;

    @Column(nullable = false,columnDefinition = "BOOLEAN DEFAULT TRUE")
    @JsonIgnore
    private boolean isEnabled=true;

    @JsonIgnore
    public List<Library> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(List<Library> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public void setCredsNonExpired(boolean credsNonExpired) {
        this.credsNonExpired = credsNonExpired;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isNonExpired() {
        return nonExpired;
    }

    public void setNonExpired(boolean nonExpired) {
        this.nonExpired = nonExpired;
    }

    public boolean isNonLocked() {
        return nonLocked;
    }

    public void setNonLocked(boolean nonLocked) {
        this.nonLocked = nonLocked;
    }

    public boolean isCredsNonExpired() {
        return credsNonExpired;
    }

    public void setCredsNonExpires(boolean credsNonExpired) {
        this.credsNonExpired = credsNonExpired;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", role=" + role +
                ", password='" + password + '\'' +
                ", nonExpired=" + nonExpired +
                ", nonLocked=" + nonLocked +
                ", credsNonExpired=" + credsNonExpired +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
