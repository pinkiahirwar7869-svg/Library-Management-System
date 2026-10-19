/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.librarymanagement;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Business rules: issue, return, fine. Uses a transaction so data stays consistent. */
public class LibraryService {
    private static final int LOAN_DAYS = 14;
    private static final double FINE_PER_DAY = 2.0;

    public static double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        long lateDays = ChronoUnit.DAYS.between(dueDate, returnDate);
        return lateDays > 0 ? lateDays * FINE_PER_DAY : 0;
    }

    public void issueBook(int bookId, int memberId) throws LibraryException, SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                // 1. member must exist and be active
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT active FROM members WHERE member_id = ?")) {
                    ps.setInt(1, memberId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !rs.getBoolean("active"))
                            throw new LibraryException("Member not found or inactive.");
                    }
                }

                // 2. member must have no unpaid fines
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT COUNT(*) FROM issues WHERE member_id = ? "
                      + "AND fine_amount > 0 AND fine_paid = FALSE")) {
                    ps.setInt(1, memberId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) > 0)
                            throw new LibraryException("Member has unpaid fines. Clear them first.");
                    }
                }

                // 3. reduce stock only if a copy is available (safe even if two users try at once)
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE books SET available_copies = available_copies - 1 "
                      + "WHERE book_id = ? AND available_copies > 0")) {
                    ps.setInt(1, bookId);
                    if (ps.executeUpdate() == 0)
                        throw new LibraryException("Book not found or no copies available.");
                }

                // 4. record the issue
                LocalDate today = LocalDate.now();
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO issues (book_id, member_id, issue_date, due_date) VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, bookId);
                    ps.setInt(2, memberId);
                    ps.setDate(3, Date.valueOf(today));
                    ps.setDate(4, Date.valueOf(today.plusDays(LOAN_DAYS)));
                    ps.executeUpdate();
                }

                con.commit();
                System.out.println("Book issued. Due date: " + today.plusDays(LOAN_DAYS));
            } catch (LibraryException | SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void returnBook(int issueId) throws LibraryException, SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int bookId;
                LocalDate dueDate;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT book_id, due_date FROM issues "
                      + "WHERE issue_id = ? AND return_date IS NULL FOR UPDATE")) {
                    ps.setInt(1, issueId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next())
                            throw new LibraryException("No active issue found with that ID.");
                        bookId = rs.getInt("book_id");
                        dueDate = rs.getDate("due_date").toLocalDate();
                    }
                }

                LocalDate today = LocalDate.now();
                double fine = calculateFine(dueDate, today);

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE issues SET return_date = ?, fine_amount = ? WHERE issue_id = ?")) {
                    ps.setDate(1, Date.valueOf(today));
                    ps.setDouble(2, fine);
                    ps.setInt(3, issueId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?")) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }

                con.commit();
                System.out.println("Book returned." + (fine > 0 ? " Fine: Rs. " + fine : " No fine."));
            } catch (LibraryException | SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void payFine(int issueId) throws LibraryException, SQLException {
        String sql = "UPDATE issues SET fine_paid = TRUE WHERE issue_id = ? AND fine_amount > 0 AND fine_paid = FALSE";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            if (ps.executeUpdate() == 0)
                throw new LibraryException("No unpaid fine found for that issue ID.");
            System.out.println("Fine marked as paid.");
        }
    }

    public void showIssues() throws SQLException {
        String sql = "SELECT i.issue_id, b.title, m.name, i.issue_date, i.due_date, "
                   + "i.return_date, i.fine_amount, i.fine_paid "
                   + "FROM issues i JOIN books b ON i.book_id = b.book_id "
                   + "JOIN members m ON i.member_id = m.member_id ORDER BY i.issue_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.printf("%-4s %-24s %-16s %-11s %-11s %-11s %-6s %s%n",
                    "ID", "Book", "Member", "Issued", "Due", "Returned", "Fine", "Paid");
            while (rs.next()) {
                System.out.printf("%-4d %-24s %-16s %-11s %-11s %-11s %-6.0f %s%n",
                        rs.getInt("issue_id"), rs.getString("title"), rs.getString("name"),
                        rs.getDate("issue_date"), rs.getDate("due_date"),
                        rs.getDate("return_date") == null ? "-" : rs.getDate("return_date"),
                        rs.getDouble("fine_amount"), rs.getBoolean("fine_paid") ? "Yes" : "No");
            }
        }
    }
}
