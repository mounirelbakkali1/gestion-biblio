package ma.youcode.utils;

import java.sql.Date;

import ma.youcode.entities.Book;
import ma.youcode.entities.BookReport;

public class Printer {

    public static void printBook(Book book) {
        System.out.println("[ISBN]   : " + book.getIsbn());
        System.out.println("[Title]  : " + book.getTitle());
        System.out.println("[Author] : " + book.getAuthor().name());
        System.out.println("[copies] : " + book.getCopies());
        System.out.println("====================================");
    }

    public static void printBookReport(BookReport br) {
        System.out.println("====================================");
        System.out.println("[ISBN]   : " + br.getIsbn());
        System.out.println("[Title]  : " + br.getTitle());
        System.out.println("[Author] : " + br.getAuthor().name());
        System.out.println("[copies] : " + br.getCopies());
        System.out.println("[available] : " + br.getAvailable());
        System.out.println("[borrowed] : " + br.getBorrowed());
        System.out.println("[lost] : " + br.getLost());
        System.out.println("====================================");
    }

    public static void printBorrowingDetails(String isbn, String title, String borrowerName, Date borrowingDate,
            Date returnDate) {
        System.out.println("=================Borrowing details===================");
        System.out.println("book isbn : " + isbn);
        System.out.println("book title : " + title);
        System.out.println("borrower name : " + borrowerName);
        System.out.println("borrowing date : " + borrowingDate);
        System.out.println("return date : " + returnDate);
        System.out.println("======================================================");
    }

}
