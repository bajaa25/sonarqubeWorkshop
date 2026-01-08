package ecommerce.controller;

import ecommerce.domain.User;
import ecommerce.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users") //BASE URL: Prefix for all Endpoint-Links down below -> e.g. /api/users/{id}
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping //URL: /api/users
    public List<User> getAllUsers() {
        logger.info("Getting all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}") //URL: /api/users/{id}
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("Getting user: " + id);
        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String email) {
        logger.info("Searching: " + email);
        return userService.searchUsersByEmail(email);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        logger.info("Creating user: " + user.getEmail());
        User created = userService.createUser(user);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            return ResponseEntity.badRequest().body("Invalid email");
        }

        if (user.getPassword() == null || user.getPassword().length() < 8) {
            return ResponseEntity.badRequest().body("Password too short");
        }

        if (user.getFirstName() == null || user.getFirstName().length() < 2) {
            return ResponseEntity.badRequest().body("First name required");
        }

        User created = userService.createUser(user);
        return ResponseEntity.ok("User registered: " + created.getId());
    }


    @GetMapping("/{id}/discount")
    public ResponseEntity<String> calculateDiscount(
            @PathVariable Long id,
            @RequestParam double amount) {

        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        double discount = 0;
        if (user.isPremium()) {
            discount = amount * 0.1;
        }

        double finalAmount = amount - discount;

        String result = "Amount: €" + amount +
                ", Discount: €" + discount +
                ", Final: €" + finalAmount;

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<String> getUserSummary(@PathVariable Long id) {
        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        String summary = "User: " + user.getFirstName() + " " + user.getLastName() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Premium: " + (user.isPremium() ? "Yes" : "No") + "\n" +
                "Orders: " + user.getOrders().size();

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/statistics")
    public ResponseEntity<String> getStatistics() {
        List<User> users = userService.getAllUsers();

        int totalUsers = users.size();
        int premiumUsers = 0;
        int totalOrders = 0;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).isPremium()) {
                premiumUsers = premiumUsers + 1;
            }
            totalOrders = totalOrders + users.get(i).getOrders().size();
        }

        double avgOrders = (double) totalOrders / totalUsers;

        String stats = "Total: " + totalUsers + ", Premium: " + premiumUsers +
                ", Avg Orders: " + avgOrders;

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/debug")
    public ResponseEntity<String> getDebugInfo() {
        return ResponseEntity.ok(userService.getDebugInfo());
    }

    @PostMapping("/admin/reset-password")
    public ResponseEntity<String> adminResetPassword(
            @RequestParam String email,
            @RequestParam String newPassword) {

        User user = userService.getUserByEmail(email);
        if (user != null) {
            user.setPassword(newPassword);
            userService.sendPasswordResetEmail(email, newPassword);
            return ResponseEntity.ok("Password reset for: " + email);
        }

        return ResponseEntity.notFound().build();
    }
}
