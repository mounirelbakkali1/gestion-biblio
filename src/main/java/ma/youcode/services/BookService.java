package ma.youcode.services;

import ma.youcode.config.Database;
import ma.youcode.entities.Author;
import ma.youcode.entities.Book;
import ma.youcode.entities.BookCopy;
import ma.youcode.entities.BookReport;
import ma.youcode.utils.BookFactory;
import ma.youcode.utils.Components;
import ma.youcode.utils.Printer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class BookService {

    private static List<Book> booksList = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);

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

    public void showAllBooks() {
        Components.Header();
        System.out.println("List of books : ");
        var books = findAllBooks();
        books.forEach(Printer::printBook);
    }

    public void addBook() {
        Components.Header();
        Book newBook = new Book();
        int numberOfCopies;
        System.out.println("Enter book information  :");
        System.out.println("[book title] : ");
        newBook.setTitle(sc.nextLine());
        System.out.println("[book author] : ");
        newBook.setAuthor(new Author(0, sc.nextLine()));
        System.out.println("[book isbn] : ");
        newBook.setIsbn(sc.nextLine());
        System.out.println("[copies] : ");
        numberOfCopies = sc.nextInt();
        newBook.setCopies(numberOfCopies);
        if (newBook.isValidInputs()) {
            System.out.println("[!] : All fields are required .");
            addBook();
        } else if (!hasUniqueIsbn(newBook.getIsbn())) {
            System.out.println("[!] : isbn must be unique .");
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
        System.out.println("Enter isbn of the book to update : ");
        String isbn = sc.nextLine();
        if (isbn.isEmpty() || isbn.isBlank()) {
            System.out.println("[!] : isbn is required .");
            updateBook();
        }
        boolean anyMatch = findBookByIsbn(isbn).isPresent();
        if (!anyMatch) {
            System.out.println("[!] : Book not found .");
        } else {
            Book book = booksList.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().get();
            System.out.println("Enter new book information : ");
            System.out.println("[book title] : ");
            book.setTitle(sc.nextLine());
            System.out.println("[book author] : ");
            book.setAuthor(new Author(0, sc.nextLine()));
            System.out.println("[book isbn] : ");
            book.setIsbn(sc.nextLine());
            if (book.isValidInputs()) {
                System.out.println("[!] : All fields are required .");
                updateBook();
            }
            try {
                book.update();
                System.out.println("[+] : Book updated successfully .");
            } catch (SQLException e) {
                System.out.println("[!] : " + e.getMessage());
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
        System.out.println("Enter isbn of the book to delete : ");
        String isbn = sc.nextLine();
        if (isbn.isEmpty() || isbn.isBlank()) {
            System.out.println("[!] : isbn is required .");
            deleteBook();
        }
        boolean anyMatch = booksList.stream().anyMatch(book -> book.getIsbn().equals(isbn));
        if (!anyMatch) {
            System.out.println("[!] : Book not found .");
        } else {
            Book book = booksList.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().get();
            try {
                book.delete();
                System.out.println("[+] : Book deleted successfully .");
            } catch (SQLException e) {
                System.out.println("[!] : " + e.getMessage());
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
        System.out.println("Enter book title or author : ");
        String search = sc.nextLine().toLowerCase();
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
        } catch (SQLException ignored) {
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
