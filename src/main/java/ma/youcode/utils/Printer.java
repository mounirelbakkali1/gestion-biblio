package ma.youcode.utils;

import java.sql.Date;
import java.util.ArrayList;

import ma.youcode.entities.Book;
import ma.youcode.entities.BookReport;
import ma.youcode.entities.Borrowing;

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

    public static void printBorrowingDetails(String isbn, String title, String numMember, Date borrowingDate,
            Date returnDate) {
        System.out.println("=================Borrowing details===================");
        System.out.println("book isbn      : " + isbn);
        System.out.println("book title     : " + title);
        System.out.println("memeber number : " + numMember);
        System.out.println("borrowing date : " + borrowingDate);
        System.out.println("return date    : " + returnDate);
        System.out.println("======================================================");
    }

    public static void printBorrowedBooks(ArrayList<Borrowing> list) {
        list.forEach(borrowing -> {
            var bd = borrowing.getDate_borrowing();
            var rd = borrowing.getReturn_date();
            boolean isNotReturned = rd.compareTo(Date.valueOf(java.time.LocalDate.now())) < 0;
            System.out.println("isbn           : " + borrowing.getCopy().getIsbn());
            System.out.println("title          : " + borrowing.getCopy().getTitle());
            System.out.println("borrower       : " + borrowing.getBorrower().getName());
            System.out.println("memeber num    : " + borrowing.getBorrower().getNumMember());
            System.out.println("borrowing date : " + bd);
            System.out.println(
                    "return date    : " + rd + (isNotReturned ? "      [!] not returned" : ""));
            System.out.println("====================================");
        });
    }

}
