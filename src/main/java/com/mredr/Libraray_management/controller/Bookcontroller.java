package com.mredr.Libraray_management.controller;

import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Bookcontroller {

    @Autowired
    BookService bookservice;

    @GetMapping("/books")
    List<Books> getBooks(){
        return bookservice.getBooks();
    }

    @GetMapping("/books/{id}")
    Books getBookById(@PathVariable long id){
        return bookservice.getBookById(id);
    }
}
