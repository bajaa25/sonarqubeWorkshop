# 🔍 E-Commerce Application - SonarCloud Issues Overview

## 📊 **Summary**

**Total Issues: ~50-60**

| Kategorie | Anzahl | Severity |
|-----------|--------|----------|
| 🔴 **Vulnerabilities** | 12 | Critical/High |
| 🟡 **Security Hotspots** | 12 | High/Medium |
| 🐛 **Bugs** | 3 | High/Medium |
| 💩 **Code Smells** | 20+ | Medium/Low |
| 👔 **Responsibility** | 8 | Medium |
| 🔧 **Adaptability** | 4 | Medium |

---

## 🔴 **VULNERABILITIES (12)**

### **1. SQL Injection (2 Issues)**
**Severity:** Critical | **Type:** Security | **OWASP:** A03:2021

#### UserService.java - Line 45
```java
public List<User> searchUsersByEmail(String email) {
    String query = "SELECT * FROM users WHERE email LIKE '%" + email + "%'";
    return entityManager.createNativeQuery(query, User.class).getResultList();
}
```
- ❌ **Problem:** User input direkt in SQL eingebaut
- ✅ **Fix:** PreparedStatement oder JPQL verwenden
```java
@Query("SELECT u FROM User u WHERE u.email LIKE %:email%")
List<User> searchByEmail(@Param("email") String email);
```

#### OrderService.java - Line 53
```java
public List<Order> searchOrdersByProduct(String productName) {
    String query = "SELECT * FROM orders WHERE product_name LIKE '%" + productName + "%'";
    return entityManager.createNativeQuery(query, Order.class).getResultList();
}
```
- ❌ **Problem:** Gleicher SQL Injection Fehler
- ✅ **Fix:** JPQL mit Parameter Binding

---

### **2. Hardcoded Credentials (8 Issues)**
**Severity:** Critical | **Type:** Security | **CWE:** CWE-798

#### AppConfig.java - Lines 11-20
```java
private String databasePassword = "password123";     // ❌ Issue 1
private String jwtSecret = "mySecretKey12345";       // ❌ Issue 2

public boolean isAdminUser(String username, String password) {
    return username.equals("admin") && password.equals("admin123"); // ❌ Issue 3
}
```

#### UserService.java - Lines 15-19
```java
private static final String DB_PASSWORD = "admin123";          // ❌ Issue 4
private static final String API_KEY = "sk-1234567890abcdef";  // ❌ Issue 5
private String adminPassword = "superadmin2024";               // ❌ Issue 6
```

#### OrderService.java - Lines 22-24
```java
private static final String PAYMENT_API_KEY = "pk_test_123456789"; // ❌ Issue 7
private String shippingSecret = "ship_secret_abc123";              // ❌ Issue 8
```

**Fix für alle:**
```java
// Environment Variables nutzen
private String apiKey = System.getenv("API_KEY");
private String dbPassword = System.getenv("DB_PASSWORD");
```

---

### **3. Weak Password Validation (1 Issue)**
**Severity:** High | **Type:** Security

#### UserService.java - Line 77
```java
public boolean isPasswordValid(String password) {
    if (password != null && password.length() >= 4) {  // ❌ Nur 4 Zeichen!
        return true;
    }
    return false;
}
```
- ❌ **Problem:** Erlaubt zu kurze Passwörter (4 Zeichen)
- ✅ **Fix:** Mindestens 8 Zeichen + Complexity Check

---

### **4. No Input Validation (1 Issue)**
**Severity:** Medium | **Type:** Security

#### UserService.java - Line 83
```java
public boolean isEmailValid(String email) {
    if (email != null && email.length() > 0) {  // ❌ Keine echte Validierung
        return true;
    }
    return false;
}
```
- ❌ **Problem:** Akzeptiert "abc" als Email
- ✅ **Fix:** Regex Pattern für Email-Validierung

---

## 🟡 **SECURITY HOTSPOTS (12)**

### **1. Logging Sensitive Data (6 Issues)**
**Severity:** High | **Type:** Privacy/GDPR

#### UserService.java - Line 51
```java
public User createUser(User user) {
    logger.info("Creating user: " + user.getEmail() + " with password: " + user.getPassword());
    // ❌ PASSWORD IM LOG!
}
```

#### UserService.java - Line 156
```java
public void sendPasswordResetEmail(String email, String newPassword) {
    logger.info("Sending password reset to: " + email);
    logger.info("New password: " + newPassword);  // ❌ PASSWORD IM LOG!
}
```

#### OrderController.java - Lines 107-108
```java
logger.info("Card: " + creditCard + ", CVV: " + cvv);  // ❌ CREDIT CARD DETAILS!
```

#### OrderService.java - Lines 126-127
```java
logger.info("Credit card: " + creditCard);       // ❌ CREDIT CARD!
logger.info("Using API Key: " + PAYMENT_API_KEY); // ❌ API KEY!
```

#### OrderService.java - Line 137
```java
logger.info("Shipping order with secret: " + shippingSecret); // ❌ SECRET!
```

**Fix für alle:**
```java
logger.info("Creating user: " + user.getId()); // Nur ID loggen
logger.info("Payment processed for order: " + orderId); // Keine Card Details
```

---

### **2. HTTP URLs (1 Issue)**
**Severity:** Medium | **Type:** Man-in-the-Middle

#### AppConfig.java - Line 14
```java
private String apiBaseUrl = "http://api.example.com";  // ❌ HTTP statt HTTPS
```
- ❌ **Problem:** Unverschlüsselte Kommunikation
- ✅ **Fix:** `https://api.example.com`

---

### **3. Debug Endpoints in Production (1 Issue)**
**Severity:** High | **Type:** Information Disclosure

#### UserController.java - Line 143
```java
@GetMapping("/debug")
public ResponseEntity<String> getDebugInfo() {
    return ResponseEntity.ok(userService.getDebugInfo());
    // Gibt DB_PASSWORD und API_KEY zurück!
}
```

#### UserService.java - Line 182
```java
public String getDebugInfo() {
    return "DB Password: " + DB_PASSWORD + ", API Key: " + API_KEY;
}
```
- ❌ **Problem:** Secrets werden exposed
- ✅ **Fix:** Endpoint komplett entfernen oder mit Auth schützen

---

### **4. No Authentication on Admin Endpoints (2 Issues)**
**Severity:** Critical | **Type:** Authorization

#### UserController.java - Line 147
```java
@PostMapping("/admin/reset-password")
public ResponseEntity<String> adminResetPassword(...) {
    // ❌ KEINE AUTH-PRÜFUNG! Jeder kann Passwörter zurücksetzen!
}
```

#### OrderController.java - Line 120
```java
@PostMapping("/{id}/force-ship")
public ResponseEntity<String> forceShip(@PathVariable Long id) {
    // ❌ KEINE AUTH-PRÜFUNG! Jeder kann Orders shippen!
}
```

**Fix:**
```java
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/admin/reset-password")
public ResponseEntity<String> adminResetPassword(...) {
    // Jetzt geschützt!
}
```

---

### **5. CORS Misconfiguration (1 Issue)**
**Severity:** Medium | **Type:** Cross-Origin

#### AppConfig.java - Line 59
```java
public String[] getAllowedOrigins() {
    return new String[]{"*"};  // ❌ Alle Origins erlaubt!
}
```
- ❌ **Problem:** Keine CORS-Einschränkungen
- ✅ **Fix:** Spezifische Origins angeben

---

### **6. No Rate Limiting (1 Issue)**
**Severity:** Medium | **Type:** DoS Prevention

#### AppConfig.java - Line 63
```java
public boolean isRateLimitingEnabled() {
    return false;  // ❌ Kein Rate Limiting!
}
```
- ❌ **Problem:** API kann geflutet werden
- ✅ **Fix:** Rate Limiting aktivieren

---

## 🐛 **BUGS (3)**

### **1. NullPointerException (2 Issues)**
**Severity:** High | **Type:** Reliability

#### UserService.java - Line 89
```java
public String getUserFullName(Long userId) {
    User user = userRepository.findById(userId).orElse(null);
    return user.getFirstName() + " " + user.getLastName();  // ❌ NPE wenn user == null!
}
```

**Fix:**
```java
public String getUserFullName(Long userId) {
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) {
        return "Unknown User";
    }
    return user.getFirstName() + " " + user.getLastName();
}
```

#### OrderService.java - Line 67
```java
public Order createOrder(Long userId, ...) {
    User user = userRepository.findById(userId).orElse(null);
    // ...
    double total = calculateTotal(price, quantity, user.isPremium());  // ❌ NPE!
}
```

---

### **2. Empty Catch Block (1 Issue)**
**Severity:** Medium | **Type:** Error Handling

#### OrderService.java - Line 107
```java
public void cancelOrder(Long orderId) {
    try {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    } catch (Exception e) {
        // ❌ Fehler wird verschluckt!
    }
}
```

**Fix:**
```java
catch (Exception e) {
    logger.error("Failed to cancel order: " + orderId, e);
    throw new OrderCancellationException("Could not cancel order", e);
}
```

---

## 💩 **CODE SMELLS (20+)**

### **1. Cognitive Complexity (2 Issues)**
**Severity:** Medium | **Type:** Maintainability

#### UserService.java - Line 55
```java
public boolean validateUser(User user) {
    if (user != null) {
        if (user.getEmail() != null) {
            if (user.getEmail().contains("@")) {
                if (user.getPassword() != null) {
                    if (user.getPassword().length() >= 8) {
                        if (!user.getPassword().equals("password")) {
                            if (!user.getPassword().equals("123456")) {
                                return true;  // ❌ 7 Ebenen tief!
```
**Cognitive Complexity: 15** (Threshold: 8)

**Fix mit Early Return:**
```java
public boolean validateUser(User user) {
    if (user == null) return false;
    if (user.getEmail() == null) return false;
    if (!user.getEmail().contains("@")) return false;
    if (user.getPassword() == null) return false;
    if (user.getPassword().length() < 8) return false;
    if (user.getPassword().equals("password")) return false;
    if (user.getPassword().equals("123456")) return false;
    return true;
}
```

#### OrderService.java - Line 79
```java
private double calculateTotal(double price, int quantity, boolean isPremium) {
    // ❌ Viele verschachtelte if-Statements
    if (isPremium) {
        if (quantity > 5) {
            if (total > 100) {
                total = total * 0.85;
            } else {
                total = total * 0.9;
            }
        }
    }
}
```

---

### **2. Magic Numbers (10+ Issues)**
**Severity:** Low | **Type:** Maintainability

**Überall im Code:**
```java
// IUserService.java
if (orderCount < 5) return "Bronze";      // ❌ Magic 5
if (orderCount < 10) return "Silver";     // ❌ Magic 10
if (orderCount < 20) return "Gold";       // ❌ Magic 20
if (totalSpent >= 1000) return true;      // ❌ Magic 1000

// IOrderService.java
if (quantity > 5) ...                     // ❌ Magic 5
if (total > 100) ...                      // ❌ Magic 100
total = total * 0.85;                     // ❌ Magic 0.85
total = total * 0.9;                      // ❌ Magic 0.9

// UserController.java
discount = amount * 0.1;                  // ❌ Magic 0.1

// AppConfig.java
sessionTimeoutSeconds = 2592000;          // ❌ Magic 2592000 (30 Tage)
```

**Fix:**
```java
private static final int BRONZE_THRESHOLD = 5;
private static final int SILVER_THRESHOLD = 10;
private static final int GOLD_THRESHOLD = 20;
private static final double PREMIUM_DISCOUNT = 0.1;
```

---

### **3. Public Fields (1 Issue)**
**Severity:** Low | **Type:** Encapsulation

#### User.java - Line 15
```java
@Column(unique = true, nullable = false)
public String email;  // ❌ Public field!
```

**Fix:**
```java
@Column(unique = true, nullable = false)
private String email;  // ✅ Private
```

---

### **4. Mutable Collection Return (1 Issue)**
**Severity:** Medium | **Type:** Encapsulation

#### User.java - Line 80
```java
public List<Order> getOrders() {
    return orders;  // ❌ Direkte Referenz wird zurückgegeben!
}
```

**Fix:**
```java
public List<Order> getOrders() {
    return Collections.unmodifiableList(orders);  // ✅ Immutable
}
```

---

### **5. String Concatenation in Loops (2+ Issues)**
**Severity:** Low | **Type:** Performance

#### UserController.java - Line 121
```java
for (int i = 0; i < users.size(); i++) {
    // String concatenation in loop
}
```

**Fix:**
```java
StringBuilder sb = new StringBuilder();
for (User user : users) {
    sb.append(user.getName());
}
```

---

## 👔 **RESPONSIBILITY ISSUES (8)**

### **1. God Class (1 Issue)**
**Severity:** Medium | **Type:** Single Responsibility Principle

#### UserService.java - 182 Zeilen, 20+ Methoden
```
UserService macht ZU VIEL:
├── CRUD Operations (5 Methoden)
├── Validation (3 Methoden)
├── Business Logic (3 Methoden)
├── Email Services (2 Methoden)
├── Statistics (3 Methoden)
└── Admin Functions (2 Methoden)
```

**Fix:** Aufteilen in:
- `UserRepository` (CRUD)
- `UserValidationService` (Validation)
- `EmailService` (Email)
- `UserStatisticsService` (Stats)

---

### **2. Business Logic in Controllers (5 Issues)**
**Severity:** Medium | **Type:** Separation of Concerns

#### UserController.java - Lines 86-93
```java
@GetMapping("/{id}/discount")
public ResponseEntity<String> calculateDiscount(...) {
    // ❌ Business Logic im Controller!
    double discount = 0;
    if (user.isPremium()) {
        discount = amount * 0.1;
    }
}
```

#### UserController.java - Lines 115-131
```java
@GetMapping("/statistics")
public ResponseEntity<String> getStatistics() {
    // ❌ Statistik-Berechnung im Controller!
    for (int i = 0; i < users.size(); i++) {
        if (users.get(i).isPremium()) {
            premiumUsers = premiumUsers + 1;
        }
    }
}
```

#### OrderController.java - Lines 83-100
```java
@GetMapping("/statistics")
public ResponseEntity<String> getStatistics() {
    // ❌ Revenue-Berechnung im Controller!
    for (int i = 0; i < orders.size(); i++) {
        totalRevenue = totalRevenue + order.getTotalAmount();
    }
}
```

**Fix:** Logic in Service verschieben

---

### **3. Feature Envy (1 Issue)**
**Severity:** Low | **Type:** Object-Oriented Design

#### UserService.java - Line 95
```java
public double calculateUserDiscount(User user, double amount) {
    if (user.isPremium()) {  // ❌ Nutzt nur User-Daten!
        return amount * 0.9;
    }
}
```

**Fix:** Methode in User-Klasse verschieben
```java
// In User.java
public double calculateDiscount(double amount) {
    return this.premium ? amount * 0.9 : amount;
}
```

---

### **4. Data Clump (1 Issue)**
**Severity:** Low | **Type:** Code Organization

#### OrderController.java - Line 145
```java
class CreateOrderRequest {
    private Long userId;        // ❌ Diese 4 Parameter
    private String productName; // werden immer
    private int quantity;       // zusammen
    private double price;       // übergeben
}
```

**Fix:** DTO verwenden (bereits vorhanden, also OK)

---

## 🔧 **ADAPTABILITY ISSUES (4)**

### **1. No Interfaces (3 Issues)**
**Severity:** Medium | **Type:** Testability/Flexibility

#### UserController.java - Line 17
```java
@Autowired
private UserService userService;  // ❌ Concrete Class!
```

#### OrderController.java - Line 20
```java
@Autowired
private OrderService orderService;  // ❌ Concrete Class!
```

#### UserService.java - Line 21
```java
@Autowired
private UserRepository userRepository;  // ❌ OK (ist Interface)
```

**Fix:**
```java
// Create interface
public interface IUserService {
    List<User> getAllUsers();
    // ...
}

// Inject interface
@Autowired
private IUserService userService;
```

---

### **2. Tight Coupling (1 Issue)**
**Severity:** Medium | **Type:** Dependency Management

Multiple Services sind direkt gekoppelt ohne Abstraction Layer.

**Fix:** Dependency Injection mit Interfaces

---

## 📋 **QUICK FIX PRIORITY**

### **🔴 Critical (Fix SOFORT):**
1. SQL Injection (2x)
2. Hardcoded Secrets (8x)
3. No Authentication on Admin Endpoints (2x)
4. Logging Credit Cards/Passwords (6x)

### **🟡 High (Fix BALD):**
1. NullPointerException (2x)
2. Debug Endpoint exposes secrets
3. Weak Password Validation

### **🟢 Medium (Fix SPÄTER):**
1. Empty Catch Block
2. Cognitive Complexity (2x)
3. God Class
4. Business Logic in Controllers (5x)
5. Magic Numbers (10+)

---

## 🎯 **WORKSHOP TEAMS**

### **Team A - Critical Security:**
- [ ] SQL Injection fixen (2)
- [ ] Hardcoded Secrets entfernen (8)
- [ ] Logging Sensitive Data stoppen (6)

### **Team B - Code Quality:**
- [ ] Magic Numbers → Constants (10+)
- [ ] NPE → null-checks (2)
- [ ] Cognitive Complexity vereinfachen (2)

### **Team C - Design & Architecture:**
- [ ] God Class aufteilen (1)
- [ ] Business Logic aus Controller (5)
- [ ] Interfaces erstellen (3)

---

## 📊 **EXPECTED SONARCLOUD METRICS**

Nach den Fixes:

```
Software Quality:
├─ Security:        17 → 3   ✅ (-14)
├─ Reliability:      7 → 2   ✅ (-5)
└─ Maintainability: 61 → 30  ✅ (-31)

Code Attributes:
├─ Consistency:      7 → 3   ✅
├─ Intentionality:  72 → 40  ✅
├─ Adaptability:     1 → 8   ✅ (+7)
└─ Responsibility:   0 → 12  ✅ (+12)
```

**Quality Gate: PASSED** ✅

---

## 🚀 **GETTING STARTED**

```bash
# 1. Clone & Setup
git clone https://github.com/YOUR_REPO/ecommerce-shop
cd ecommerce-shop

# 2. Build
mvn clean install

# 3. Run
mvn spring-boot:run

# 4. SonarCloud Scan
mvn clean verify sonar:sonar -Dsonar.token=YOUR_TOKEN

# 5. View Results
https://sonarcloud.io/dashboard?id=bajaa25_ECommerceShop
```

---

**Happy Fixing! 🎉**