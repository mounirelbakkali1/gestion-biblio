package ma.youcode.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import ma.youcode.config.Database;
import ma.youcode.utils.BookFactory;

public class Book {
    private String isbn;
    private String title;
    private Author author;
    private int copies;

    public Book(String isbn, String title, Author author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public Book(String isbn, String title) {
        this.title = title;
        this.isbn = isbn;
    }

    public Book() {
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public void save() throws SQLException {
        String query = "CALL saveBook(?,?,?,?);";
        try (Connection connection = Database.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, this.isbn);
            preparedStatement.setString(2, this.title);
            preparedStatement.setString(3, this.author.name());
            preparedStatement.setInt(4, this.copies);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update() throws SQLException {
        String query = "call updateBook(?,?,?,?);";
        try (Connection connection = Database.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, this.title);
            preparedStatement.setString(2, this.author.name());
            preparedStatement.setInt(3, this.copies);
            preparedStatement.setString(4, this.isbn);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete() throws SQLException {
        String query = "call deleteBook(?);";
        try (Connection connection = Database.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, this.isbn);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean isUniqueIsbn(String isbn) {
        String query = "SELECT `isUniqueIsbn`(?)";
        try (Connection connection = Database.getConnection();
                var preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, isbn);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
        }
        return false;
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
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public boolean isValidInputs() {
        return !title.isEmpty() || !title.isBlank() ||
                !author.name().isEmpty() || !author.name().isBlank() ||
                !isbn.isEmpty() || !isbn.isBlank();
    }

    public static Optional<Book> findBookByIsbn(String isbn) {
        String query = "CALL findBookByIsbn(?)";
        try (Connection connection = Database.getConnection();
                var preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, isbn);
            var resultSet = preparedStatement.executeQuery();
            return BookFactory.tooBookList(resultSet).stream().findFirst();
        } catch (SQLException e) {
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author=" + author.name() +
                ", copies=" + copies +
                '}';
    }
}
