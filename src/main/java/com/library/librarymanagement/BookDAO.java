
package com.library.librarymanagement;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public void addBook(Book b) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn, total_copies, available_copies) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            ps.setString(3, b.getIsbn());
            ps.setInt(4, b.getTotalCopies());
            ps.setInt(5, b.getAvailableCopies());
            ps.executeUpdate();
        }
    }

    public List<Book> getAllBooks() throws SQLException {
        return query("SELECT * FROM books", null);
    }

    public List<Book> search(String keyword) throws SQLException {
        return query("SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?", keyword);
    }

    private List<Book> query(String sql, String keyword) throws SQLException {
        List<Book> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (keyword != null) {
                String like = "%" + keyword + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Book(rs.getInt("book_id"), rs.getString("title"),
                            rs.getString("author"), rs.getString("isbn"),
                            rs.getInt("total_copies"), rs.getInt("available_copies")));
                }
            }
        }
        return list;
    }
}