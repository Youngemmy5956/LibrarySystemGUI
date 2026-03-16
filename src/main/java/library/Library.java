package library;
import java.util.*;
import java.util.stream.Collectors;

public class Library {
    private String name;
    private Map<String, LibraryItem> catalog;
    private Map<String, Member>      members;

    public Library(String name) {
        this.name = name;
        this.catalog = new LinkedHashMap<>();
        this.members = new LinkedHashMap<>();
    }

    public String                   getName()          { return name; }
    public Map<String, LibraryItem> getCatalog()       { return catalog; }
    public Map<String, Member>      getMembers()       { return members; }
    public LibraryItem              getItem(String id) { return catalog.get(id); }
    public Member                   getMember(String id){ return members.get(id); }

    public void addItem(LibraryItem item) { catalog.put(item.getItemId(), item); }
    public void registerMember(Member m)  { members.put(m.getMemberId(), m); }

    public String checkOut(String mid, String iid) {
        Member m = members.get(mid); LibraryItem i = catalog.get(iid);
        if (m==null) return "ERROR: Member not found.";
        if (i==null) return "ERROR: Item not found.";
        return m.borrowItem(i);
    }

    public String checkIn(String mid, String iid) {
        Member m = members.get(mid); LibraryItem i = catalog.get(iid);
        if (m==null) return "ERROR: Member not found.";
        if (i==null) return "ERROR: Item not found.";
        return m.returnItem(i);
    }

    public List<LibraryItem> search(String q) {
        return catalog.values().stream()
            .filter(i -> i.getTitle().toLowerCase().contains(q.toLowerCase()))
            .collect(Collectors.toList());
    }

    public List<LibraryItem> getAllItems()   { return new ArrayList<>(catalog.values()); }
    public List<Member>      getAllMembers() { return new ArrayList<>(members.values()); }
    public long   getAvailableCount()   { return catalog.values().stream().filter(LibraryItem::isAvailable).count(); }
    public long   getCheckedOutCount()  { return catalog.size() - getAvailableCount(); }
    public double getTotalFines()       { return members.values().stream().mapToDouble(Member::getFinesOwed).sum(); }

    public void seedData() {
        addItem(new Book("B001","Clean Code","Robert C. Martin","978-0132350884","Technology"));
        addItem(new Book("B002","The Great Gatsby","F. Scott Fitzgerald","978-0743273565","Classic"));
        addItem(new Book("B003","Atomic Habits","James Clear","978-0735211292","Self-Help"));
        addItem(new Book("B004","Effective Java","Joshua Bloch","978-0134685991","Technology"));
        addItem(new Book("B005","The Pragmatic Programmer","David Thomas","978-0135957059","Technology"));
        addItem(new DVD("D001","Inception","Christopher Nolan",148,"PG-13"));
        addItem(new DVD("D002","The Shawshank Redemption","Frank Darabont",142,"R"));
        addItem(new DVD("D003","Interstellar","Christopher Nolan",169,"PG-13"));
        addItem(new Magazine("M001","National Geographic","Nat Geo Society",312,"March 2025"));
        addItem(new Magazine("M002","Wired","Conde Nast",88,"Jan 2025"));
        addItem(new Magazine("M003","Time","Time USA LLC",44,"Feb 2025"));
        registerMember(new Member("MEM001","Alice Johnson","alice@email.com","PREMIUM"));
        registerMember(new Member("MEM002","Bob Smith","bob@email.com","STANDARD"));
        registerMember(new Member("MEM003","Carol Davis","carol@email.com","STANDARD"));
    }
}
