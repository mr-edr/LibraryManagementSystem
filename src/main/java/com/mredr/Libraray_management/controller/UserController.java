package com.mredr.Libraray_management.controller;

import com.mredr.Libraray_management.dto.BorrowedBookSummary;
import com.mredr.Libraray_management.model.Library;
import com.mredr.Libraray_management.model.User;

import com.mredr.Libraray_management.model.UserPrincipal;
import com.mredr.Libraray_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user){
        return userService.register(user);
    }

    @GetMapping("/my-books")
    public List<Library> getMyBorrowedBooks(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userService.getBorrowedBooks(userPrincipal.getUsername());
    }


    //admin
    @GetMapping("/admin/users")
    public List<User> getUsers(){
        return userService.getUsers();
    }







}
