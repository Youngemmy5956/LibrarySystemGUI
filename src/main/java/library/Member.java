package library;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Member {
    private String memberId, name, email, membershipType;
    private List<LibraryItem> borrowedItems;
    private double finesOwed;

    public Member(String memberId, String name, String email, String membershipType) {
        this.memberId = memberId; this.name = name;
        this.email = email; this.membershipType = membershipType;
        this.borrowedItems = new ArrayList<>(); this.finesOwed = 0.0;
    }

    public String            getMemberId()       { return memberId; }
    public String            getName()           { return name; }
    public String            getEmail()          { return email; }
    public String            getMembershipType() { return membershipType; }
    public List<LibraryItem> getBorrowedItems()  { return Collections.unmodifiableList(borrowedItems); }
    public double            getFinesOwed()      { return finesOwed; }
    public void              addFine(double a)   { this.finesOwed += a; }
    public void              payFine(double a)   { this.finesOwed = Math.max(0, finesOwed - a); }
    public int               getMaxItems()       { return membershipType.equals("PREMIUM") ? 10 : 3; }

    public String borrowItem(LibraryItem item) {
        if (!item.isAvailable()) return "ERROR: \"" + item.getTitle() + "\" is not available.";
        if (borrowedItems.size() >= getMaxItems()) return "ERROR: Borrow limit reached (" + getMaxItems() + " items).";
        borrowedItems.add(item); item.setAvailable(false);
        item.setDueDate(LocalDate.now().plusDays(item.getLoanPeriodDays()));
        return "SUCCESS: Borrowed \"" + item.getTitle() + "\" — due " + item.getDueDate();
    }

    public String returnItem(LibraryItem item) {
        if (!borrowedItems.contains(item)) return "ERROR: \"" + item.getTitle() + "\" not in your list.";
        String msg;
        if (item.getDueDate() != null && LocalDate.now().isAfter(item.getDueDate())) {
            long days = ChronoUnit.DAYS.between(item.getDueDate(), LocalDate.now());
            double fine = days * item.getLateFeePerDay(); addFine(fine);
            msg = String.format("LATE: %d day(s) late. Fine of $%.2f added.", days, fine);
        } else { msg = "SUCCESS: \"" + item.getTitle() + "\" returned on time."; }
        borrowedItems.remove(item); item.setAvailable(true); item.setDueDate(null);
        return msg;
    }

    @Override public String toString() { return name + " (" + membershipType + ")"; }
}
