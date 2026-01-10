# E-Commerce Application - SonarCloud Workshop

Eine Spring Boot E-Commerce Anwendung für SonarCloud Quality & Security Workshop.

## 🎯 Lernziele

Nach diesem Workshop können Sie:

✅ SonarCloud nutzen und verstehen
✅ Security Hotspots identifizieren  
✅ Code Quality Metriken interpretieren  
✅ Technical Debt verstehen

**Bonus:**
✅ IntelliJ + SonarQube Plugin für Live-Feedback nutzen

## 📋 Voraussetzungen

### System Requirements
- **JDK**: 11 oder höher
- **Maven**: 3.6+ ([Download](https://maven.apache.org/download.cgi))

### Installation prüfen

```bash
# Java Version prüfen
java -version
# Sollte zeigen: openjdk version "11.x.x" oder höher

# Maven Version prüfen
mvn -version
# Sollte zeigen: Apache Maven 3.6.x oder höher
```

### Workshop-Zugang

**Für den Workshop nutzen wir einen gemeinsamen GitHub Account:**

```
GitHub Account (für SonarCloud Login):
Email:    bennet.bgt@googlemail.com
Username: SonarQubeDemoUser  
Password: SonarQubeWorkshop2026
```

**⚠️ Wichtig:** Diese Zugangsdaten nur während des Workshops nutzen!

## 🚀 Quick Start

### 1. Projekt Setup

```bash
# Verzeichnis wechseln
cd ecommerce-app

# Dependencies installieren
mvn clean install
```

### 2. Anwendung starten

```bash
# Starten
mvn spring-boot:run

# Warte bis du diese Meldung siehst:
# "Started Application in X.XXX seconds"
```

### 3. Testen

Öffne Browser oder nutze curl:

```bash
# Alle Users ansehen
curl http://localhost:8081/api/users

# Alle Orders ansehen
curl http://localhost:8081/api/orders
```
---

## 📂 Projekt-Struktur

```
ecommerce-app/
├── pom.xml                          # Maven Dependencies
├── src/
│   ├── main/
│   │   ├── java/com/example/ecommerce/
│   │   │   ├── config/                     # Configuration
│   │   │   │   ├── AppConfig.java         
│   │   │   ├── controller/                     # Presentation
│   │   │   │   ├── UserController.java         
│   │   │   │   └── OrderController.java        
│   │   │   ├── domain/                         # Domain/Entity
│   │   │   │   ├── User.java                   
│   │   │   │   ├── Order.java
│   │   │   │   └── OrderStatus.java            # Enum
│   │   │   ├── repository/                     # Persistance
│   │   │   │   ├── UserRepository.java         
│   │   │   │   └── OrderRepository.java
│   │   │   ── service/                        # Business
│   │   │   │   ├── UserService.java            
│   │   │   │   └── OrderService.java
│   │   │   ├── ECommerceShop.java              # Main
│   │   └── resources/
│   │       ├── application.properties          # Config
│   │       └── import.sql                      # Demo Data
│   └── test/
│       └── java/                               # Tests
└── README.md                                   
```

## 📚 Weiterführende Links
- [SonarCloud Dokumentation](https://docs.sonarcloud.io)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [SonarQube for IDE](https://www.sonarsource.com/products/sonarlint/)

----
# Input für die Semantik und Inhalt vom Projekt

## 📡 API Endpoints

### Users
```
GET    /api/users              - Alle User
GET    /api/users/{id}         - User by ID  
GET    /api/users/search       - User suchen (?email=alice)
POST   /api/users              - User erstellen
```

### Orders
```
GET    /api/orders             - Alle Orders
GET    /api/orders/{id}        - Order by ID
GET    /api/orders/user/{id}   - Orders eines Users
GET    /api/orders/search      - Orders suchen (?product=MacBook)
POST   /api/orders             - Order erstellen
```

### Database Console
```
GET    /h2-console             - H2 Database Console
       JDBC URL: jdbc:h2:mem:testdb
       Username: sa
       Password: (leer lassen)
```

## 🗄️ Demo-Daten

Die Anwendung startet automatisch mit:

### 5 Users:
- **Alice** (alice@example.com) - Premium User
- **Bob** (bob@example.com) - Regular User
- **Charlie** (charlie@example.com) - Premium User
- **Diana** (diana@example.com) - Regular User
- **Eve** (eve@example.com) - Premium User

### 10 Orders:
- MacBook Pro 16" (€2,499)
- 2x iPhone 15 Pro (€2,398)
- Samsung Galaxy S24 (€899)
- 3x AirPods Pro (€837)
- iPad Air (€679)
- Dell XPS 15 Laptop (€1,899)
- Sony WH-1000XM5 Headphones (€379)
- Apple Watch Series 9 (€449)
- Nintendo Switch OLED (€349)
- Kindle Paperwhite (€139)

**Total Revenue**: €10,527

---
# SonarQube SonarCloud login

1. Gehe zu [sonarcloud.io](https://sonarcloud.io)
2. Click **"Log in"**
3. Wähle **"Sign in with GitHub"**
4. **Nutze die Workshop-Zugangsdaten** (siehe oben)

---

## 🎁 BONUS - Für schnelle Teilnehmer

**Fertig mit den Workshop-Aufgaben? Hier sind erweiterte Challenges:**

### 🔧 Bonus 1: IntelliJ IDEA + SonarQube Plugin

**Setup IntelliJ für Live-Feedback während des Codens**

#### Installation

1. **SonarQube for IDE Plugin installieren**
   ```
   IntelliJ öffnen
   → File → Settings → Plugins (Windows/Linux)
   → IntelliJ IDEA → Settings → Plugins (macOS)
   → Suche: "SonarQube for IDE"
   → Install → IntelliJ neu starten
   ```

2. **Projekt öffnen**
   ```bash
   # Im Terminal
   cd ecommerce-app
   idea .
   
   # Oder: IntelliJ → Open → ecommerce-app Ordner wählen
   ```

3. **Warte auf Indexierung**
   - Unten rechts: "Indexing..." muss fertig sein
   - Kann 2-5 Minuten dauern

#### Live-Analyse nutzen

1. **Issues sofort sehen**
   ```
   Öffne: UserService.java
   → Zeile 44: SQL Injection (rot markiert)
   → Zeile 24: Hardcoded Password (gelb markiert)
   ```

2. **Quick Fixes nutzen**
   ```
   Cursor auf Issue → Alt+Enter (Win/Linux) oder ⌥↵ (Mac)
   → Zeigt Vorschläge zur Behebung
   ```

3. **SonarQube Tool Window**
   ```
   View → Tool Windows → SonarQube (oder Alt+6)
   → Zeigt alle Issues im Projekt
   → Filter nach Severity, Type, etc.
   ```
---

### 📊 Bonus 2: Quality Gate selbst konfigurieren

1. **SonarCloud → Quality Gates**
2. **Eigenes Gate erstellen:** "Workshop-Gate"
3. **Bedingungen hinzufügen:**
   - Coverage on New Code < 80% → Failed
   - Duplicated Lines on New Code > 3% → Failed
   - New Security Hotspots > 0 → Warning
   - New Bugs > 0 → Failed
4. **Auf Projekt anwenden**
5. **Re-Scan → Status prüfen**