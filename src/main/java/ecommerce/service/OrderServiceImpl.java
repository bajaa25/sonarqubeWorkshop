package ecommerce.service;

import ecommerce.constants.AppConstants;
import ecommerce.domain.Order;
import ecommerce.domain.OrderStatus;
import ecommerce.domain.User;
import ecommerce.repository.OrderRepository;
import ecommerce.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Order Service Implementation
 * Fixed: No hardcoded secrets, no SQL injection, proper error handling
 */
@Service
public class OrderServiceImpl implements IOrderService {

    private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // Fixed: No hardcoded secrets - using environment variables
    @Value("${payment.api.key:${PAYMENT_API_KEY:}}")
    private String paymentApiKey;

    @Value("${shipping.secret:${SHIPPING_SECRET:}}")
    private String shippingSecret;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        logger.info("Getting orders for user: {}", userId);
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> searchOrdersByProduct(String productName) {
        // Fixed: No SQL injection - using repository method
        logger.info("Searching orders by product");
        return orderRepository.searchByProductName(productName);
    }

    @Override
    public Order createOrder(Long userId, String productName, int quantity, double price) {
        logger.info("Creating order: {}", productName);

        // Fixed: Proper null handling
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        User user = userOpt.get();

        Order order = new Order();
        order.setUser(user);
        order.setProductName(productName);
        order.setQuantity(quantity);
        order.setPrice(price);

        double total = calculateOrderTotal(price, quantity, user.isPremium());
        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    @Override
    public double calculateOrderTotal(double price, int quantity, boolean isPremium) {
        // Fixed: Reduced cognitive complexity, using constants
        double total = price * quantity;

        if (!isPremium) {
            return applyStandardDiscount(total, quantity);
        }

        return applyPremiumDiscount(total, quantity);
    }

    private double applyStandardDiscount(double total, int quantity) {
        if (quantity > AppConstants.BULK_QUANTITY_LARGE) {
            return total * (1 - AppConstants.BULK_DISCOUNT_10);
        }
        if (quantity > AppConstants.BULK_QUANTITY_SMALL) {
            return total * (1 - AppConstants.BULK_DISCOUNT_5);
        }
        return total;
    }

    private double applyPremiumDiscount(double total, int quantity) {
        if (quantity <= AppConstants.BULK_QUANTITY_SMALL) {
            return total * (1 - AppConstants.PREMIUM_DISCOUNT);
        }

        if (total > AppConstants.BULK_AMOUNT_THRESHOLD) {
            return total * (1 - AppConstants.PREMIUM_BULK_HIGH_DISCOUNT);
        }

        return total * (1 - AppConstants.PREMIUM_BULK_DISCOUNT);
    }

    @Override
    public void cancelOrder(Long orderId) {
        // Fixed: Proper error handling instead of empty catch
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                logger.info("Order cancelled: {}", orderId);
            } else {
                logger.warn("Order not found for cancellation: {}", orderId);
            }
        } catch (Exception e) {
            logger.error("Failed to cancel order: {}", orderId, e);
            throw new RuntimeException("Failed to cancel order", e);
        }
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Optional<Order> orderOpt = getOrderById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            orderRepository.save(order);
            logger.info("Order status updated: {}", orderId);
        } else {
            logger.warn("Order not found: {}", orderId);
        }
    }

    @Override
    public boolean processPayment(Long orderId, String creditCard) {
        Optional<Order> orderOpt = getOrderById(orderId);
        if (orderOpt.isEmpty()) {
            return false;
        }

        Order order = orderOpt.get();

        // Fixed: No logging of sensitive payment data!
        logger.info("Processing payment for order: {}", orderId);
        logger.debug("Using payment API");

        // Fixed: Using constants
        return order.getTotalAmount() > AppConstants.PAYMENT_LARGE_ORDER_THRESHOLD;
    }

    @Override
    public void shipOrder(Long orderId) {
        Optional<Order> orderOpt = getOrderById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            // Fixed: No logging of secrets!
            logger.info("Shipping order: {}", orderId);
            order.setStatus(OrderStatus.SHIPPED);
            orderRepository.save(order);
        } else {
            logger.warn("Order not found: {}", orderId);
        }
    }
}