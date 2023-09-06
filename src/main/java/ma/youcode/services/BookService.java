package ma.youcode.services;

import ma.youcode.config.Database;
import ma.youcode.entities.Author;
import ma.youcode.entities.Book;
import ma.youcode.entities.BookReport;
import ma.youcode.utils.BookFactory;
import ma.youcode.utils.Components;
import ma.youcode.utils.Printer;
import ma.youcode.utils.Reader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

public class BookService {

    private static List<Book> booksList = new ArrayList<>();
    private static String ERROR_MESSAGE = null;

    private static BookService instance;

    private BookService() {

    }

    public static BookService getInstance() {
        if (instance == null) {
            instance = new BookService();
        }
        return instance;
    }

    private List<Book> findAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "CALL findAllBooks()";
        try (Connection connection = Database.getConnection();
                var preparedStatement = connection.prepareStatement(query);
                var resultSet = preparedStatement.executeQuery(query);) {
            books.addAll(BookFactory.tooBookList(resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // update cache
        booksList.clear();
        booksList.addAll(books);
        return books;

    }

    public void showBooks() {
        Components.Header();

        System.out.println("List of books : ");
        var books = findAllBooks();
        books.forEach(Printer::printBook);
    }

    public void addBook() {
        Components.Header();
        if (ERROR_MESSAGE != null) {
            System.out.println(ERROR_MESSAGE);
            ERROR_MESSAGE = null;
        }
        Book newBook = new Book();
        System.out.println("Enter book information  :");
        newBook.setTitle(Reader.readString("[book title] : ", false));
        newBook.setAuthor(new Author(0, Reader.readString("[book author] : ", false)));
        newBook.setIsbn(Reader.readString("[book isbn] : ", false));
        newBook.setCopies(Reader.readInt("[copies] : ", false));
        if (!newBook.isValidInputs()) {
            ERROR_MESSAGE = "[!] : All fields are required .";
            addBook();
        } else if (!hasUniqueIsbn(newBook.getIsbn())) {
            ERROR_MESSAGE = "[!] : isbn must be unique .";
            addBook();
        }
        try {
            newBook.save();
            System.out.println("[+] : Book added successfully .");
        } catch (SQLException e) {
            System.out.println("[!] : " + e.getMessage());
        }

    }

    private boolean hasUniqueIsbn(String isbn) {
        boolean isUnique = false;
        if (!booksList.isEmpty())
            isUnique = booksList.stream().anyMatch(book -> book.getIsbn().equals(isbn));
        else {
            String query = "SELECT `isUniqueIsbn`(?)";
            try (Connection connection = Database.getConnection();
                    var preparedStatement = connection.prepareStatement(query);) {
                preparedStatement.setString(1, isbn);
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    isUnique = resultSet.getBoolean(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isUnique;
    }

    public void updateBook() {
        Components.Header();
        if (ERROR_MESSAGE != null) {
            System.out.println(ERROR_MESSAGE);
            ERROR_MESSAGE = null;
        }
        String isbn = Reader.readString("Enter isbn of the book to update : ", false);
        boolean anyMatch = findBookByIsbn(isbn).isPresent();
        if (!anyMatch) {
            ERROR_MESSAGE = "[!] : Book not found .";
            updateBook();
        } else {
            Book book = booksList.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().get();
            System.out.println("[[ Enter updated book information ]]");
            String title = Reader.readString("[book title (" + book.getTitle() + ")] : ", true);
            book.setTitle(title.isEmpty() ? book.getTitle() : title);
            String authorName = Reader.readString("[book author (" + book.getAuthor().name() + ")] : ", true);
            book.setAuthor(new Author(0, authorName.isEmpty() ? book.getAuthor().name() : authorName));
            int copies = Reader.readInt("[number of copies] (" + book.getCopies() + "): ", true);
            book.setCopies(copies == 0 ? book.getCopies() : copies);
            try {
                book.update();
                System.out.println("[+] : Book updated successfully .");
            } catch (SQLException e) {
                ERROR_MESSAGE = "[!] : " + e.getMessage();
                updateBook();
            }

        }

    }

    public Optional<Book> findBookByIsbn(String isbn) {
        if (!booksList.isEmpty()) {
            return booksList.stream().filter(book -> book.getIsbn().equals(isbn)).findFirst();
        }
        String query = "CALL findBookByIsbn(?)";
        try (Connection connection = Database.getConnection();
                var preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, isbn);
            var resultSet = preparedStatement.executeQuery();
            return BookFactory.tooBookList(resultSet).stream().findFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void deleteBook() {
        Components.Header();
        if (ERROR_MESSAGE != null) {
            System.out.println(ERROR_MESSAGE);
            ERROR_MESSAGE = null;
        }
        String isbn = Reader.readString("Enter isbn of the book to delete : ", false);
        boolean anyMatch = booksList.stream().anyMatch(book -> book.getIsbn().equals(isbn));
        if (!anyMatch) {
            ERROR_MESSAGE = "[!] : Book not found .";
            deleteBook();
        } else {
            Book book = booksList.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().get();
            try {
                book.delete();
                System.out.println("[+] : Book deleted successfully .");
            } catch (SQLException e) {
                ERROR_MESSAGE = "[!] : " + e.getMessage();
                deleteBook();
            }
        }
    }

    public List<Book> findAvailableBooks() {
        return new ArrayList<>();
    }

    public List<Book> findBorrowedBooks() {
        return new ArrayList<>();
    }

    public void searchBooks() {
        Components.Header();
        String search = Reader.readString("Enter book title or author : ", false);
        if (!booksList.isEmpty()) {
            booksList.stream()
                    .filter(book -> book.getTitle().toLowerCase().contains(search)
                            || book.getAuthor().name().toLowerCase().contains(search))
                    .forEach(Printer::printBook);
        } else {
            String query = "CALL findBooksByTitleOrAuthor(?)";
            try (Connection connection = Database.getConnection();
                    var preparedStatement = connection.prepareStatement(query);) {
                preparedStatement.setString(1, search);
                var resultSet = preparedStatement.executeQuery();
                List<Book> books = BookFactory.tooBookList(resultSet);
                System.out.println(books.size());
                books
                        .forEach(Printer::printBook);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<BookReport> generateReport() {
        String query = "CALL generateReport()";
        try (Connection connection = Database.getConnection();
                var preparedStatement = connection.prepareStatement(query);
                var resultSet = preparedStatement.executeQuery(query);) {
            return BookFactory.toBookReport(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return List.of();
    }

    public void showStatistics() {
        Components.Header();
        generateReport().forEach(Printer::printBookReport);
        System.out.println("""
                [+] : Report generated successfully .
                                        """);
    }
}
