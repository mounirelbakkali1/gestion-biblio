package ma.youcode.utils;

import java.io.IOException;

public class Components {
    final static String N ="-".repeat(30);


    public static void Header(){
        clearConsole();
        System.out.println(N);
        System.out.println("""
                
                     Gestion de biblio
                """);
        System.out.println(N);
    }
    public static void Footer(){
        clearConsole();
        System.out.println(N);
        System.out.println("""
                
                     Exiting
                """);
        System.out.println(N);
    }
    public static void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
