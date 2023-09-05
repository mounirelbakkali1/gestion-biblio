package ma.youcode.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ma.youcode.entities.Author;
import ma.youcode.entities.Book;
import ma.youcode.entities.BookReport;

public class BookFactory {

    public static List<Book> tooBookList(ResultSet resultSet) {
        List<Book> books = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Book book = new Book();
                book.setIsbn(resultSet.getString("isbn"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(new Author(resultSet.getInt("author_id"), resultSet.getString("author_name")));
                book.setCopies(resultSet.getInt("copies"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public static List<BookReport> toBookReport(ResultSet resultSet) {
        List<BookReport> bookReports = new ArrayList<>();
        try {
            while (resultSet.next()) {
                BookReport bookReport = new BookReport();
                bookReport.setIsbn(resultSet.getString("isbn"));
                bookReport.setTitle(resultSet.getString("title"));
                bookReport.setAuthor(new Author(resultSet.getInt("author_id"), resultSet.getString("author_name")));
                bookReport.setCopies(resultSet.getInt("copies"));
                bookReport.setAvailable(resultSet.getInt("available"));
                bookReport.setBorrowed(resultSet.getInt("borrowed"));
                bookReport.setLost(resultSet.getInt("lost"));
                bookReports.add(bookReport);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookReports;
    }
}
