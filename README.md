# 📚 Library Management System — JavaFX GUI + AI

A fully featured desktop Library Management System built in Java.
It demonstrates all six OOP principles, has a real graphical user interface (GUI)
built with JavaFX, and uses the Claude AI API to give personalised book recommendations
and answer questions about the library catalog.

---

## 📸 What the App Looks Like

The app opens as a desktop window with a dark sidebar and 6 screens:

| Screen | What it does |
|--------|-------------|
| **Dashboard** | Overview — total items, available, checked out, members, fines |
| **Catalog** | Browse, search, filter, and add new Books / DVDs / Magazines |
| **Members** | View all members, see their borrowed items and fines, register new members |
| **Check In/Out** | Borrow and return items, see late fee warnings |
| **AI Assistant** | Get AI-powered recommendations and ask the library anything |
| **Settings** | Enter your Claude API key to activate AI features |

---

## ✅ Requirements — Install These First

Before you can run the project you need two things installed on your machine.

### 1. Java JDK 21
This is the programming language the app is written in.

Check if you already have it:
```bash
java -version
```
If it says `openjdk 21` or higher you are good. If not, install it:

| Your OS | Command |
|---------|---------|
| macOS   | `brew install openjdk@21` |
| Ubuntu  | `sudo apt install openjdk-21-jdk` |
| Windows | Download from https://adoptium.net and run the installer |

### 2. Apache Maven
Maven is the build tool that downloads JavaFX and compiles the project for you.

Check if you already have it:
```bash
mvn -version
```
If not, install it:

| Your OS | Command |
|---------|---------|
| macOS   | `brew install maven` |
| Ubuntu  | `sudo apt install maven` |
| Windows | Download from https://maven.apache.org/download.cgi and follow the install guide |

---

## 🚀 How to Run the Project

### Step 1 — Clone or download the project

If you have Git:
```bash
git clone <your-repo-url>
cd LibrarySystemGUI
```

Or if you downloaded a ZIP file, unzip it and open your terminal inside the folder.

### Step 2 — Run the app

```bash
mvn javafx:run
```

That's it. Maven will automatically:
- Download JavaFX (only on the first run, takes ~30 seconds)
- Compile all the Java source files
- Launch the desktop window

> **Note:** The first time you run it will be slower because it downloads
> dependencies. Every run after that is fast.

---

## 🤖 Setting Up AI Features

The AI features use the Claude API by Anthropic. You need an API key and a small
amount of credits ($5 is more than enough for testing).

### Step 1 — Get your API key
1. Go to **https://console.anthropic.com**
2. Create a free account
3. Go to **API Keys** and click **Create Key**
4. Copy the key — it starts with `sk-ant-...`

### Step 2 — Add credits
The API is not free but is very cheap.
1. In console.anthropic.com go to **Plans & Billing**
2. Add $5 — this is enough for hundreds of AI requests

### Step 3 — Enter the key in the app
1. Open the app with `mvn javafx:run`
2. Click **Settings** in the left sidebar
3. Paste your API key in the field
4. Click **Save Key**
5. You will see "AI features are now active"

---

## 💡 How to Use the AI Assistant

Once your API key is saved, go to the **AI Assistant** screen.

**Personalised Recommendations:**
1. Select a member from the dropdown (e.g. Alice Johnson)
2. Click **Get Recommendations**
3. The AI looks at their borrowing history and suggests items they might enjoy

> Tip: First borrow some items for a member using Check In/Out, then come back
> to AI Assistant — the recommendations get smarter the more history a member has.

**Ask the Library AI anything:**
Try questions like:
```
What technology books do you have?
```
```
Which DVD has the longest runtime?
```
```
What should I read if I like self-help?
```
```
Which items are best for someone who loves Christopher Nolan?
```
```
What is the cheapest item to borrow based on late fees?
```

---

## 📂 Project Structure

```
LibrarySystemGUI/
├── pom.xml                          # Maven config — manages dependencies
└── src/
    └── main/
        └── java/
            └── library/
                ├── LibraryItem.java  # Abstract base class (Abstraction)
                ├── Book.java         # Extends LibraryItem (Inheritance)
                ├── DVD.java          # Extends LibraryItem (Inheritance)
                ├── Magazine.java     # Extends LibraryItem (Inheritance)
                ├── Member.java       # Member class (Encapsulation)
                ├── Library.java      # Core system logic (Methods)
                ├── AIRecommender.java# Claude AI integration (AI Usability)
                └── MainApp.java      # JavaFX GUI entry point (GUI)
```

---

## 🧱 OOP Principles — Where to Find Them

This project was built to demonstrate all six OOP principles clearly.

### 1. Classes
Every component is its own class with fields, a constructor, and methods.
`Library`, `Member`, `Book`, `DVD`, `Magazine`, `AIRecommender`, `MainApp`.

### 2. Inheritance
`Book`, `DVD`, and `Magazine` all **extend** `LibraryItem`.
They inherit the common fields (`itemId`, `title`, `isAvailable`, `dueDate`)
and each adds its own specific fields on top.
```java
public class Book     extends LibraryItem { ... }
public class DVD      extends LibraryItem { ... }
public class Magazine extends LibraryItem { ... }
```

### 3. Abstraction
`LibraryItem` is an **abstract class** — you cannot create a plain `LibraryItem`
directly. It defines three abstract methods that every subclass is forced to implement:
```java
public abstract String getItemType();
public abstract int    getLoanPeriodDays();
public abstract double getLateFeePerDay();
```
This means every item type defines its own loan period and late fee rules.

### 4. Polymorphism
The same method call behaves differently depending on the actual object type.
In `Library.java`, `getAllItems()` returns a `List<LibraryItem>` — but when the
GUI calls `getDisplayInfo()` on each item, each one produces different output
because `Book`, `DVD`, and `Magazine` each override that method.
```java
for (LibraryItem item : library.getAllItems())
    System.out.println(item.getDisplayInfo()); // different output each time
```

### 5. Encapsulation
Every field across every class is `private`. Nothing can be accessed or changed
directly from outside. For example in `Member.java`, the fines balance is private
and can only change through controlled methods:
```java
private double finesOwed;

public void addFine(double amt) { this.finesOwed += amt; }
public void payFine(double amt) { this.finesOwed = Math.max(0, finesOwed - amt); }
```

### 6. Methods
The entire system runs through methods — `borrowItem()`, `returnItem()`,
`checkOut()`, `checkIn()`, `search()`, `getRecommendations()`, `askQuestion()`,
and many more. Each method does one clear job.

---

## 💰 Loan Rules

| Item Type | Loan Period | Late Fee Per Day |
|-----------|------------|-----------------|
| Book      | 14 days    | $0.25           |
| DVD       | 7 days     | $1.00           |
| Magazine  | 3 days     | $0.10           |

---

## 👥 Membership Types

| Type     | Max Items at Once |
|----------|------------------|
| STANDARD | 3 items          |
| PREMIUM  | 10 items         |

---

## 🛠 Common Issues

**`mvn: command not found`**
Maven is not installed. See the Requirements section above.

**`java: command not found`**
Java is not installed. See the Requirements section above.

**App window does not open**
Make sure you are running `mvn javafx:run` from inside the `LibrarySystemGUI` folder.
```bash
cd ~/LibrarySystemGUI
mvn javafx:run
```

**AI error 401**
Your API key is wrong or expired. Go to console.anthropic.com, generate a new key,
and paste it in the app Settings screen.

**AI error — credit balance too low**
Your Anthropic account has no credits. Go to console.anthropic.com → Plans & Billing
and add at least $5.

**First run is very slow**
Normal — Maven is downloading JavaFX. Subsequent runs are fast.

---

## 🔧 To Open and Edit in VS Code

```bash
code ~/LibrarySystemGUI
```

To run from the VS Code terminal:
```bash
mvn javafx:run
```

---

## 👨‍💻 Built With

- **Java 21** — core language
- **JavaFX 21** — desktop GUI framework
- **Apache Maven** — build and dependency management
- **Claude AI (Anthropic)** — AI recommendations and Q&A via REST API

---

## 📄 License

This project was built as an academic demonstration of Java OOP principles
with GUI and AI integration.