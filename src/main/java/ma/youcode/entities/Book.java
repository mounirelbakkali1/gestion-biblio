package ma.youcode.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ma.youcode.config.Database;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private int copies ;

    Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public Book() {
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public void save(int numberOfCopies) throws SQLException {
        String query = "INSERT INTO books (isbn, title, author) VALUES (?, ?, ?)";
        String query2 = "INSERT INTO copies (isbn) VALUES (?)";
        try (Connection connection = Database.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);) {
            preparedStatement.setString(1, this.isbn);
            preparedStatement.setString(2, this.title);
            preparedStatement.setString(3, this.author);
            preparedStatement.executeUpdate();
            for (int i = 0; i < numberOfCopies; i++) {
                preparedStatement2.setString(1, this.isbn);
                preparedStatement2.execute();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update() throws SQLException {
        String query = "UPDATE books SET title = ?, author = ? WHERE isbn = ?";
        try (Connection connection = Database.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, this.title);
            preparedStatement.setString(2, this.author);
            preparedStatement.setString(3, this.isbn);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete() throws SQLException {
        String query = "DELETE FROM books WHERE isbn = ?";
        try (Connection connection = Database.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, this.isbn);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        // if(title=="" || title==null) throw new IllegalArgumentException("[!] : title
        // should not be empty .");
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

enum BookStatus {
    AVAILABLE, BORROWED
}
