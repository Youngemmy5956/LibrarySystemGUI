package library;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.concurrent.Task;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.List;

public class MainApp extends Application {

    private final Library       library       = new Library("City Central Library");
    private final AIRecommender aiRecommender = new AIRecommender("");

    private static final String BG_DARK    = "#0d0d0d";
    private static final String BG_CARD    = "#1a1a1a";
    private static final String BG_SIDEBAR = "#111111";
    private static final String ACCENT     = "#d4fc3a";
    private static final String TEXT_DIM   = "#888888";
    private static final String SUCCESS    = "#22c55e";
    private static final String ERR        = "#ef4444";
    private static final String WARN       = "#eab308";

    @Override
    public void start(Stage stage) {
        library.seedData();
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG_DARK + ";");
        root.setLeft(buildSidebar(root));
        root.setCenter(buildDashboard());
        stage.setScene(new Scene(root, 1100, 720));
        stage.setTitle("Library Management System");
        stage.setMinWidth(900); stage.setMinHeight(600);
        stage.show();
    }

    private VBox buildSidebar(BorderPane root) {
        VBox sb = new VBox(4);
        sb.setPrefWidth(210);
        sb.setStyle("-fx-background-color:" + BG_SIDEBAR + ";-fx-border-color:#222;-fx-border-width:0 1 0 0;");
        sb.setPadding(new Insets(20, 12, 20, 12));
        Label logo = new Label("  LibraryOS");
        logo.setStyle("-fx-text-fill:" + ACCENT + ";-fx-font-size:17px;-fx-font-weight:bold;");
        logo.setPadding(new Insets(0, 0, 20, 8));
        sb.getChildren().add(logo);
        String[][] nav = {{"Dashboard"},{"Catalog"},{"Members"},{"Check In/Out"},{"AI Assistant"},{"Settings"}};
        for (String[] item : nav) {
            Button btn = navBtn(item[0], false);
            btn.setOnAction(e -> {
                sb.getChildren().stream().filter(n -> n instanceof Button)
                  .forEach(n -> ((Button)n).setStyle(navStyle(false)));
                btn.setStyle(navStyle(true));
                switch (item[0]) {
                    case "Dashboard"    -> root.setCenter(buildDashboard());
                    case "Catalog"      -> root.setCenter(buildCatalog());
                    case "Members"      -> root.setCenter(buildMembers());
                    case "Check In/Out" -> root.setCenter(buildCheckPane());
                    case "AI Assistant" -> root.setCenter(buildAIPane());
                    case "Settings"     -> root.setCenter(buildSettings());
                }
            });
            sb.getChildren().add(btn);
        }
        return sb;
    }

    private Button navBtn(String label, boolean active) {
        Button b = new Button(label); b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT); b.setStyle(navStyle(active)); return b;
    }

    private String navStyle(boolean active) {
        return "-fx-background-color:" + (active ? "rgba(212,252,58,0.12)" : "transparent") + ";"
             + "-fx-text-fill:" + (active ? ACCENT : "#ccc") + ";"
             + "-fx-font-size:13px;-fx-padding:10 14;-fx-border-radius:8;-fx-background-radius:8;-fx-cursor:hand;";
    }

    private VBox buildDashboard() {
        VBox p = page();
        HBox stats = new HBox(16);
        stats.getChildren().addAll(
            stat("Total Items",  String.valueOf(library.getCatalog().size()), ACCENT),
            stat("Available",    String.valueOf(library.getAvailableCount()), SUCCESS),
            stat("Checked Out",  String.valueOf(library.getCheckedOutCount()), WARN),
            stat("Members",      String.valueOf(library.getMembers().size()), "#60a5fa"),
            stat("Total Fines",  String.format("$%.2f", library.getTotalFines()), ERR)
        );
        ListView<String> list = styledList(); list.setPrefHeight(260);
        library.getAllItems().forEach(i -> list.getItems().add(
            String.format("%-10s %-36s %s", "["+i.getItemType()+"]", i.getTitle(),
                i.isAvailable() ? "Available" : "Due: " + i.getDueDate())));
        p.getChildren().addAll(title("Dashboard"), stats, sec("Full Catalog"), list);
        return p;
    }

    private VBox buildCatalog() {
        VBox p = page();
        TextField searchF = field("Search title...");
        Button searchBtn = accentBtn("Search");
        HBox searchRow = new HBox(10, searchF, searchBtn);
        searchF.setPrefWidth(300); searchRow.setAlignment(Pos.CENTER_LEFT);
        TableView<LibraryItem> table = catalogTable();
        table.setItems(FXCollections.observableArrayList(library.getAllItems())); table.setPrefHeight(300);
        searchBtn.setOnAction(e -> {
            String q = searchF.getText().trim();
            table.setItems(FXCollections.observableArrayList(q.isEmpty() ? library.getAllItems() : library.search(q)));
        });
        searchF.setOnAction(e -> searchBtn.fire());
        ComboBox<String> typeBox = combo("Book","DVD","Magazine"); typeBox.setValue("Book");
        TextField id=field("ID"), ttl=field("Title"), v1=field("Author/Director/Publisher"),
                  v2=field("ISBN/Runtime/Issue"), v3=field("Genre/Rating/Month");
        Button addBtn = accentBtn("+ Add");
        addBtn.setOnAction(e -> {
            String type=typeBox.getValue(), sid=id.getText().trim(), st=ttl.getText().trim();
            if (sid.isEmpty()||st.isEmpty()) { alert("ID and Title required."); return; }
            if (library.getItem(sid)!=null)  { alert("ID already exists."); return; }
            LibraryItem item = switch(type) {
                case "DVD"      -> new DVD(sid,st,v1.getText().isBlank()?"Unknown":v1.getText(),
                                      v2.getText().isBlank()?0:Integer.parseInt(v2.getText().replaceAll("[^0-9]","")),
                                      v3.getText().isBlank()?"NR":v3.getText());
                case "Magazine" -> new Magazine(sid,st,v1.getText().isBlank()?"Unknown":v1.getText(),
                                      v2.getText().isBlank()?1:Integer.parseInt(v2.getText().replaceAll("[^0-9]","")),
                                      v3.getText().isBlank()?"N/A":v3.getText());
                default         -> new Book(sid,st,v1.getText().isBlank()?"Unknown":v1.getText(),
                                      v2.getText().isBlank()?"N/A":v2.getText(),
                                      v3.getText().isBlank()?"General":v3.getText());
            };
            library.addItem(item);
            table.setItems(FXCollections.observableArrayList(library.getAllItems()));
            id.clear(); ttl.clear(); v1.clear(); v2.clear(); v3.clear();
        });
        HBox addRow = new HBox(8, typeBox, id, ttl, v1, v2, v3, addBtn);
        addRow.setAlignment(Pos.CENTER_LEFT);
        p.getChildren().addAll(title("Catalog"), searchRow, table, sec("Add New Item"), addRow);
        return p;
    }

    @SuppressWarnings("unchecked")
    private TableView<LibraryItem> catalogTable() {
        TableView<LibraryItem> t = new TableView<>();
        t.setStyle("-fx-background-color:"+BG_CARD+";");
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        t.getColumns().addAll(
            col("Type",    c -> c.getValue().getItemType()),
            col("ID",      c -> c.getValue().getItemId()),
            col("Title",   c -> c.getValue().getTitle()),
            col("Details", c -> c.getValue().getExtraInfo()),
            col("Status",  c -> c.getValue().isAvailable() ? "Available" : "Due: "+c.getValue().getDueDate())
        );
        return t;
    }

    private VBox buildMembers() {
        VBox p = page();
        TableView<Member> table = membersTable();
        table.setItems(FXCollections.observableArrayList(library.getAllMembers())); table.setPrefHeight(260);
        TextField mid=field("ID"), mname=field("Name"), meml=field("Email");
        ComboBox<String> mtype = combo("STANDARD","PREMIUM"); mtype.setValue("STANDARD");
        Button regBtn = accentBtn("Register");
        regBtn.setOnAction(e -> {
            if (mid.getText().isBlank()||mname.getText().isBlank()) { alert("ID and Name required."); return; }
            if (library.getMember(mid.getText().trim())!=null) { alert("ID already exists."); return; }
            library.registerMember(new Member(mid.getText().trim(),mname.getText().trim(),meml.getText().trim(),mtype.getValue()));
            table.setItems(FXCollections.observableArrayList(library.getAllMembers()));
            mid.clear(); mname.clear(); meml.clear();
        });
        HBox formRow = new HBox(8, mid, mname, meml, mtype, regBtn); formRow.setAlignment(Pos.CENTER_LEFT);
        TextArea detail = styledTA(); detail.setEditable(false); detail.setPrefHeight(130);
        table.getSelectionModel().selectedItemProperty().addListener((obs,old,m) -> {
            if (m==null) return;
            StringBuilder sb = new StringBuilder();
            sb.append("ID:      ").append(m.getMemberId()).append("\n");
            sb.append("Name:    ").append(m.getName()).append("\n");
            sb.append("Email:   ").append(m.getEmail()).append("\n");
            sb.append("Type:    ").append(m.getMembershipType()).append(" (max ").append(m.getMaxItems()).append(" items)\n");
            sb.append(String.format("Fines:   $%.2f%n", m.getFinesOwed()));
            m.getBorrowedItems().forEach(i -> sb.append("  -> \"").append(i.getTitle()).append("\" due ").append(i.getDueDate()).append("\n"));
            detail.setText(sb.toString());
        });
        p.getChildren().addAll(title("Members"), table, sec("Register Member"), formRow, sec("Member Details"), detail);
        return p;
    }

    @SuppressWarnings("unchecked")
    private TableView<Member> membersTable() {
        TableView<Member> t = new TableView<>();
        t.setStyle("-fx-background-color:"+BG_CARD+";");
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        t.getColumns().addAll(
            col("ID",    c -> c.getValue().getMemberId()),
            col("Name",  c -> c.getValue().getName()),
            col("Email", c -> c.getValue().getEmail()),
            col("Type",  c -> c.getValue().getMembershipType()),
            col("Fines", c -> String.format("$%.2f", c.getValue().getFinesOwed())),
            col("Out",   c -> String.valueOf(c.getValue().getBorrowedItems().size()))
        );
        return t;
    }

    private VBox buildCheckPane() {
        VBox p = page();
        ComboBox<Member> memberBox = new ComboBox<>();
        ComboBox<LibraryItem> itemBox = new ComboBox<>();
        memberBox.getItems().addAll(library.getAllMembers());
        itemBox.getItems().addAll(library.getAllItems());
        styleCombo(memberBox); styleCombo(itemBox);
        memberBox.setPromptText("Select member..."); memberBox.setPrefWidth(260);
        itemBox.setPromptText("Select item...");     itemBox.setPrefWidth(260);
        Button outBtn = accentBtn("Check Out"), inBtn = plainBtn("Check In");
        Label result = new Label(); result.setStyle("-fx-font-size:13px;-fx-padding:8;"); result.setWrapText(true);
        ListView<String> borrowed = styledList(); borrowed.setPrefHeight(180);

        outBtn.setOnAction(e -> {
            Member m=memberBox.getValue(); LibraryItem i=itemBox.getValue();
            if (m==null||i==null) { res(result,"Select member and item.",false); return; }
            String r = library.checkOut(m.getMemberId(), i.getItemId());
            res(result, r.replace("SUCCESS: ","").replace("ERROR: ",""), r.startsWith("SUCCESS"));
            refresh(memberBox, itemBox, borrowed);
        });
        inBtn.setOnAction(e -> {
            Member m=memberBox.getValue(); LibraryItem i=itemBox.getValue();
            if (m==null||i==null) { res(result,"Select member and item.",false); return; }
            String r = library.checkIn(m.getMemberId(), i.getItemId());
            res(result, r.replace("SUCCESS: ","").replace("LATE: ","").replace("ERROR: ",""),
                r.startsWith("SUCCESS")||r.startsWith("LATE"));
            refresh(memberBox, itemBox, borrowed);
        });
        memberBox.setOnAction(e -> {
            Member m=memberBox.getValue(); if (m==null) return;
            borrowed.getItems().clear();
            m.getBorrowedItems().forEach(i -> borrowed.getItems().add(
                "["+i.getItemType()+"] "+i.getTitle()+" — due "+i.getDueDate()));
        });

        HBox selRow = new HBox(16, vbox(sec("Member"), memberBox), vbox(sec("Item"), itemBox));
        p.getChildren().addAll(title("Check In / Out"), selRow, new HBox(10,outBtn,inBtn), result,
                               sec("Currently Borrowed by Selected Member"), borrowed);
        return p;
    }

    private VBox buildAIPane() {
        VBox p = page();
        Label desc = new Label("Powered by Claude AI — get personalised recommendations or ask any library question.");
        desc.setStyle("-fx-text-fill:"+TEXT_DIM+";-fx-font-size:13px;"); desc.setWrapText(true);
        ComboBox<Member> memberBox = new ComboBox<>();
        memberBox.getItems().addAll(library.getAllMembers()); styleCombo(memberBox);
        memberBox.setPromptText("Select member..."); memberBox.setPrefWidth(240);
        Button recBtn = accentBtn("Get Recommendations");
        TextArea recArea = styledTA(); recArea.setPrefHeight(150); recArea.setEditable(false);
        recArea.setPromptText("AI recommendations will appear here...");
        recBtn.setOnAction(e -> {
            Member m=memberBox.getValue();
            if (m==null) { recArea.setText("Select a member first."); return; }
            if (!aiRecommender.hasApiKey()) { recArea.setText("No API key. Go to Settings."); return; }
            recBtn.setDisable(true); recArea.setText("Thinking...");
            Task<String> t = new Task<>() { protected String call() { return aiRecommender.getRecommendations(m,library.getAllItems()); }};
            t.setOnSucceeded(ev -> { recArea.setText(t.getValue()); recBtn.setDisable(false); });
            t.setOnFailed(ev -> { recArea.setText("Error: "+t.getException().getMessage()); recBtn.setDisable(false); });
            new Thread(t).start();
        });
        TextField qField = field("e.g. What technology books do you have?"); qField.setPrefWidth(400);
        Button askBtn = accentBtn("Ask");
        TextArea ansArea = styledTA(); ansArea.setPrefHeight(130); ansArea.setEditable(false);
        ansArea.setPromptText("Answer will appear here...");
        askBtn.setOnAction(e -> {
            String q=qField.getText().trim(); if (q.isEmpty()) return;
            if (!aiRecommender.hasApiKey()) { ansArea.setText("No API key. Go to Settings."); return; }
            askBtn.setDisable(true); ansArea.setText("Thinking...");
            Task<String> t = new Task<>() { protected String call() { return aiRecommender.askQuestion(q,library.getAllItems()); }};
            t.setOnSucceeded(ev -> { ansArea.setText(t.getValue()); askBtn.setDisable(false); });
            t.setOnFailed(ev -> { ansArea.setText("Error: "+t.getException().getMessage()); askBtn.setDisable(false); });
            new Thread(t).start();
        });
        qField.setOnAction(e -> askBtn.fire());
        p.getChildren().addAll(title("AI Assistant"), desc,
            sec("Personalised Recommendations"), new HBox(10,memberBox,recBtn), recArea,
            sec("Ask the Library AI"), new HBox(10,qField,askBtn), ansArea);
        return p;
    }

    private VBox buildSettings() {
        VBox p = page();
        Label hint = new Label("Get your API key at: https://console.anthropic.com");
        hint.setStyle("-fx-text-fill:"+TEXT_DIM+";-fx-font-size:12px;");
        PasswordField kf = new PasswordField(); kf.setPromptText("sk-ant-...");
        kf.setStyle("-fx-background-color:"+BG_CARD+";-fx-text-fill:white;-fx-border-color:#333;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:10;-fx-font-size:13px;");
        kf.setPrefWidth(420);
        Button save = accentBtn("Save Key"); Label sr = new Label(); sr.setStyle("-fx-font-size:13px;");
        save.setOnAction(e -> {
            String k=kf.getText().trim(); aiRecommender.setApiKey(k);
            if (k.isEmpty()) { sr.setText("Key cleared."); sr.setTextFill(Color.web(WARN)); }
            else { sr.setText("Key saved. AI features are now active."); sr.setTextFill(Color.web(SUCCESS)); }
        });
        VBox rules = card();
        for (String[] r : new String[][]{{"Book","14 days","$0.25/day"},{"DVD","7 days","$1.00/day"},{"Magazine","3 days","$0.10/day"}})
            rules.getChildren().add(ruleRow(r[0],r[1],r[2]));
        VBox mems = card();
        for (String[] r : new String[][]{{"STANDARD","3 items max",""},{"PREMIUM","10 items max",""}})
            mems.getChildren().add(ruleRow(r[0],r[1],r[2]));
        p.getChildren().addAll(title("Settings"), sec("Claude AI API Key"), hint, kf, save, sr,
            sec("Loan Rules"), rules, sec("Membership Limits"), mems);
        return p;
    }

    // ── helpers ──────────────────────────────────────────────
    private VBox page() { VBox v=new VBox(16); v.setPadding(new Insets(32)); return v; }
    private VBox vbox(javafx.scene.Node... nodes) { VBox v=new VBox(4); v.getChildren().addAll(nodes); return v; }

    private Label title(String t) {
        Label l=new Label(t); l.setStyle("-fx-text-fill:white;-fx-font-size:22px;-fx-font-weight:bold;"); return l; }
    private Label sec(String t) {
        Label l=new Label(t); l.setStyle("-fx-text-fill:"+TEXT_DIM+";-fx-font-size:12px;-fx-font-weight:bold;-fx-padding:6 0 2 0;"); return l; }

    private VBox stat(String label, String value, String color) {
        VBox c=new VBox(6); c.setPadding(new Insets(18,22,18,22)); c.setMinWidth(150);
        c.setStyle("-fx-background-color:"+BG_CARD+";-fx-border-color:#2a2a2a;-fx-border-radius:12;-fx-background-radius:12;");
        Label v=new Label(value); v.setStyle("-fx-text-fill:"+color+";-fx-font-size:26px;-fx-font-weight:bold;");
        Label l=new Label(label); l.setStyle("-fx-text-fill:"+TEXT_DIM+";-fx-font-size:12px;");
        c.getChildren().addAll(v,l); HBox.setHgrow(c,Priority.ALWAYS); return c;
    }

    private TextField field(String p) {
        TextField tf=new TextField(); tf.setPromptText(p);
        tf.setStyle("-fx-background-color:"+BG_CARD+";-fx-text-fill:white;-fx-prompt-text-fill:#555;-fx-border-color:#333;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:8 12;-fx-font-size:13px;");
        return tf;
    }
    private TextArea styledTA() {
        TextArea ta=new TextArea();
        ta.setStyle("-fx-background-color:"+BG_CARD+";-fx-text-fill:white;-fx-control-inner-background:"+BG_CARD+";-fx-border-color:#333;-fx-border-radius:8;-fx-background-radius:8;-fx-font-size:13px;");
        ta.setWrapText(true); return ta;
    }
    private Button accentBtn(String t) {
        Button b=new Button(t);
        b.setStyle("-fx-background-color:"+ACCENT+";-fx-text-fill:#111;-fx-font-weight:bold;-fx-font-size:13px;-fx-padding:9 18;-fx-border-radius:8;-fx-background-radius:8;-fx-cursor:hand;");
        return b;
    }
    private Button plainBtn(String t) {
        Button b=new Button(t);
        b.setStyle("-fx-background-color:"+BG_CARD+";-fx-text-fill:#60a5fa;-fx-font-size:13px;-fx-padding:9 18;-fx-border-color:#444;-fx-border-radius:8;-fx-background-radius:8;-fx-cursor:hand;");
        return b;
    }
    private ComboBox<String> combo(String... items) {
        ComboBox<String> cb=new ComboBox<>(); cb.getItems().addAll(items); styleCombo(cb); return cb; }
    private <T> void styleCombo(ComboBox<T> cb) {
        cb.setStyle("-fx-background-color:"+BG_CARD+";-fx-text-fill:white;-fx-border-color:#333;-fx-border-radius:8;-fx-background-radius:8;"); }
    private ListView<String> styledList() {
        ListView<String> lv=new ListView<>();
        lv.setStyle("-fx-background-color:"+BG_CARD+";-fx-border-color:#333;-fx-border-radius:8;-fx-background-radius:8;");
        return lv;
    }
    private <T> TableColumn<T,String> col(String name, java.util.function.Function<TableColumn.CellDataFeatures<T,String>,String> fn) {
        TableColumn<T,String> c=new TableColumn<>(name);
        c.setCellValueFactory(cd -> new SimpleStringProperty(fn.apply(cd))); return c;
    }
    private VBox card() {
        VBox c=new VBox(4); c.setPadding(new Insets(14,18,14,18)); c.setMaxWidth(460);
        c.setStyle("-fx-background-color:"+BG_CARD+";-fx-border-color:#2a2a2a;-fx-border-radius:10;-fx-background-radius:10;"); return c;
    }
    private HBox ruleRow(String n, String v1, String v2) {
        Label ln=new Label(n); ln.setStyle("-fx-text-fill:white;-fx-font-size:13px;"); ln.setPrefWidth(120);
        Label lv=new Label(v1); lv.setStyle("-fx-text-fill:"+ACCENT+";-fx-font-size:13px;"); lv.setPrefWidth(100);
        Label ld=new Label(v2); ld.setStyle("-fx-text-fill:"+TEXT_DIM+";-fx-font-size:13px;");
        HBox row=new HBox(20,ln,lv,ld); row.setPadding(new Insets(6,0,6,0)); return row;
    }
    private void res(Label l, String msg, boolean ok) { l.setText(msg); l.setTextFill(Color.web(ok?SUCCESS:ERR)); }
    private void alert(String msg) { new Alert(Alert.AlertType.WARNING,msg,ButtonType.OK).showAndWait(); }
    private void refresh(ComboBox<Member> mb, ComboBox<LibraryItem> ib, ListView<String> list) {
        Member sm=mb.getValue(); LibraryItem si=ib.getValue();
        mb.getItems().setAll(library.getAllMembers()); ib.getItems().setAll(library.getAllItems());
        if (sm!=null) mb.setValue(library.getMember(sm.getMemberId()));
        if (si!=null) ib.setValue(library.getItem(si.getItemId()));
        if (sm!=null) {
            list.getItems().clear();
            Member m=library.getMember(sm.getMemberId());
            if (m!=null) m.getBorrowedItems().forEach(i -> list.getItems().add("["+i.getItemType()+"] "+i.getTitle()+" — due "+i.getDueDate()));
        }
    }

    public static void main(String[] args) { launch(args); }
}
