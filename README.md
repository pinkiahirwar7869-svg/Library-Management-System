# Library Management System

A console-based Library Management System built with **Core Java, OOP and MySQL (JDBC)**. It manages books and members, handles issue/return of books, and calculates late-return fines automatically.

## Features
- Add, view and search books (by title, author or ISBN)
- Add and view members
- Issue a book (14-day loan period)
- Return a book with automatic fine calculation (Rs. 2 per late day)
- Block issuing if the member has unpaid fines or no copies are available
- Pay fines and view all issue records

## Tech Stack
- Java 17+
- MySQL 8
- JDBC (MySQL Connector/J)
- Maven, NetBeans

## Project Structure
```
src/main/java/com/library/librarymanagement/
 ├── Book.java, Member.java       Model classes
 ├── BookDAO.java, MemberDAO.java Database access (all SQL here)
 ├── LibraryService.java          Business rules (issue, return, fine)
 ├── LibraryException.java        Custom exception
 ├── DBConnection.java            JDBC connection
 └── Main.java                    Console menu
schema.sql                        Database tables and sample data
```

## Design Highlights
- **Layered architecture:** model, DAO and service layers kept separate
- **Transactions:** issue and return use commit/rollback so stock count and issue records stay consistent
- **Safe stock update:** `UPDATE ... WHERE available_copies > 0` prevents issuing the last copy twice
- **PreparedStatement** everywhere, which prevents SQL injection
- **Custom exception** for business rule violations

## How to Run
1. Run `schema.sql` in MySQL Workbench to create `library_db`.
2. Open `DBConnection.java` and set your MySQL password.
3. Open the project in NetBeans (or any IDE) and let Maven download dependencies.
4. Run `Main.java`.

## Screenshots
Add screenshots of the menu, book list and issue records here.

## Future Improvements
- Login for librarian/admin with hashed passwords
- JUnit tests for fine calculation
- Swing/JavaFX GUI
- Spring Boot REST API
