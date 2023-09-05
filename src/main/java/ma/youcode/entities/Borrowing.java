package ma.youcode.entities;

import java.util.Date;

public class Borrowing {
    // composition principle
    private BookCopy copy ;
    private Borrower borrower;
    private Date date_borrowing;
    private int duration_of_borrowing_in_days;


    public Borrowing() {
    }
    public Borrowing(String isbn, String borrowerName, Date date, Date returnDate) {
    }


    public void save(){

    }

    public BookCopy getCopy() {
        return copy;
    }

    public void setCopy(BookCopy copy) {
        this.copy = copy;
    }

    public Borrower getBorrower() {
        return borrower;
    }

    public void setBorrower(Borrower borrower) {
        this.borrower = borrower;
    }

    public Date getDate_borrowing() {
        return date_borrowing;
    }

    public void setDate_borrowing(Date date_borrowing) {
        this.date_borrowing = date_borrowing;
    }

    public int getDuration_of_borrowing_in_days() {
        return duration_of_borrowing_in_days;
    }

    public void setDuration_of_borrowing_in_days(int duration_of_borrowing_in_days) {
        this.duration_of_borrowing_in_days = duration_of_borrowing_in_days;
    }
}
