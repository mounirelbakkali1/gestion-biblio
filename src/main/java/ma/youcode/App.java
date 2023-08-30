package ma.youcode;


import ma.youcode.menu.MainMenu;
import ma.youcode.services.BookService;

import java.util.Scanner;

public class App
{
    final static String N ="-".repeat(30);
    public static void main( String[] args ){

        showHeader();
        MainMenu menu = new MainMenu();
        BookService bookService = BookService.getInstance();
        menu.showMenu();
        Scanner sc = new Scanner(System.in);
        String choice = "";
        while (choice.isEmpty()){
            System.out.println("enter choice : ");
            choice = sc.nextLine();
            if(!menu.inMenu(choice)) choice = "";
            switch (choice) {
                case "1" -> bookService.findAllBooks();
                case "2" -> bookService.findAvailableBooks();
                case "3" -> bookService.findBorrowedBooks();
                case "4" -> bookService.searchBooks();
                default -> bookService.findAllBooks();
            }
        }
        clearConsole();
        showHeader();
    }
    public static void showHeader(){
        System.out.println(N);
        System.out.println("    Gestion de biblio     ");
        System.out.println(N);
    }
    public final static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            //  Handle any exceptions.
        }
    }
}
