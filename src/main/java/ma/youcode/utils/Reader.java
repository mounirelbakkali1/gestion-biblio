package ma.youcode.utils;

import java.sql.Date;
import java.util.Scanner;

import ma.youcode.App;

public class Reader {

    public static String readString(String message, boolean allowEmpty) {
        System.out.println(message);
        Scanner sc = new Scanner(System.in);
        String in = "";
        in = sc.nextLine();
        if (in.trim().equals("-1"))
            App.lunch();
        if (!allowEmpty) {
            while (in.isEmpty()) {
                in = sc.nextLine();
            }
        }
        return in.trim();
    }

    public static int readInt(String message, boolean allowZero) {
        System.out.println(message);

        int in = 0;
        boolean invalid = true;
        while (invalid) {
            Scanner sc = new Scanner(System.in);
            try {
                in = sc.nextInt();
                if ((allowZero && in >= 0) || (!allowZero && in > 0)) {
                    invalid = false;
                } else if (in == -1) {
                    App.lunch();
                } else {
                    System.out.println("[invalid input]");
                }
            } catch (Exception e) {
                System.out.println("[retry]");
                continue;
            }
        }
        return in;
    }

    public static Date readDate(String message, boolean allowEmpty) {
        System.out.println(message);
        Scanner sc = new Scanner(System.in);
        Date in = null;
        boolean invalid = true;
        while (invalid) {
            var inString = sc.nextLine().trim();
            if (inString.equals("-1"))
                App.lunch();
            if (allowEmpty && inString.isEmpty()) {
                var today = java.time.LocalDate.now();
                System.out.println(today);
                return Date.valueOf(today);
            }
            try {
                in = Date.valueOf(inString);
                invalid = false;
            } catch (Exception e) {
                System.out.println("[invalid date format yyyy-mm-dd]");
                continue;
            }
        }
        return in;
    }
}
