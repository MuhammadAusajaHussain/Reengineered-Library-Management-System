# System Analysis: Library Management System (LMS)

This document provides a detailed analysis of the Library Management System, a modernized web application reengineered from a legacy Java console application.

## 1. Project Overview
The system is a full-stack web application designed to manage library operations, including book inventory, user/borrower management, circulation (loans), fines, and hold requests.

## 2. Technology Stack

### Backend (`api/`)
- **Framework**: Spring Boot 2.7.x (Targeting Java 8/17)
- **Database**: Apache Derby (Embedded, zero-configuration)
- **Data Access**: Spring `JdbcTemplate` (Manual Row Mapping)
- **Security**: Custom token-based authentication with role-based access control (RBAC)
- **Build Tool**: Maven / mvnd
- **Port**: 9000

### Frontend (`client/`)
- **Framework**: React 18+ (Vite)
- **Language**: TypeScript
- **Styling**: Vanilla CSS with a "Glassmorphism" design system
- **State Management**: React Hooks (useState, useEffect, useContext)
- **Routing**: React Router
- **Port**: 8080 (Proxies `/api` to port 9000)

## 3. Architecture

### Backend Layering
The backend follows a clean, layered architecture:
- **API/Controller Layer**: REST endpoints (e.g., `BooksController`, `LoansController`)
- **Service Layer**: Business logic implementation (e.g., `LoanService`, `BookService`)
- **Infrastructure/Repository Layer**: Database interactions using SQL via `JdbcTemplate`
- **DTO Layer**: Data transfer objects for request/response payloads

### Data Model (Logical)
The system operates on several core entities:
- **Book**: ISBN, Title, Author, Subject, Total/Available Copies.
- **User/Borrower**: Role-based (Admin, Librarian, Clerk, Borrower), Login credentials, Profile details.
- **Loan**: Book-User mapping, Issue Date, Due Date, Return Date, Fine details.
- **Hold Request**: Queue for books that are currently unavailable.

## 4. Key Functionality

### Core Features
- **User Management**: CRUD operations for users and borrowers.
- **Catalog Management**: Searchable book catalog with "Merge-on-Create" logic.
- **Circulation**:
  - **Checkout**: Assigns a book to a borrower.
  - **Check-in**: Processes returns, calculates fines, and triggers hold fulfillment.
  - **Renewal**: Extends the due date of a loan.
- **Hold System**: Lifecycle management from `ACTIVE` to `READY` to `FULFILLED`.
- **Fines**: Automated fine calculation and payment tracking.
- **Dashboard**: Real-time statistics tailored to user roles.

### Advanced Modernization Features
- **Self-Healing Inventory**: Startup reconciliation to ensure data consistency.
- **Glassmorphism UI**: Premium visual design with dark mode and micro-animations.
- **Session Persistence**: Maintains user state across browser refreshes.

## 5. Directory Structure Summary
```
.
├── api/                  # Backend source code
│   └── src/main/java/com/lms/api/
│       ├── controller/   # REST Endpoints
│       ├── service/      # Business Logic
│       ├── infrastructure/# Persistence & Sessions
│       └── dto/          # Data Transfer Objects
├── client/               # Frontend source code
│   └── src/
│       ├── pages/        # React components for screens
│       └── types.ts      # TypeScript interfaces
├── Database/             # Derby DB storage (generated at runtime)
├── Project/              # Legacy Java source (for reference)
└── docs/                 # Project documentation
```

## 6. Implementation Notes
- **Persistence Strategy**: The choice of `JdbcTemplate` over JPA/Hibernate suggests a focus on performance and direct SQL control, fitting for a reengineering project where the legacy schema might have been closely followed or adapted.
- **Embedded DB**: The use of Derby makes the project highly portable and easy to set up for evaluation.
- **Bootstrap Logic**: The system automatically resets the admin account on startup, ensuring accessibility during development/demo.
