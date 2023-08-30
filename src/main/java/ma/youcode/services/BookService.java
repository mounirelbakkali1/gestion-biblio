package ma.youcode.services;

import ma.youcode.entities.Book;

import java.util.List;

public class BookService {

    private static BookService instance ;

    private BookService(){

    }
    public static  BookService getInstance(){
        if (instance == null) {
            instance = new BookService();
        }
        return  instance;
    }

    public List<Book> findAllBooks(){
        return  List.of();
    }
    public void addBook(Book book){

    }
    public void updateBook(Book book){

    }
    public void deleteBook(String isbn){

    }
    public List<Book> findBooks(String keyIsbn){
        return List.of();
    }


    public List<Book> findAvailableBooks() {
        return List.of();
    }

    public List<Book> findBorrowedBooks() {
        return List.of();
    }

    public void searchBooks() {

    }
}
