package com.mredr.Libraray_management.controller;

import com.mredr.Libraray_management.dto.UserProfileDto;
import com.mredr.Libraray_management.model.Library;
import com.mredr.Libraray_management.model.User;
import com.mredr.Libraray_management.model.UserPrincipal;
import com.mredr.Libraray_management.model.enums.Role;
import com.mredr.Libraray_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @GetMapping("/user/me")
    public UserProfileDto getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userService.getUserProfile(userPrincipal.getUsername());
    }

    @GetMapping("/my-books")
    public List<Library> getMyBorrowedBooks(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userService.getBorrowedBooks(userPrincipal.getUsername());
    }

    @GetMapping("/admin/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PutMapping("/admin/users/{userId}/role")
    public User updateUserRole(@PathVariable long userId, @RequestParam Role role) {
        return userService.updateUserRole(userId, role);
    }

    @PutMapping("/admin/users/{userId}/status")
    public User toggleUserStatus(@PathVariable long userId, @RequestParam boolean enabled) {
        return userService.toggleUserStatus(userId, enabled);
    }
}
