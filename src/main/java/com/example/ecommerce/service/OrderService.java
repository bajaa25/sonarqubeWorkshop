package com.example.ecommerce.service;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderStatus;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class OrderService {
    
    private static final Logger logger = LogManager.getLogger(OrderService.class);
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
    
    public List<Order> getOrdersByUserId(Long userId) {
        logger.info("Fetching orders for user: " + userId);
        return orderRepository.findByUserId(userId);
    }
    
    @SuppressWarnings("unchecked")
    public List<Order> searchOrdersByProduct(String productName) {
        String query = "SELECT * FROM orders WHERE product_name LIKE '%" + productName + "%'";
        logger.warn("Searching orders with query: " + query);
        return entityManager.createNativeQuery(query, Order.class).getResultList();
    }
    
    public Order createOrder(Long userId, String productName, int quantity, double price) {
        logger.info("Creating order for product: " + productName);
        
        User user = userRepository.findById(userId).orElse(null);
        
        Order order = new Order();
        order.setUser(user);
        order.setProductName(productName);
        order.setQuantity(quantity);
        order.setPrice(price);
        
        double total = calculateTotal(price, quantity, user.isPremium());
        order.setTotalAmount(total);
        
        return orderRepository.save(order);
    }
    
    private double calculateTotal(double price, int quantity, boolean isPremium) {
        double total = price * quantity;
        
        if (isPremium) {
            if (quantity > 5) {
                if (total > 100) {
                    total = total * 0.85;
                } else {
                    total = total * 0.9;
                }
            } else {
                total = total * 0.9;
            }
        } else {
            if (quantity > 10) {
                total = total * 0.95;
            }
        }
        
        return total;
    }
    
    public void cancelOrder(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
            }
        } catch (Exception e) {
        }
    }
}
