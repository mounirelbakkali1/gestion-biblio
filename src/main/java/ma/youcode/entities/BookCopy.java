package ma.youcode.entities;

public class BookCopy extends Book{
    int ref ;
    BookStatus status;
    BookCopy(int ref , String isbn, String title, String author, BookStatus status) {
        super(isbn, title, author);
        this.ref = ref ;
        this.status = status;
    }
    BookCopy(String isbn, String title, String author, BookStatus status) {
        super(isbn, title, author);
        this.status = status;
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
