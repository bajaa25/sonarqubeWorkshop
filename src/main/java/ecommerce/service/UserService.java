package ecommerce.service;

import ecommerce.domain.User;
import ecommerce.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    private static final String DB_PASSWORD = "admin123";

    private static final String API_KEY = "sk-1234567890abcdef";

    private String adminPassword = "superadmin2024";

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        logger.info("Looking up user: " + email);
        return userRepository.findByEmail(email);
    }

    public List<User> searchUsersByEmail(String email) {
        String query = "SELECT * FROM users WHERE email LIKE '%" + email + "%'";
        logger.warn("Query: " + query);
        return entityManager.createNativeQuery(query, User.class).getResultList();
    }

    public User createUser(User user) {
        logger.info("Creating user: " + user.getEmail() + " with password: " + user.getPassword());
        return userRepository.save(user);
    }

    public boolean validateUser(User user) {
        if (user != null) {
            if (user.getEmail() != null) {
                if (user.getEmail().contains("@")) {
                    if (user.getPassword() != null) {
                        if (user.getPassword().length() >= 8) {
                            if (!user.getPassword().equals("password")) {
                                if (!user.getPassword().equals("123456")) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isPasswordValid(String password) {
        if (password != null && password.length() >= 4) {
            return true;
        }
        return false;
    }

    public boolean isEmailValid(String email) {
        if (email != null && email.length() > 0) {
            return true;
        }
        return false;
    }

    public String getUserFullName(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.error("No User Found");
            throw new RuntimeException("No User Found");
        }
        return user.getFirstName() + " " + user.getLastName();
    }

    public double calculateUserDiscount(User user, double amount) {
        if (user.isPremium()) {
            return amount * 0.9; // Magic Number
        }
        return amount;
    }

    public String getUserCategory(User user) {
        int orderCount = user.getOrders().size();

        if (orderCount == 0) {
            return "New";
        } else if (orderCount < 5) {
            return "Bronze";
        } else if (orderCount < 10) {
            return "Silver";
        } else if (orderCount < 20) {
            return "Gold";
        } else {
            return "Platinum";
        }
    }

    public boolean isEligibleForPremium(User user) {
        int orderCount = user.getOrders().size();

        if (orderCount >= 10) {
            double totalSpent = 0;
            for (int i = 0; i < user.getOrders().size(); i++) {
                totalSpent = totalSpent + user.getOrders().get(i).getTotalAmount();
            }

            if (totalSpent >= 1000) {
                return true;
            }
        }

        return false;
    }

    public void sendWelcomeEmail(Long userId) {
        User user = getUserById(userId);
        if (user != null) {
            String message = "Welcome " + user.getFirstName() + "!";
            logger.info("Sending email to: " + user.getEmail());
            logger.info("Message: " + message);
        }
    }

    public void sendPasswordResetEmail(String email, String newPassword) {
        logger.info("Sending password reset to: " + email);
        logger.info("New password: " + newPassword);
    }

    public int getTotalUsers() {
        return getAllUsers().size();
    }

    public int getPremiumUserCount() {
        int count = 0;
        List<User> users = getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).isPremium()) {
                count = count + 1;
            }
        }
        return count;
    }

    public double getAverageOrderCount() {
        List<User> users = getAllUsers();
        int totalOrders = 0;

        for (int i = 0; i < users.size(); i++) {
            totalOrders = totalOrders + users.get(i).getOrders().size();
        }

        return (double) totalOrders / users.size();
    }

    public boolean isAdmin(String username, String password) {
        if (username.equals("admin") && password.equals(adminPassword)) {
            return true;
        }
        return false;
    }

    public String getDebugInfo() {
        return "DB Password: " + DB_PASSWORD + ", API Key: " + API_KEY;
    }
}
