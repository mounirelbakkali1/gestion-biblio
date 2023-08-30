package ma.youcode.entities;

public record Book(String isbn , String title , String author,BookStatus status) {
}
enum BookStatus {
    AVAILABLE,BORROWED
}
