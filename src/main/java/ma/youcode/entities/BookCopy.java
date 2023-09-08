package ma.youcode.entities;

public class BookCopy extends Book {
    int ref;
    BookStatus status;

    public BookCopy(int ref, String isbn, String title, Author author, BookStatus status) {
        super(isbn, title, author);
        this.ref = ref;
        this.status = status;
    }

    public BookCopy(String isbn, String title, Author author, BookStatus status) {
        super(isbn, title, author);
        this.status = status;
    }

    public BookCopy(String isbn, String title) {
        super(isbn, title);
    }

    public BookCopy() {
    }

    public int getRef() {
        return ref;
    }

    public void setRef(int ref) {
        this.ref = ref;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }
}
