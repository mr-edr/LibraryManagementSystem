# Library Management System

A full-stack Library Management System built with **Spring Boot** (backend) and **vanilla HTML/CSS/JS** (frontend), built end-to-end by hand without AI assistance as a placement-prep portfolio project.

## Overview

The system supports a request → approval workflow: users request to borrow, extend, or return books, and a librarian reviews and approves/rejects those requests. This was chosen deliberately over a self-service model to demonstrate role-based workflows, state management, and business logic beyond simple CRUD.

## Tech Stack

- **Backend:** Java, Spring Boot, Spring Data JPA, Spring Security
- **Database:** MySQL (Hibernate `ddl-auto=update` for schema management in dev)
- **Frontend:** HTML, CSS, vanilla JavaScript (`fetch` API)
- **Auth:** Session-based authentication with BCrypt password encoding (CSRF currently disabled); a planned future migration to stateless JWT auth is scoped for after core features are complete

## Project Structure

```
com.mredr.Libraray_management
├── config          # SecurityConfig
├── controller       # AdminController, BookController, LibraryController, TransactionController, UserController
├── model             # Books, Library, TransactionRequest, User, UserPrincipal, enums (RequestType, RequestStatus, Borrowed, Availability)
├── repo              # BookRepo, LibraryRepo, TransactionRepo, UserRepo
└── service          # BookService, LibraryService, MyUserDetailsService, TransactionService, UserService
```

## Core Domain Model

- **`User`** — application users, with roles (e.g. `USER`, `ADMIN`/`LIBRARIAN`)
- **`Books`** — catalog items with stock count and availability status
- **`TransactionRequest`** — a user-submitted request (`BORROW`, `EXTEND`, or `RETURN`) with a status (`PENDING`, `ACCEPTED`, `REJECTED`)
- **`Library`** — the actual borrow record created once a `BORROW` request is approved, tracking due date, return date, borrow status, and which librarian approved it

## Key Features Implemented So Far

- **Borrow requests:** users request to borrow an available book; librarian approval creates a `Library` (borrow) record with a 14-day due date and decrements book stock
- **Extension requests:** users request a due-date extension on an active borrow; approval extends the due date by 14 days
- **Return requests:** users initiate a return referencing their active borrow transaction; approval marks the record as returned and restores book stock/availability
- **Role-based access:** librarian-only endpoints (e.g. approving requests) restricted via Spring Security (`hasAnyRole`)
- **Librarian dashboard view:** endpoint returning borrowed books grouped by book, with borrower count and each borrower's due date
- **Book listing page:** frontend page that fetches and renders the book catalog in a table

## Design Decisions

- **Resource-based controller structure:** all transaction request logic (creation by users, approval by librarians) lives in a single `TransactionController` → `TransactionService` → `TransactionRepo`, rather than splitting by which role acts on it — keeps the request's lifecycle in one place.
- **Approval side-effects wrapped in `@Transactional`:** approving a request can touch `TransactionRequest`, `Library`, and `Books` in one operation, so the whole flow is transactional to avoid inconsistent partial state.
- **Bidirectional entity relationships (`User` ↔ `Library`) required care around serialization:** `@JsonIgnore` used to prevent infinite recursion in JSON responses, and manual `toString()` overrides used to avoid the same issue when logging/debugging.
- **DTOs for summary views:** rather than serializing full entities (which leak internal fields and risk recursion), purpose-built DTOs (e.g. `BorrowedBookSummary`, `BorrowerInfo`) are used for read-heavy endpoints, and sensitive fields on `User` (like password) are excluded from API responses via `@JsonIgnore`.

## Known Gaps / Next Steps

- Login page and full frontend flows (borrow/extend/return UI, librarian dashboard UI) still in progress
- Late-return fine calculation not yet implemented
- JWT-based stateless authentication migration planned once core features are complete
- Minor naming cleanup planned (e.g. renaming `Library` entity to something like `BorrowRecord` for clarity)

## Status

Actively in development .
