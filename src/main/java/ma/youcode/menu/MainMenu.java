package ma.youcode.menu;

import java.util.HashMap;
import java.util.Map;

public class MainMenu {
    private static Map<String, String> options = new HashMap<>();

    public void showMenu() {
        for (Map.Entry<String, String> option : options.entrySet()) {
            System.out.println(option.getKey() + " : \t" + option.getValue());
        }
    }

    public boolean inMenu(String key) {
        return options.containsKey(key);
    }

    public MainMenu() {
        options.put("1", "show books");
        options.put("2", "get statistics");
        options.put("3", "borrowing service");
        options.put("4", "search books");
        options.put("5", "add book");
        options.put("6", "update book");
        options.put("7", "delete book");
        options.put("9", "exit");
    }
}
