package ecommerce.service;

import ecommerce.constants.AppConstants;
import ecommerce.domain.Order;
import ecommerce.domain.User;
import ecommerce.dto.CreateUserRequest;
import ecommerce.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * User Service Implementation
 * Fixed: No hardcoded secrets, no SQL injection, proper error handling
 */
@Service
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(AppConstants.EMAIL_PATTERN);

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        // Fixed: No logging of sensitive data (email)
        logger.info("Looking up user by ID");
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    @Override
    public List<User> searchUsersByEmail(String email) {
        // Fixed: No SQL injection - using repository method
        logger.info("Searching users");
        return userRepository.searchByEmail(email);
    }

    @Override
    public User createUser(CreateUserRequest createUserRequest) {
        // Fixed: No logging of passwords!
        logger.info("Creating new user");
        User user = new User();
        user.setEmail(createUserRequest.getEmail());
        user.setPassword(createUserRequest.getPassword());
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());
        return userRepository.save(user);
    }

    @Override
    public boolean validateUser(CreateUserRequest createUserRequest) {
        return createUserRequest != null
                && isEmailValid(createUserRequest.getEmail())
                && isPasswordValid(createUserRequest.getPassword());
    }

    @Override
    public boolean isPasswordValid(String password) {
        return password != null && password.length() >= AppConstants.MIN_PASSWORD_LENGTH;
    }

    @Override
    public boolean isEmailValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public String getUserFullName(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found: {}", userId);
            return "Unknown User";
        }
        User user = userOpt.get();
        return user.getFirstName() + " " + user.getLastName();
    }

    @Override
    public double calculateUserDiscount(User user, double amount) {
        if (user.isPremium()) {
            return amount * (1 - AppConstants.PREMIUM_DISCOUNT);
        }
        return amount;
    }

    @Override
    public String getUserCategory(User user) {
        int orderCount = user.getOrders().size();

        if (orderCount == 0) {
            return "New";
        }
        if (orderCount < AppConstants.BRONZE_THRESHOLD) {
            return "Bronze";
        }
        if (orderCount < AppConstants.SILVER_THRESHOLD) {
            return "Silver";
        }
        if (orderCount < AppConstants.GOLD_THRESHOLD) {
            return "Gold";
        }
        return "Platinum";
    }

    @Override
    public boolean isEligibleForPremium(User user) {
        // Fixed: Using constants, reduced complexity
        int orderCount = user.getOrders().size();

        if (orderCount < AppConstants.PREMIUM_ELIGIBILITY_ORDERS) {
            return false;
        }

        double totalSpent = user.getOrders().stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        return totalSpent >= AppConstants.PREMIUM_ELIGIBILITY_AMOUNT;
    }

    @Override
    public int getTotalUsers() {
        return (int) userRepository.count();
    }

    @Override
    public int getPremiumUserCount() {
        return userRepository.findByPremium(true).size();
    }

    @Override
    public double getAverageOrderCount() {
        List<User> users = getAllUsers();
        if (users.isEmpty()) {
            return 0.0;
        }

        int totalOrders = users.stream()
                .mapToInt(user -> user.getOrders().size())
                .sum();

        return (double) totalOrders / users.size();
    }
}