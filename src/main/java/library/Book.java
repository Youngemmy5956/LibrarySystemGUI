package library;

public class Book extends LibraryItem {
    private String author, isbn, genre;

    public Book(String itemId, String title, String author, String isbn, String genre) {
        super(itemId, title);
        this.author = author; this.isbn = isbn; this.genre = genre;
    }

    public String getAuthor() { return author; }
    public String getIsbn()   { return isbn; }
    public String getGenre()  { return genre; }

    @Override public String getItemType()       { return "BOOK"; }
    @Override public int    getLoanPeriodDays() { return 14; }
    @Override public double getLateFeePerDay()  { return 0.25; }
    @Override public String getExtraInfo()      { return "Author: " + author + " | ISBN: " + isbn + " | Genre: " + genre; }
    @Override public String getDisplayInfo()    { return super.getDisplayInfo() + "\n  " + getExtraInfo(); }
}
