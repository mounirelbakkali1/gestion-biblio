package ma.youcode;

import ma.youcode.menu.MainMenu;
import ma.youcode.services.BookService;
import ma.youcode.services.BorrowingService;
import ma.youcode.utils.Components;
import ma.youcode.utils.Reader;

public class App {
    public static void main(String[] args) {
        lunch();
    }

    public static void lunch() {
        Components.Header();
        MainMenu menu = new MainMenu();
        BookService bookService = BookService.getInstance();
        menu.showMenu();
        String choice = "";
        while (choice.isEmpty()) {
            choice = Reader.readString("enter choice : ", false);
            if (!menu.inMenu(choice))
                choice = "";
            try {
                switch (choice) {
                    case "1" -> bookService.showBooks();
                    case "2" -> bookService.showStatistics();
                    case "3" -> BorrowingService.showMenu();
                    case "4" -> bookService.searchBooks();
                    case "5" -> bookService.addBook();
                    case "6" -> bookService.updateBook();
                    case "7" -> bookService.deleteBook();
                    case "8" -> bookService.showStatistics();
                    default -> System.out.println("[retry]");
                }
            } finally {
                choice = Reader.readString("[enter] return ", true);
                if (choice.equals(""))
                    lunch();
            }

        }
        Components.Footer();
    }

}
