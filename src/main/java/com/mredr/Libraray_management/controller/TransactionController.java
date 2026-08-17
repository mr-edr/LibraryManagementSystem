package com.mredr.Libraray_management.controller;


import com.mredr.Libraray_management.model.TransactionRequest;
import com.mredr.Libraray_management.model.UserPrincipal;
import com.mredr.Libraray_management.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @RequestMapping("/request/{bookId}")
    public TransactionRequest borrowRequest(@PathVariable long bookId , @AuthenticationPrincipal UserPrincipal userPrincipal){
        return transactionService.borrowRequest(bookId,userPrincipal.getUsername());
    }

    @RequestMapping("/request/return/{transactionId}")
    public TransactionRequest returnRequest(@PathVariable long transactionId){
        return transactionService.returnRequest(transactionId);
    }



    @RequestMapping("/librarian/requests")
    public List<TransactionRequest> getRequests(){
        return transactionService.getRequests();
    }

    @PutMapping("/librarian/requests/{requestId}")
    public TransactionRequest approveRequest(@PathVariable long requestId, @AuthenticationPrincipal UserPrincipal userPrincipal){
        return transactionService.approveRequest(requestId,userPrincipal.getUsername());
    }




}
