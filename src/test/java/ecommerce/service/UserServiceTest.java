package ecommerce.service;

import ecommerce.domain.User;
import ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        // Test läuft mit Daten aus import.sql
        // 5 Users sollten vorhanden sein
    }

    @Test
    public void testGetAllUsers() {
        // Given: Datenbank hat Demo-Daten (import.sql)

        // When: Alle Users abrufen
        List<User> users = userService.getAllUsers();

        // Then: 5 Users sollten vorhanden sein
        Assertions.assertNotNull(users, "User list should not be null");
        Assertions.assertEquals(5, users.size(), "Should have 5 users from import.sql");
    }

    @Test
    public void testGetAllUsers_ContainsExpectedUsers() {
        // When
        List<User> users = userService.getAllUsers();

        // Then: Überprüfe dass Alice und Bob existieren
        boolean hasAlice = users.stream()
                .anyMatch(user -> "alice@example.com".equals(user.getEmail()));
        boolean hasBob = users.stream()
                .anyMatch(user -> "bob@example.com".equals(user.getEmail()));

        Assertions.assertTrue(hasAlice, "Should contain Alice");
        Assertions.assertTrue(hasBob, "Should contain Bob");
    }

    @Test
    public void testGetAllUsers_PremiumUsersCount() {
        // When
        List<User> users = userService.getAllUsers();

        // Then: 3 Premium Users (Alice, Charlie, Eve)
        long premiumCount = users.stream()
                .filter(User::isPremium)
                .count();

        Assertions.assertEquals(3, premiumCount, "Should have 3 premium users");
    }
}