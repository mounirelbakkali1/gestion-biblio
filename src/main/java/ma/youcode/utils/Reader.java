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
        return in;
    }

    public static int readInt(String message, boolean allowZero) {
        System.out.println(message);
        Scanner sc = new Scanner(System.in);
        int in = 0;
        while (in == 0) {
            try {
                in = sc.nextInt();
                if (allowZero && in == 0) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("[retry]");
                readInt(message, allowZero);
            }
        }
        return in;
    }

    public static Date readDate(String message) {
        System.out.println(message);
        Scanner sc = new Scanner(System.in);
        Date in = null;
        while (in == null) {
            try {
                in = Date.valueOf(sc.nextLine());
            } catch (Exception e) {
                System.out.println("[invalid date format yyyy-mm-dd]");
                readDate(message);
            }
        }
        return in;
    }
}
