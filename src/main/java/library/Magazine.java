package library;

public class Magazine extends LibraryItem {
    private String publisher, month;
    private int issueNumber;

    public Magazine(String itemId, String title, String publisher, int issueNumber, String month) {
        super(itemId, title);
        this.publisher = publisher; this.issueNumber = issueNumber; this.month = month;
    }

    public String getPublisher()   { return publisher; }
    public int    getIssueNumber() { return issueNumber; }
    public String getMonth()       { return month; }

    @Override public String getItemType()       { return "MAGAZINE"; }
    @Override public int    getLoanPeriodDays() { return 3; }
    @Override public double getLateFeePerDay()  { return 0.10; }
    @Override public String getExtraInfo()      { return "Publisher: " + publisher + " | Issue: #" + issueNumber + " | Month: " + month; }
    @Override public String getDisplayInfo()    { return super.getDisplayInfo() + "\n  " + getExtraInfo(); }
}
