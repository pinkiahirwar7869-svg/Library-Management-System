CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

CREATE TABLE IF NOT EXISTS books (
  book_id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  author VARCHAR(100),
  isbn VARCHAR(20) UNIQUE,
  total_copies INT NOT NULL,
  available_copies INT NOT NULL
);

CREATE TABLE IF NOT EXISTS members (
  member_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE,
  phone VARCHAR(15),
  active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS issues (
  issue_id INT PRIMARY KEY AUTO_INCREMENT,
  book_id INT NOT NULL,
  member_id INT NOT NULL,
  issue_date DATE NOT NULL,
  due_date DATE NOT NULL,
  return_date DATE,
  fine_amount DECIMAL(8,2) DEFAULT 0,
  fine_paid BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (book_id) REFERENCES books(book_id),
  FOREIGN KEY (member_id) REFERENCES members(member_id)
);

INSERT INTO books (title, author, isbn, total_copies, available_copies) VALUES
('Head First Java', 'Kathy Sierra', '9780596009205', 3, 3),
('Effective Java', 'Joshua Bloch', '9780134685991', 2, 2),
('Clean Code', 'Robert C. Martin', '9780132350884', 2, 2);

INSERT INTO members (name, email, phone) VALUES
('Rahul Sharma', 'rahul@example.com', '9876543210'),
('Priya Verma', 'priya@example.com', '9123456780');
