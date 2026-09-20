package com.mredr.Libraray_management.controller;

import com.mredr.Libraray_management.model.TransactionRequest;
import com.mredr.Libraray_management.model.UserPrincipal;
import com.mredr.Libraray_management.model.enums.RequestStatus;
import com.mredr.Libraray_management.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping({"/request/borrow/{bookId}", "/request/{bookId}"})
    public TransactionRequest borrowRequest(
            @PathVariable long bookId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return transactionService.borrowRequest(bookId, userPrincipal.getUsername());
    }

    @PostMapping("/request/extend/{transactionId}")
    public TransactionRequest extendRequest(
            @PathVariable long transactionId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return transactionService.extendRequest(transactionId, userPrincipal.getUsername());
    }

    @PostMapping("/request/return/{transactionId}")
    public TransactionRequest returnRequest(
            @PathVariable long transactionId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return transactionService.returnRequest(transactionId, userPrincipal.getUsername());
    }

    @GetMapping("/my-requests")
    public List<TransactionRequest> getMyRequests(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return transactionService.getUserRequests(userPrincipal.getUsername());
    }

    @GetMapping("/librarian/requests")
    public List<TransactionRequest> getRequests(@RequestParam(required = false) RequestStatus status) {
        return transactionService.getRequests(status);
    }

    @PutMapping({"/librarian/requests/{requestId}/approve", "/librarian/requests/{requestId}"})
    public TransactionRequest approveRequest(
            @PathVariable long requestId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return transactionService.approveRequest(requestId, userPrincipal.getUsername());
    }

    @PutMapping("/librarian/requests/{requestId}/reject")
    public TransactionRequest rejectRequest(
            @PathVariable long requestId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return transactionService.rejectRequest(requestId, userPrincipal.getUsername());
    }
}
