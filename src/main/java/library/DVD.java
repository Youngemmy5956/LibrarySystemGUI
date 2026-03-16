package library;

public class DVD extends LibraryItem {
    private String director, rating;
    private int runtimeMinutes;

    public DVD(String itemId, String title, String director, int runtimeMinutes, String rating) {
        super(itemId, title);
        this.director = director; this.runtimeMinutes = runtimeMinutes; this.rating = rating;
    }

    public String getDirector()       { return director; }
    public int    getRuntimeMinutes() { return runtimeMinutes; }
    public String getRating()         { return rating; }

    @Override public String getItemType()       { return "DVD"; }
    @Override public int    getLoanPeriodDays() { return 7; }
    @Override public double getLateFeePerDay()  { return 1.00; }
    @Override public String getExtraInfo()      { return "Director: " + director + " | Runtime: " + runtimeMinutes + " min | Rating: " + rating; }
    @Override public String getDisplayInfo()    { return super.getDisplayInfo() + "\n  " + getExtraInfo(); }
}
