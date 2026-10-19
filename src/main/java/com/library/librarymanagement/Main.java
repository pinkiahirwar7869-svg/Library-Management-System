
package com.library.librarymanagement;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final BookDAO bookDAO = new BookDAO();
    private static final MemberDAO memberDAO = new MemberDAO();
    private static final LibraryService service = new LibraryService();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. Add book");
            System.out.println("2. View all books");
            System.out.println("3. Search books");
            System.out.println("4. Add member");
            System.out.println("5. View members");
            System.out.println("6. Issue book");
            System.out.println("7. Return book");
            System.out.println("8. Pay fine");
            System.out.println("9. View issue records");
            System.out.println("0. Exit");
            int choice = readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> printBooks(bookDAO.getAllBooks());
                    case 3 -> printBooks(bookDAO.search(readLine("Keyword: ")));
                    case 4 -> addMember();
                    case 5 -> memberDAO.getAllMembers().forEach(System.out::println);
                    case 6 -> service.issueBook(readInt("Book ID: "), readInt("Member ID: "));
                    case 7 -> service.returnBook(readInt("Issue ID: "));
                    case 8 -> service.payFine(readInt("Issue ID: "));
                    case 9 -> service.showIssues();
                    case 0 -> { System.out.println("Goodbye!"); return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (LibraryException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private static void addBook() throws SQLException {
        String title = readLine("Title: ");
        String author = readLine("Author: ");
        String isbn = readLine("ISBN: ");
        int copies = readInt("Copies: ");
        bookDAO.addBook(new Book(title, author, isbn, copies));
        System.out.println("Book added.");
    }

    private static void addMember() throws SQLException {
        String name = readLine("Name: ");
        String email = readLine("Email: ");
        String phone = readLine("Phone: ");
        memberDAO.addMember(new Member(name, email, phone));
        System.out.println("Member added.");
    }

    private static void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        System.out.printf("%-4s %-28s %-22s %-15s %s%n", "ID", "Title", "Author", "ISBN", "Avail");
        books.forEach(System.out::println);
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}