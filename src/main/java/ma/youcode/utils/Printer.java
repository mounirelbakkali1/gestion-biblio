package ma.youcode.utils;

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
}
