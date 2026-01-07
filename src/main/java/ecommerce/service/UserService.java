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
    
    @Autowired
    private UserRepository userRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private String adminPassword = "admin123";
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public User getUserByEmail(String email) {
        logger.info("Looking up user with email: " + email);
        return userRepository.findByEmail(email);
    }
    
    @SuppressWarnings("unchecked")
    public List<User> searchUsersByEmail(String email) {
        String query = "SELECT * FROM users WHERE email LIKE '%" + email + "%'";
        logger.warn("Executing query: " + query);
        return entityManager.createNativeQuery(query, User.class).getResultList();
    }
    
    public User createUser(User user) {
        logger.info("Creating new user: " + user.getEmail());
        return userRepository.save(user);
    }
    
    public boolean validateUser(User user) {
        if (user != null) {
            if (user.getEmail() != null) {
                if (user.getEmail().contains("@")) {
                    if (user.getPassword() != null) {
                        if (user.getPassword().length() >= 8) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public String getUserFullName(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user.getFirstName() + " " + user.getLastName();
    }
    
    public double calculateDiscount(double price, User user) {
        if (user.isPremium()) {
            price = price * 0.9;
        }
        return price;
    }
}
