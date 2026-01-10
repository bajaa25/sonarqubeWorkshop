package ecommerce.service;

import ecommerce.domain.User;
import ecommerce.dto.CreateUserRequest;

import java.util.List;
import java.util.Optional;

/**
 * User Service Interface
 * Fixed: Adaptability - using interfaces instead of concrete classes
 */
public interface IUserService {

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    List<User> searchUsersByEmail(String email);

    User createUser(CreateUserRequest createUserRequest);

    boolean validateUser(CreateUserRequest createUserRequest);

    boolean isPasswordValid(String password);

    boolean isEmailValid(String email);

    String getUserFullName(Long userId);

    double calculateUserDiscount(User user, double amount);

    String getUserCategory(User user);

    boolean isEligibleForPremium(User user);

    int getTotalUsers();

    int getPremiumUserCount();

    double getAverageOrderCount();
}