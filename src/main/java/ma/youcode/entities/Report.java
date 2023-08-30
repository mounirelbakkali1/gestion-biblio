package ma.youcode.entities;

import java.util.*;

public record Report(List<Book> availableBooks,List<Book> borrowedBooks , List<Book> lostBooks) {
}
