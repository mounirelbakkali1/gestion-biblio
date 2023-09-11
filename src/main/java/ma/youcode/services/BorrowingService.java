package ma.youcode.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import ma.youcode.config.Database;
import ma.youcode.entities.Book;
import ma.youcode.entities.BookCopy;
import ma.youcode.entities.BookReport;
import ma.youcode.entities.Borrower;
import ma.youcode.entities.Borrowing;
import ma.youcode.utils.BookFactory;
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
        System.out.println("3. Show borrowed books");
        System.out.println("4. Back");
        System.out.println("-------------------------------------------------");
        String choice = Reader.readString("_", false);
        switch (choice) {
            case "1" -> instance.borrowBook();
            case "2" -> instance.returnBook();
            case "3" -> instance.showBorrowedBooks();
            case "4" -> {
                System.out.println("back");
                return;
            }
            default -> System.out.println("[retry]");
        }

    }

    private void showBorrowedBooks() {
        if (ERROR_MESSAGE != null) {
            System.out.println(ERROR_MESSAGE);
            ERROR_MESSAGE = null;
        }
        String query = "CALL getBorrowingBooks()";
        try (var connection = Database.getConnection();
                var preparedStatement = connection.prepareStatement(query);) {
            var resultSet = preparedStatement.executeQuery();
            var list = new ArrayList<Borrowing>();
            while (resultSet.next()) {
                var borrowing = new Borrowing();
                borrowing.setCopy(new BookCopy(resultSet.getString("isbn"), resultSet.getString("title")));
                borrowing.setBorrower(new Borrower(resultSet.getString("borrower"), resultSet.getString("numMember")));
                borrowing.setDate_borrowing(resultSet.getDate("borrowing_date"));
                borrowing.setReturn_date(resultSet.getDate("return_date"));
                list.add(borrowing);
            }
            System.out.println("====================================");
            System.out.println("List of borrowed books : ");
            Printer.printBorrowedBooks(list);
        } catch (Exception e) {
            ERROR_MESSAGE = "[!] error while getting borrowed books (try again)";
            showBorrowedBooks();
        }

    }

    public void borrowBook() {
        while (true) {
            if (ERROR_MESSAGE != null) {
                System.out.println(ERROR_MESSAGE);
                ERROR_MESSAGE = null;
            }
            String isbn = Reader.readString("enter book isbn : ", false);
            // check
            Optional<Book> optionalBook = bookService.findBookByIsbn(isbn);
            if (optionalBook.isEmpty()) {
                ERROR_MESSAGE = "[!] book not found";
                continue;
            } else {
                if (!bookService.couldBeBorrowed(optionalBook.get().getIsbn())) {
                    ERROR_MESSAGE = "[!] book not available [all copies are borrowed]";
                    continue;
                }
            }
            String memberNum = Reader.readString("enter member num : ", false);
            String borrowerName = Reader.readString("enter borrower name [if new]: ", true);
            var br = new Borrower(borrowerName, memberNum);
            Date borrowingDate = Reader
                    .readDate("enter borrowing date [" + Date.valueOf(java.time.LocalDate.now()) + "]: ", true);
            int duration = Reader.readInt("enter borrowing duration [days] : ", false);
            Date returnDate = Date.valueOf(borrowingDate.toLocalDate().plusDays(duration));
            Printer.printBorrowingDetails(isbn, optionalBook.get().getTitle(), memberNum, borrowingDate, returnDate);
            String confirm = Reader.readString("confirm borrowing [y/n] : ", false);
            if (confirm.equals("y")) {
                int random = getRandomBookRef(isbn);
                if (random == 0) {
                    ERROR_MESSAGE = "[!] error while borrowing the book (try again)";
                    continue;
                }
                Borrowing borrowing = new Borrowing(random, br, borrowingDate, returnDate);
                try {
                    borrowing.save();
                } catch (Exception e) {
                    ERROR_MESSAGE = "[!] error while borrowing the book (try again)";
                    continue;
                }
                System.out.println("[+] book borrowed successfully");
                break;
            } else {
                ERROR_MESSAGE = "[!] error while borrowing the book (try again)";
                continue;
            }
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
        while (true) {
            if (ERROR_MESSAGE != null) {
                System.out.println(ERROR_MESSAGE);
                ERROR_MESSAGE = null;
            }
            String isbn = Reader.readString("enter book isbn : ", false);
            // check
            Optional<Book> optionalBook = bookService.findBookByIsbn(isbn);
            if (optionalBook.isEmpty()) {
                ERROR_MESSAGE = "[!] book not found";
                continue;
            }
            String memberNum = Reader.readString("enter member num : ", false);
            int ref = getBorrowingDetails(isbn, memberNum);
            if (ref == 0) {
                ERROR_MESSAGE = "[!] borrowing details with [isbn : " + isbn + ", memeber Number : " + memberNum
                        + "] not found";
                continue;
            }
            String confirm = Reader.readString("confirm returning [y/n] : ", false);
            if (confirm.toLowerCase().equals("y")) {
                String query = "call returnBook(?)";
                try (var connection = Database.getConnection();
                        var preparedStatement = connection.prepareStatement(query);) {
                    preparedStatement.setInt(1, ref);
                    preparedStatement.executeUpdate();
                    System.out.println("[+] book returned successfully");
                    break;
                } catch (Exception e) {
                    System.out.println("[!] error while returning the book copy (try again)");
                    continue;
                }
            } else {
                ERROR_MESSAGE = "[!] error while returning the book copy (try again)";
                continue;
            }
        }

    }

    private int getBorrowingDetails(String isbn, String numMemeber) {
        int ref = 0;
        String query = "call getBorrowingDetails(?,?)";
        try (var connection = Database.getConnection();
                var preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, isbn);
            preparedStatement.setString(2, numMemeber);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ref = resultSet.getInt("book_ref");
                String title = resultSet.getString("title");
                Date borrowingDate = resultSet.getDate("borrowing_date");
                Date returnDate = resultSet.getDate("return_date");
                Printer.printBorrowingDetails(isbn, title, numMemeber, borrowingDate, returnDate);
            }
        } catch (Exception e) {
            System.out.println("[!] error while returning the book copy (try again)");
        }
        return ref;
    }

}
