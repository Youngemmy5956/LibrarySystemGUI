# 📚 Library Management System — JavaFX GUI + AI

A fully featured desktop Library Management System built in Java.
It demonstrates all six OOP principles, has a real graphical user interface
built with JavaFX, and uses a free AI model via OpenRouter to give personalised
book recommendations and answer questions about the library catalog.

---

## 👨‍🎓 Project Team

| Name | Matric Number |
|------|--------------|
| AMOO Azeezat Oluwakemi | 24110591412 |
| Adebayo Johnson Olorunfemi | 23110591377 |
| Alex Elisha Stephen | 23110591271 |
| Majoyegbe Israel Afolabi | 23110591135 |
| Emmanuel Chukwudi Anene | 23110591276 |
| SHITTU Rukayat Iretomiwa | 23110591217 |
| Adeleke Oriyomi Oluwapelumi | 23110651019 |

---

## 📸 What the App Looks Like

The app opens as a desktop window with a dark sidebar and 6 screens:

| Screen | What it does |
|--------|-------------|
| **Dashboard** | Overview — total items, available, checked out, members, fines |
| **Catalog** | Browse, search, filter, and add Books / DVDs / Magazines |
| **Members** | View all members, see borrowed items and fines, register new members |
| **Check In/Out** | Borrow and return items, see late fee warnings |
| **AI Assistant** | Get AI-powered recommendations and ask the library anything |
| **Settings** | Enter your OpenRouter API key to activate AI features |

---

## ✅ Requirements — Install These First

### 1. Java JDK 21

Check if you have it:
```bash
java -version
```
If it says `openjdk 21` or higher you are good. If not:

| OS | Command |
|----|---------|
| macOS | `brew install openjdk@21` |
| Ubuntu | `sudo apt install openjdk-21-jdk` |
| Windows | Download from https://adoptium.net |

### 2. Apache Maven

Check if you have it:
```bash
mvn -version
```
If not:

| OS | Command |
|----|---------|
| macOS | `brew install maven` |
| Ubuntu | `sudo apt install maven` |
| Windows | Download from https://maven.apache.org/download.cgi |

---

## 🚀 How to Run

### Step 1 — Clone or download the project

```bash
git clone <your-repo-url>
cd LibrarySystemGUI
```

Or if you downloaded a ZIP, unzip it and open your terminal inside the folder.

### Step 2 — Run the app

```bash
mvn javafx:run
```

Maven will automatically download JavaFX, compile everything, and launch the window.

> **Note:** The first run takes about 30 seconds to download dependencies.
> Every run after that is much faster.

### Step 3 — Open in VS Code (optional)

```bash
code ~/LibrarySystemGUI
```

Then run from the VS Code terminal:
```bash
mvn javafx:run
```

---

## 🤖 Setting Up AI Features (Free — No Credit Card)

The AI uses **OpenRouter** which gives you free access to AI models with no
credit card required.

### Step 1 — Get your free API key
1. Go to **https://openrouter.ai**
2. Sign up with your Google account
3. Go to **Keys** → **Create Key**
4. Copy the key — it starts with `sk-or-...`

### Step 2 — Enter the key in the app
1. Run the app with `mvn javafx:run`
2. Click **Settings** in the left sidebar
3. Paste your API key in the field
4. Click **Save Key**
5. You will see "AI features are now active"

### Step 3 — Use the AI Assistant
Go to the **AI Assistant** screen and try:

**Personalised Recommendations:**
- Select a member from the dropdown
- Click **Get Recommendations**
- The AI suggests items based on their borrowing history

> Tip: First borrow some items for a member using Check In/Out,
> then come back — the recommendations get smarter with more history.

**Ask the Library AI — example questions:**
```
What technology books do you have?
```
```
Which DVD has the longest runtime?
```
```
What should I read if I like self-help books?
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
├── pom.xml                            # Maven config — manages dependencies
└── src/
    └── main/
        └── java/
            └── library/
                ├── LibraryItem.java   # Abstract base class (Abstraction)
                ├── Book.java          # Extends LibraryItem (Inheritance)
                ├── DVD.java           # Extends LibraryItem (Inheritance)
                ├── Magazine.java      # Extends LibraryItem (Inheritance)
                ├── Member.java        # Member class (Encapsulation)
                ├── Library.java       # Core system logic (Methods + Classes)
                ├── AIRecommender.java # OpenRouter AI integration (AI Usability)
                └── MainApp.java       # JavaFX GUI — all 6 screens (GUI)
```

---

## 🧱 OOP Principles — Where to Find Them

### 1. Classes
Every component is its own class with fields, a constructor, and methods:
`Library`, `Member`, `Book`, `DVD`, `Magazine`, `AIRecommender`, `MainApp`

### 2. Inheritance
`Book`, `DVD`, and `Magazine` all extend `LibraryItem`, inheriting common
fields and adding their own on top:
```java
public class Book     extends LibraryItem { ... }
public class DVD      extends LibraryItem { ... }
public class Magazine extends LibraryItem { ... }
```

### 3. Abstraction
`LibraryItem` is abstract — you cannot create it directly. It forces every
subclass to implement its own loan rules:
```java
public abstract String getItemType();
public abstract int    getLoanPeriodDays();
public abstract double getLateFeePerDay();
```

### 4. Polymorphism
The same method call on a `List<LibraryItem>` produces different output
depending on whether the item is a Book, DVD, or Magazine:
```java
for (LibraryItem item : library.getAllItems())
    item.getDisplayInfo(); // different result for each type
```

### 5. Encapsulation
All fields are private. Nothing can be changed directly from outside.
Fines in `Member` can only change through controlled methods:
```java
private double finesOwed;
public void addFine(double amt) { this.finesOwed += amt; }
public void payFine(double amt) { this.finesOwed = Math.max(0, finesOwed - amt); }
```

### 6. Methods
The system runs entirely through methods — `borrowItem()`, `returnItem()`,
`checkOut()`, `checkIn()`, `search()`, `getRecommendations()`, `askQuestion()` and more.

---

## 💰 Loan Rules

| Item Type | Loan Period | Late Fee Per Day |
|-----------|------------|-----------------|
| Book      | 14 days    | $0.25 |
| DVD       | 7 days     | $1.00 |
| Magazine  | 3 days     | $0.10 |

---

## 👥 Membership Types

| Type | Max Items at Once |
|------|------------------|
| STANDARD | 3 items |
| PREMIUM  | 10 items |

---

## 🛠 Common Issues and Fixes

**`mvn: command not found`**
Maven is not installed. See Requirements section above.

**`java: command not found`**
Java JDK is not installed. See Requirements section above.

**App window does not open**
Make sure you are inside the project folder first:
```bash
cd ~/LibrarySystemGUI
mvn javafx:run
```

**AI error 401**
Your API key is wrong. Go to openrouter.ai, generate a new key,
and paste it in the app Settings screen.

**AI error 429 — rate limited**
The free model is temporarily busy. Wait 30 seconds and try again.
If it keeps happening, the Settings screen — just change the model name
in `AIRecommender.java` to another free model from https://openrouter.ai/models?q=free

**Text not showing in AI response boxes**
Run the display fix script:
```bash
bash fix_display.sh
mvn javafx:run
```

**First run is very slow**
Normal — Maven is downloading JavaFX the first time. All runs after are fast.

---

## 🔧 Built With

- **Java 21** — core language
- **JavaFX 21** — desktop GUI framework
- **Apache Maven** — build and dependency management
- **OpenRouter + DeepSeek R1 (free)** — AI recommendations and Q&A

---

## 📄 License

Built as an academic demonstration of Java OOP principles
with GUI and AI integration.