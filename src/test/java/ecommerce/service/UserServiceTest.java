package ecommerce.service;

import ecommerce.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testGetAllUsers() {
        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(5, users.size());
    }

    @Test
    public void testGetAllUsers_ContainsExpectedUsers() {
        List<User> users = userService.getAllUsers();

        boolean hasAlice = users.stream()
                .anyMatch(user -> "alice@example.com".equals(user.getEmail()));

        assertTrue(hasAlice);
    }

    @Test
    public void testGetAllUsers_PremiumUsersCount() {
        List<User> users = userService.getAllUsers();

        long premiumCount = users.stream()
                .filter(User::isPremium)
                .count();

        assertEquals(3, premiumCount);
    }
}
