package ma.youcode.services;

import java.sql.Date;
import java.util.Optional;
import java.util.Scanner;

import ma.youcode.entities.Book;
import ma.youcode.entities.Borrowing;

public class BorrowingService {

    private static final Scanner sc = new Scanner(System.in);
    private final BookService bookService = BookService.getInstance();

    public void borrowBook() {
        // get book isbn
        System.out.println("enter book isbn : ");
        String isbn = sc.nextLine();
        // check
        Optional<Book> optionalBook = bookService.findBookByIsbn(isbn);
        if (optionalBook.isEmpty()) {
            System.out.println("book not found");
            borrowBook();
        }
        boolean couldBeBorrowed = bookService.generateReport().stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .anyMatch(br -> br.getAvailable() > 1);
        if (!couldBeBorrowed) {
            System.out.println("book not available [all copies are borrowed]");
            borrowBook();
        }
        // get borrower name
        System.out.println("enter borrower name : ");
        String borrowerName = sc.nextLine();
        // get borrowing date
        System.out.println("enter borrower date [yyyy-MM-dd]: ");
        String borrowerDate = sc.nextLine();
        Date date = Date.valueOf(borrowerDate);
        // get borrowing duration
        System.out.println("enter borrower duration [days]: ");
        int duration = sc.nextInt();
        Date returnDate = Date.valueOf(date.toLocalDate().plusDays(duration));
        // print borrowing details
        System.out.println("=================Borrowing details===================");
        System.out.println("book isbn : " + isbn);
        System.out.println("book title : " + optionalBook.get().getTitle());
        System.out.println("borrower name : " + borrowerName);
        System.out.println("borrowing date : " + date);
        System.out.println("return date : " + returnDate);
        System.out.println("======================================================");
        // confirm
        System.out.println("confirm [y/n] : ");
        String confirm = sc.nextLine();
        if (confirm.equals("y")) {
            // save borrowing
            Borrowing borrowing = new Borrowing(isbn, borrowerName, date, returnDate);
            borrowing.save();
            System.out.println("[+] book borrowed successfully");
        } else {
            System.out.println("book not borrowed");
        }
    }

    public void returnBook() {

    }

}
