package ma.youcode.utils;

import java.sql.Date;
import java.util.Scanner;

public class Reader {

    public static String readString(String message, boolean allowEmpty) {
        System.out.println(message);
        Scanner sc = new Scanner(System.in);
        String in = "";
        if (allowEmpty) {
            in = sc.nextLine();
        } else {
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

    public static Date readDate(String message) {
        System.out.println(message);
        Scanner sc = new Scanner(System.in);
        Date in = null;
        boolean invalid = true;
        while (invalid) {
            try {
                in = Date.valueOf(sc.nextLine().trim());
                invalid = false;
            } catch (Exception e) {
                System.out.println("[invalid date format yyyy-mm-dd]");
                continue;
            }
        }
        return in;
    }
}
