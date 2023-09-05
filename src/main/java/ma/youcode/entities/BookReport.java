package ma.youcode.entities;

public class BookReport extends Book {
    private int available;
    private int borrowed;
    private int lost;

    public BookReport(String isbn, String title, Author author, int available, int borrowed, int lost) {
        super(isbn, title, author);
        this.available = available;
        this.borrowed = borrowed;
        this.lost = lost;
    }

    public BookReport(int available, int borrowed, int lost) {
        this.available = available;
        this.borrowed = borrowed;
        this.lost = lost;
    }

    public BookReport() {

    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getBorrowed() {
        return borrowed;
    }

    public void setBorrowed(int borrowed) {
        this.borrowed = borrowed;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }
}
