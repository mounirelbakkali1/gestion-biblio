package ma.youcode.entities;

public class Borrower {
    private String name;
    private String numMember;

    public Borrower(String name, String numMember) {
        this.name = name;
        this.numMember = numMember;
    }

    public Borrower() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumMember() {
        return numMember;
    }

    public void setNumMember(String numMember) {
        this.numMember = numMember;
    }
}
