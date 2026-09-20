package com.mredr.Libraray_management.dto;

import com.mredr.Libraray_management.model.enums.Role;

public class UserProfileDto {
    private long userId;
    private String name;
    private String userName;
    private Role role;

    public UserProfileDto() {
    }

    public UserProfileDto(long userId, String name, String userName, Role role) {
        this.userId = userId;
        this.name = name;
        this.userName = userName;
        this.role = role;
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
}
