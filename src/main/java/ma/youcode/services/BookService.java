package ma.youcode.services;

import ma.youcode.config.Database;
import ma.youcode.entities.Book;
import ma.youcode.entities.BookCopy;
import ma.youcode.utils.Components;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

    public void findAllBooks() {
        Components.Header();
        System.out.println("List of books : ");
        List<Book> books = new ArrayList<>();
        String query = """
                SELECT books.*,COUNT(copies.ref) AS copies FROM books
                    left JOIN copies ON copies.isbn = books.isbn
                    GROUP BY books.isbn
                """;
        try (Connection connection = Database.getConnection();
                var preparedStatement = connection.prepareStatement(query);
                var resultSet = preparedStatement.executeQuery(query);) {
            while (resultSet.next()) {
                Book book = new Book();
                book.setIsbn(resultSet.getString("isbn"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                book.setCopies(resultSet.getInt("copies"));
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        books.forEach(book -> printBook(book));
        booksList.clear();
        booksList.addAll(books);
    }

    public void addBook() {
        Components.Header();
        Book newBook = new Book();
        int numberOfCopies;
        System.out.println("Enter book information  :");
        System.out.println("[book title] : ");
        newBook.setTitle(sc.nextLine());
        System.out.println("[book author] : ");
        newBook.setAuthor(sc.nextLine());
        System.out.println("[book isbn] : ");
        newBook.setIsbn(sc.nextLine());
        System.out.println("[copies] : ");
        numberOfCopies = sc.nextInt();
        if (isValidInput(newBook)) {
            System.out.println("[!] : All fields are required .");
            addBook();
        }
        try {
            newBook.save(numberOfCopies);
            System.out.println("[+] : Book added successfully .");
        } catch (SQLException e) {
            System.out.println("[!] : " + e.getMessage());
        }

    }

    public void updateBook() {
        Components.Header();
        System.out.println("Enter isbn of the book to update : ");
        String isbn = sc.nextLine();
        if (isbn.isEmpty() || isbn.isBlank()) {
            System.out.println("[!] : isbn is required .");
            updateBook();
        }
        boolean anyMatch = booksList.stream().anyMatch(book -> book.getIsbn().equals(isbn));
        if (!anyMatch) {
            System.out.println("[!] : Book not found .");
        } else {
            Book book = booksList.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().get();
            System.out.println("Enter new book information : ");
            System.out.println("[book title] : ");
            book.setTitle(sc.nextLine());
            System.out.println("[book author] : ");
            book.setAuthor(sc.nextLine());
            System.out.println("[book isbn] : ");
            book.setIsbn(sc.nextLine());
            if (isValidInput(book)) {
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

    public void findBooks() {

    }

    public List<Book> findAvailableBooks() {
        return List.of();
    }

    public List<Book> findBorrowedBooks() {
        return List.of();
    }

    public void searchBooks() {
        Components.Header();
        System.out.println("Enter book title or author : ");
        String search = sc.nextLine().toLowerCase();
        booksList.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(search)
                        || book.getAuthor().toLowerCase().contains(search))
                .forEach(book -> printBook(book));
    }

    public void generateReport() {
        Components.Header();
        String query = """
                SELECT
                    books.*,
                    COUNT(copies.ref) AS copies,
                    SUM(CASE WHEN copies.`status` = 'AVAILABLE' THEN 1 ELSE 0 END) AS available,
                    SUM(CASE WHEN copies.`status` = 'BORROWED' THEN 1 ELSE 0 END) AS borrowed,
                    SUM(CASE WHEN borrowing.date_of_returning < NOW() THEN 1 ELSE 0 END) AS lost
                FROM
                    books
                LEFT JOIN
                    copies ON copies.isbn = books.isbn
                LEFT JOIN
                    borrowing ON borrowing.book_ref = books.isbn
                GROUP BY
                    books.isbn;
                                """;

        try (Connection connection = Database.getConnection();
                var preparedStatement = connection.prepareStatement(query);
                var resultSet = preparedStatement.executeQuery(query);) {
            System.out.println("""
                    [+] : Report generated successfully .
                                            """);
            while (resultSet.next()) {
                // print data here
                System.out.println("====================================");
                System.out.println("[ISBN]   : " + resultSet.getString("isbn"));
                System.out.println("[copies] : " + resultSet.getInt("copies"));
                System.out.println("[available] : " + resultSet.getInt("available"));
                System.out.println("[borrowed] : " + resultSet.getInt("borrowed"));
                System.out.println("[lost] : " + resultSet.getInt("lost"));
                System.out.println("====================================");
            }
        } catch (SQLException e) {
        }
    }

    private boolean isValidInput(Book book) {
        return book.getTitle().isEmpty() || book.getTitle().isBlank() ||
                book.getAuthor().isEmpty() || book.getAuthor().isBlank() ||
                book.getIsbn().isEmpty() || book.getIsbn().isBlank();
    }

    private void printBook(Book book) {
        System.out.println("[ISBN]   : " + book.getIsbn());
        System.out.println("[Title]  : " + book.getTitle());
        System.out.println("[Author] : " + book.getAuthor());
        System.out.println("[copies] : " + book.getCopies());
        System.out.println("====================================");
    }
}
