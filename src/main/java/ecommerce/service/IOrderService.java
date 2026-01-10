package ecommerce.service;

import ecommerce.domain.Order;
import ecommerce.domain.OrderStatus;
import java.util.List;
import java.util.Optional;

/**
 * Order Service Interface
 * Fixed: Adaptability - using interfaces
 */
public interface IOrderService {

    List<Order> getAllOrders();

    Optional<Order> getOrderById(Long id);

    List<Order> getOrdersByUserId(Long userId);

    List<Order> searchOrdersByProduct(String productName);

    Order createOrder(Long userId, String productName, int quantity, double price);

    void cancelOrder(Long orderId);

    void updateOrderStatus(Long orderId, OrderStatus status);

    boolean processPayment(Long orderId, String creditCard);

    void shipOrder(Long orderId);

    double calculateOrderTotal(double price, int quantity, boolean isPremium);
}
