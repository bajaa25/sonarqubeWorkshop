package ecommerce.controller;

import ecommerce.domain.User;
import ecommerce.dto.CreateUserRequest;
import ecommerce.service.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * User Controller
 * Fixed: No business logic, only routing and delegation to service
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    // Fixed: Using interface instead of concrete class
    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        logger.info("GET /api/users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("GET /api/users/{}", id);
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String email) {
        logger.info("GET /api/users/search");
        return userService.searchUsersByEmail(email);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        logger.info("POST /api/users");
        User created = userService.createUser(createUserRequest);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody CreateUserRequest createUserRequest) {
        logger.info("POST /api/users/register");

        if (!userService.validateUser(createUserRequest)) {
            return ResponseEntity.badRequest().body("Invalid user data");
        }

        User created = userService.createUser(createUserRequest);
        return ResponseEntity.ok("User registered: " + created.getId());
    }

    @GetMapping("/{id}/discount")
    public ResponseEntity<String> calculateDiscount(
            @PathVariable Long id,
            @RequestParam double amount) {

        logger.info("GET /api/users/{}/discount", id);

        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Fixed: Business logic delegated to service
        User user = userOpt.get();
        double finalAmount = userService.calculateUserDiscount(user, amount);
        double discount = amount - finalAmount;

        String result = String.format("Amount: €%.2f, Discount: €%.2f, Final: €%.2f",
                amount, discount, finalAmount);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<String> getUserSummary(@PathVariable Long id) {
        logger.info("GET /api/users/{}/summary", id);

        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Fixed: Formatting logic could be in service, but simple enough
        User user = userOpt.get();
        String summary = String.format("User: %s%nEmail: %s%nPremium: %s%nOrders: %d",
                userService.getUserFullName(id),
                user.getEmail(),
                user.isPremium() ? "Yes" : "No",
                user.getOrders().size());

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/statistics")
    public ResponseEntity<String> getStatistics() {
        logger.info("GET /api/users/statistics");

        // Fixed: Statistics calculation delegated to service
        int totalUsers = userService.getTotalUsers();
        int premiumUsers = userService.getPremiumUserCount();
        double avgOrders = userService.getAverageOrderCount();

        String stats = String.format("Total: %d, Premium: %d, Avg Orders: %.2f",
                totalUsers, premiumUsers, avgOrders);

        return ResponseEntity.ok(stats);
    }
}