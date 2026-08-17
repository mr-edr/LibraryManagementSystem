package com.mredr.Libraray_management.service;

import com.mredr.Libraray_management.model.Library;
import com.mredr.Libraray_management.model.User;
import com.mredr.Libraray_management.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    public UserRepo userRepo;

    private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);

    public User register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public List<User> getUsers() {
        return userRepo.findAll();
    }


    public List<Library> getBorrowedBooks(String username) {
        return userRepo.findByUserName(username).getBorrowedBooks();
    }
}
