package ma.youcode.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

import ma.youcode.config.Database;

public class Borrowing {
    // composition principle
    private BookCopy copy;
    private Borrower borrower;
    private Date date_borrowing;
    private Date return_date;

    public Borrowing() {
    }

    public Borrowing(int ref, String borrowerName, Date date, Date returnDate) {
        this.copy = new BookCopy();
        this.copy.setRef(ref);
        this.borrower = new Borrower();
        this.borrower.setName(borrowerName);
        this.date_borrowing = date;
        this.return_date = returnDate;
    }

    public void save() {
        String query = "CALL borrowBook(?,?,?,?);";
        try (Connection connection = Database.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, this.copy.getRef());
            preparedStatement.setString(2, this.borrower.getName());
            preparedStatement.setDate(3, this.date_borrowing);
            preparedStatement.setDate(4, this.return_date);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[!] error while borrowing the book (try again)");
        }

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

    public Date getReturn_date() {
        return return_date;
    }

    public void setReturn_date(Date return_date) {
        this.return_date = return_date;
    }

}