package ma.youcode.entities;

import java.util.*;

public record Report(List<? extends Book> availableBooks, List<? extends Book> borrowedBooks,
        List<? extends Book> lostBooks) {
}
