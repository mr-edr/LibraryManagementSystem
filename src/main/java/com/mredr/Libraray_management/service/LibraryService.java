package com.mredr.Libraray_management.service;

import com.mredr.Libraray_management.dto.BorrowerInfo;
import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.dto.BorrowedBookSummary;
import com.mredr.Libraray_management.model.Library;
import com.mredr.Libraray_management.model.User;
import com.mredr.Libraray_management.model.enums.Availability;
import com.mredr.Libraray_management.model.enums.Borrowed;
import com.mredr.Libraray_management.repo.BookRepo;
import com.mredr.Libraray_management.repo.LibraryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    @Autowired
    LibraryRepo libraryRepo;

    @Autowired
    BookRepo bookRepo;




    public Library createBorrow(User user, Books book, User librarian) {

        book.setStock(book.getStock()-1);

        if(book.getStock()==0){
            book.setAvailability(Availability.NOT_AVAILABLE);
        }

        if(book.getStock()<0)
            book.setStock(0);

        bookRepo.save(book);

        Library library = new Library(LocalDate.now(),user,book,librarian );
        library.setBorrowed(Borrowed.BORROWED);
        library.setDueDate(LocalDate.now().plusDays(14));

        return libraryRepo.save(library);
    }

    public List<BorrowedBookSummary> getBorrowedBooksSummary() {
        List<Library> activeBorrows = libraryRepo.findByBorrowed(Borrowed.BORROWED);
        System.out.println("Found: " + activeBorrows.size());

        Map<Books, List<Library>> grouped = activeBorrows.stream()
                .collect(Collectors.groupingBy(Library::getBook));

        for (Map.Entry<Books,List<Library>> entry : grouped.entrySet() ){
            System.out.println(entry.getKey() +" -> "+ entry.getValue().toString());
        }

        return grouped.entrySet().stream()
                .map(entry -> {
                    Books book = entry.getKey();
                    List<Library> records = entry.getValue();
                    List<BorrowerInfo> borrowers = records.stream()
                            .map(r -> new BorrowerInfo(r.getUser(), r.getDueDate()))
                            .collect(Collectors.toList());
                    return new BorrowedBookSummary(book, records.size(), borrowers);
                })
                .collect(Collectors.toList());
    }
}
