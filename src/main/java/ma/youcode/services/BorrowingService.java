package ma.youcode.services;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import ma.youcode.config.Database;
import ma.youcode.entities.Book;
import ma.youcode.entities.BookReport;
import ma.youcode.entities.Borrowing;
import ma.youcode.utils.Components;
import ma.youcode.utils.Printer;
import ma.youcode.utils.Reader;

public class BorrowingService {
    private static final BorrowingService instance = new BorrowingService();
    private static String ERROR_MESSAGE = null;
    private final BookService bookService = BookService.getInstance();

    public static BorrowingService getInstance() {
        return instance;
    }

    private BorrowingService() {
    }

    public static void showMenu() {
        Components.clearConsole();
        System.out.println("-----------------Borrowing menu-----------------");
        System.out.println("1. Borrow book");
        System.out.println("2. Return book");
        System.out.println("3. Back");
        System.out.println("-------------------------------------------------");
        String choice = Reader.readString("_", false);
        switch (choice) {
            case "1" -> instance.borrowBook();
            case "2" -> instance.returnBook();
            case "3" -> {
                System.out.println("back");
                return;
            }
            default -> System.out.println("[retry]");
        }

    }

    public void borrowBook() {
        if (ERROR_MESSAGE != null) {
            System.out.println(ERROR_MESSAGE);
            ERROR_MESSAGE = null;
        }
        String isbn = Reader.readString("enter book isbn : ", false);
        // check
        Optional<Book> optionalBook = bookService.findBookByIsbn(isbn);
        if (optionalBook.isEmpty()) {
            ERROR_MESSAGE = "[!] book not found";
            borrowBook();
        }
        List<BookReport> report = bookService.generateReport();
        boolean couldBeBorrowed = report.stream()
                .anyMatch(br -> br.getIsbn().equals(isbn) && br.getAvailable() > 0);
        if (!couldBeBorrowed) {
            ERROR_MESSAGE = "[!] book not available [all copies are borrowed]";
            borrowBook();
        }
        String borrowerName = Reader.readString("enter borrower name : ", false);
        Date borrowingDate = Reader.readDate("enter borrowing date [yyyy-MM-dd]: ");
        int duration = Reader.readInt("enter borrowing duration [days] : ", false);
        Date returnDate = Date.valueOf(borrowingDate.toLocalDate().plusDays(duration));
        Printer.printBorrowingDetails(isbn, optionalBook.get().getTitle(), borrowerName, borrowingDate, returnDate);
        String confirm = Reader.readString("confirm borrowing [y/n] : ", false);
        if (confirm.equals("y")) {
            int random = getRandomBookRef(isbn);
            if (random == 0) {
                ERROR_MESSAGE = "[!] error while borrowing the book (try again)";
                borrowBook();
            }
            Borrowing borrowing = new Borrowing(random, borrowerName, borrowingDate, returnDate);
            borrowing.save();
            System.out.println("[+] book borrowed successfully");
        } else {
            ERROR_MESSAGE = "[!] error while borrowing the book (try again)";
            borrowBook();
        }
    }

    private int getRandomBookRef(String isbn) {
        String query = "call randomAvailableCopy(?)";
        int random = 0;
        try (var connection = Database.getConnection();
                var preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, isbn);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                random = resultSet.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("[!] error while returning the book copy (try again)");
        }
        return random;
    }

    public void returnBook() {
        if (ERROR_MESSAGE != null) {
            System.out.println(ERROR_MESSAGE);
            ERROR_MESSAGE = null;
        }
        String isbn = Reader.readString("enter book isbn : ", false);
        // check
        Optional<Book> optionalBook = bookService.findBookByIsbn(isbn);
        if (optionalBook.isEmpty()) {
            ERROR_MESSAGE = "[!] book not found";
            returnBook();
        }
        String borrowerName = Reader.readString("enter borrower name : ", false);
        int ref = getBorrowingDetails(isbn, borrowerName);
        if (ref == 0) {
            ERROR_MESSAGE = "[!] borrowing details with [isbn : " + isbn + ", borrower name : " + borrowerName
                    + "] not found";
            returnBook();
        }
        String confirm = Reader.readString("confirm returning [y/n] : ", false);
        if (confirm.toLowerCase().equals("y")) {
            String query = "call returnBook(?)";
            try (var connection = Database.getConnection();
                    var preparedStatement = connection.prepareStatement(query);) {
                preparedStatement.setInt(1, ref);
                preparedStatement.executeUpdate();
                System.out.println("[+] book returned successfully");
            } catch (Exception e) {
                System.out.println("[!] error while returning the book copy (try again)");
            }
        } else {
            ERROR_MESSAGE = "[!] error while returning the book copy (try again)";
            returnBook();
        }

    }

    private int getBorrowingDetails(String isbn, String borrowerName) {
        int ref = 0;
        String query = "call getBorrowingDetails(?,?)";
        try (var connection = Database.getConnection();
                var preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, isbn);
            preparedStatement.setString(2, borrowerName);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ref = resultSet.getInt("book_ref");
                String title = resultSet.getString("title");
                Date borrowingDate = resultSet.getDate("borrowing_date");
                Date returnDate = resultSet.getDate("return_date");
                Printer.printBorrowingDetails(isbn, title, borrowerName, borrowingDate, returnDate);
            }
        } catch (Exception e) {
            System.out.println("[!] error while returning the book copy (try again)");
        }
        return ref;
    }

}
