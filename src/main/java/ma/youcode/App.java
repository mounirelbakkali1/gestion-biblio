package ma.youcode;


import ma.youcode.entities.Book;
import ma.youcode.menu.MainMenu;
import ma.youcode.services.BookService;
import ma.youcode.utils.Components;

import java.util.*;

public class App
{
    public static void main( String[] args ){
        lunch();
    }

    public static void lunch(){
        Components.Header();
        MainMenu menu = new MainMenu();
        BookService bookService = BookService.getInstance();
        menu.showMenu();
        Scanner sc = new Scanner(System.in);
        String choice = "";
        while (choice.isEmpty()){
            System.out.println("enter choice : ");
            choice = sc.nextLine();
            if(!menu.inMenu(choice)) choice = "";
            try{
                switch (choice) {
                    case "1" -> bookService.findAllBooks();
                    case "2" -> bookService.findAvailableBooks();
                    case "3" -> bookService.findBorrowedBooks();
                    case "4" -> bookService.searchBooks();
                    case "5" -> bookService.addBook();
                    case "6" -> bookService.updateBook();
                    case "7" -> bookService.deleteBook();
                    case "8" -> bookService.generateReport();
                    default -> System.out.println("[retry]");
                }
            }finally {
                System.out.println("[enter] return ");
                choice = sc.nextLine();
                if(choice.equals("")) lunch();
            }
        }
        Components.Footer();
    }

}
