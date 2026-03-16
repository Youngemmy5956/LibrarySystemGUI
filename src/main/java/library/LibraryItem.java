package library;
import java.time.LocalDate;

public abstract class LibraryItem {
    private String itemId;
    private String title;
    private boolean isAvailable;
    private LocalDate dueDate;

    public LibraryItem(String itemId, String title) {
        this.itemId = itemId; this.title = title;
        this.isAvailable = true; this.dueDate = null;
    }

    public String    getItemId()             { return itemId; }
    public String    getTitle()              { return title; }
    public boolean   isAvailable()           { return isAvailable; }
    public LocalDate getDueDate()            { return dueDate; }
    public void      setAvailable(boolean v) { this.isAvailable = v; }
    public void      setDueDate(LocalDate d) { this.dueDate = d; }

    public abstract String getItemType();
    public abstract int    getLoanPeriodDays();
    public abstract double getLateFeePerDay();
    public abstract String getExtraInfo();

    public String getDisplayInfo() {
        return String.format("[%s] %s — %s", getItemType(), title,
            isAvailable ? "Available" : "Due: " + dueDate);
    }

    @Override public String toString() { return title; }
}
