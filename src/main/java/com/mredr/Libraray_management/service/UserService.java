package com.mredr.Libraray_management.service;

import com.mredr.Libraray_management.dto.UserProfileDto;
import com.mredr.Libraray_management.model.Library;
import com.mredr.Libraray_management.model.User;
import com.mredr.Libraray_management.model.enums.Borrowed;
import com.mredr.Libraray_management.model.enums.Role;
import com.mredr.Libraray_management.repo.LibraryRepo;
import com.mredr.Libraray_management.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private LibraryRepo libraryRepo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User register(User user) {
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (userRepo.existsByUserName(user.getUserName())) {
            throw new IllegalArgumentException("Username is already taken: " + user.getUserName());
        }

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setNonExpired(true);
        user.setNonLocked(true);
        user.setCredsNonExpires(true);
        user.setEnabled(true);

        return userRepo.save(user);
    }

    public List<User> getUsers() {
        return userRepo.findAll();
    }

    public User getUserByUsername(String username) {
        User user = userRepo.findByUserName(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        return user;
    }

    public UserProfileDto getUserProfile(String username) {
        User user = getUserByUsername(username);
        return new UserProfileDto(user.getUserId(), user.getName(), user.getUserName(), user.getRole());
    }

    public List<Library> getBorrowedBooks(String username) {
        User user = getUserByUsername(username);
        return libraryRepo.findByUserAndBorrowed(user, Borrowed.BORROWED);
    }

    public User updateUserRole(long userId, Role newRole) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setRole(newRole);
        return userRepo.save(user);
    }

    public User toggleUserStatus(long userId, boolean enabled) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setEnabled(enabled);
        return userRepo.save(user);
    }
}
