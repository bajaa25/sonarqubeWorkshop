package ecommerce.service;

import ecommerce.controller.UserController;
import ecommerce.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController userController;

    /* -------------------------------------------------
       GET /api/users
       ------------------------------------------------- */

    @Test
    public void testGetAllUsers() {
        List<User> users = userController.getAllUsers();

        assertNotNull(users);
        assertEquals(5, users.size());
    }

    @Test
    public void testGetAllUsers_ContainsExpectedUser() {
        List<User> users = userController.getAllUsers();

        boolean hasAlice = users.stream()
                .anyMatch(user -> "alice@example.com".equals(user.getEmail()));

        assertTrue(hasAlice);
    }

    /* -------------------------------------------------
       GET /api/users/{id}
       ------------------------------------------------- */

    @Test
    public void testGetUserById_Found() {
        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetUserById_NotFound() {
        ResponseEntity<User> response = userController.getUserById(999L);

        assertEquals(404, response.getStatusCodeValue());
    }

    /* -------------------------------------------------
       GET /api/users/search
       ------------------------------------------------- */

    @Test
    public void testSearchUsersByEmail() {
        List<User> result = userController.searchUsers("alice");

        assertFalse(result.isEmpty());
        assertTrue(result.stream()
                .allMatch(u -> u.getEmail().contains("alice")));
    }

    /* -------------------------------------------------
       POST /api/users
       ------------------------------------------------- */

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setFirstName("Bob");
        user.setLastName("Smith");
        user.setEmail("bob.smith@example.com");
        user.setPassword("password123");
        user.setPremium(false);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
    }

    /* -------------------------------------------------
       POST /api/users/register
       ------------------------------------------------- */

    @Test
    public void testRegisterUser_Valid() {
        User user = new User();
        user.setFirstName("Charlie");
        user.setLastName("Brown");
        user.setEmail("charlie@example.com");
        user.setPassword("securePass123");

        ResponseEntity<String> response = userController.register(user);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("User registered"));
    }

    @Test
    public void testRegisterUser_InvalidEmail() {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("invalid-email");
        user.setPassword("securePass123");

        ResponseEntity<String> response = userController.register(user);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid email", response.getBody());
    }

    /* -------------------------------------------------
       GET /api/users/{id}/discount
       ------------------------------------------------- */

    @Test
    public void testCalculateDiscount_PremiumUser() {
        ResponseEntity<String> response =
                userController.calculateDiscount(1L, 100.0);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Discount"));
    }

    @Test
    public void testCalculateDiscount_UserNotFound() {
        ResponseEntity<String> response =
                userController.calculateDiscount(999L, 100.0);

        assertEquals(404, response.getStatusCodeValue());
    }

    /* -------------------------------------------------
       GET /api/users/{id}/summary
       ------------------------------------------------- */

    @Test
    public void testGetUserSummary() {
        ResponseEntity<String> response = userController.getUserSummary(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Email:"));
    }

    /* -------------------------------------------------
       GET /api/users/statistics
       ------------------------------------------------- */

    @Test
    public void testGetStatistics() {
        ResponseEntity<String> response = userController.getStatistics();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Total:"));
        assertTrue(response.getBody().contains("Premium:"));
    }

    /* -------------------------------------------------
       GET /api/users/debug
       ------------------------------------------------- */

    @Test
    public void testGetDebugInfo() {
        ResponseEntity<String> response = userController.getDebugInfo();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    /* -------------------------------------------------
       POST /api/users/admin/reset-password
       ------------------------------------------------- */

    @Test
    public void testAdminResetPassword_Success() {
        ResponseEntity<String> response =
                userController.adminResetPassword(
                        "alice@example.com",
                        "newPassword123");

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Password reset"));
    }

    @Test
    public void testAdminResetPassword_UserNotFound() {
        ResponseEntity<String> response =
                userController.adminResetPassword(
                        "unknown@example.com",
                        "newPassword123");

        assertEquals(404, response.getStatusCodeValue());
    }
}
