# Library Management System

A full-stack Library Management System built with **Spring Boot** (backend) and **vanilla HTML/CSS/JS** (frontend), built end-to-end by hand without AI assistance as a placement-prep portfolio project.

## Overview

The system supports a request → approval workflow: users request to borrow, extend, or return books, and a librarian reviews and approves/rejects those requests. This was chosen deliberately over a self-service model to demonstrate role-based workflows, state management, and business logic beyond simple CRUD.

## Tech Stack

- **Backend:** Java, Spring Boot, Spring Data JPA, Spring Security
- **Database:** MySQL (Hibernate `ddl-auto=update` for schema management in dev)
- **Frontend:** React (Vite), Lucide-React, modern responsive CSS
- **Auth:** Session-based authentication with BCrypt password encoding (CSRF currently disabled); a planned future migration to stateless JWT auth is scoped for after core features are complete

## Running the Application

### 1. Backend (Spring Boot)
```bash
./mvnw spring-boot:run
```
Runs the Spring Boot server on `http://localhost:8080`. When running standalone, it serves the compiled React application directly from `src/main/resources/static`.

### 2. Frontend Development Server (Vite)
```bash
cd frontend
npm install
npm run dev
```
Runs the Vite development server on `http://localhost:5173` with hot module replacement (HMR) and automatic API proxying to `http://localhost:8080`.

### 3. Frontend Production Build
```bash
cd frontend
npm run build
```
Builds and outputs production-optimized bundles directly into `src/main/resources/static/`.

## Key Features Implemented

- **React Single-Page Application:**
  - **Auth & Session Management:** Modal for Login and Registration, role badges, auto session hydration.
  - **Book Catalog Browser:** Search books by title/description, filter by category or availability, with direct "Request Borrow" action.
  - **User Loans & Requests Hub:** View active borrowed books, request 14-day due date extensions, initiate book returns, and track approval status (`PENDING`, `ACCEPTED`, `REJECTED`).
  - **Librarian Operations Desk:** Queue of pending circulation requests with one-click Approve / Reject, grouped borrows overview, and real-time overdue loan tracking with dynamic fine calculations.
  - **Admin Console:** User directory management with role promotion (`USER` ↔ `LIBRARIAN` ↔ `ADMIN`) and account locking/unlocking.
- **Robust Backend APIs:**
  - Full request-approval workflow (`BORROW`, `EXTEND`, `RETURN`).
  - Safe catalog CRUD operations (blocking deletion of loaned books).
  - Unified JSON error responses via `@RestControllerAdvice`.
  - Automated Mockito and Spring Boot integration test suite.

## Known Gaps / Next Steps

- JWT-based stateless authentication migration planned once core features are complete
- Minor naming cleanup planned (e.g. renaming `Library` entity to something like `BorrowRecord` for clarity)

## Status

Actively in development.

