package ecommerce.controller;

import ecommerce.domain.Order;
import ecommerce.domain.OrderStatus;
import ecommerce.service.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Order Controller
 */
@RestController
@RequestMapping("/api/orders") //BASE URL: Prefix for all Endpoint-Links down below -> e.g. /api/orders/{id}
public class OrderController {

    private static final Logger logger = LogManager.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping //URL: /api/orders
    public List<Order> getAllOrders() {
        logger.info("Getting all orders");
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}") //URL: /api/orders/{id}
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        logger.info("Getting order: " + id);
        Order order = orderService.getOrderById(id);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}") //URL: /api/orders/user/{userId}
    public List<Order> getOrdersByUserId(@PathVariable Long userId) {
        logger.info("Orders for user: " + userId);
        return orderService.getOrdersByUserId(userId);
    }

    @GetMapping("/search")
    public List<Order> searchOrders(@RequestParam String product) {
        logger.info("Searching: " + product);
        return orderService.searchOrdersByProduct(product);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        logger.info("Creating order: " + request.getProductName());

        Order order = orderService.createOrder(
                request.getUserId(),
                request.getProductName(),
                request.getQuantity(),
                request.getPrice()
        );

        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order cancelled");
    }

    @GetMapping("/statistics")
    public ResponseEntity<String> getStatistics() {
        List<Order> orders = orderService.getAllOrders();

        // Calculation im Controller!
        double totalRevenue = 0;
        int totalOrders = orders.size();
        int cancelledOrders = 0;

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            totalRevenue = totalRevenue + order.getTotalAmount();

            if (order.getStatus() == OrderStatus.CANCELLED) {
                cancelledOrders = cancelledOrders + 1;
            }
        }

        double avgOrderValue = totalRevenue / totalOrders;

        String stats = "Orders: " + totalOrders + ", Cancelled: " + cancelledOrders +
                ", Revenue: €" + totalRevenue + ", Avg: €" + avgOrderValue;

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<String> processPayment(
            @PathVariable Long id,
            @RequestParam String creditCard,
            @RequestParam String cvv) {

        logger.info("Payment for order " + id);
        logger.info("Card: " + creditCard + ", CVV: " + cvv);

        boolean success = orderService.processPayment(id, creditCard);

        if (success) {
            return ResponseEntity.ok("Payment processed");
        }

        return ResponseEntity.badRequest().body("Payment failed");
    }

    @PostMapping("/{id}/force-ship")
    public ResponseEntity<String> forceShip(@PathVariable Long id) {
        // No authentication check!
        orderService.shipOrder(id);
        return ResponseEntity.ok("Order shipped");
    }
}

class CreateOrderRequest {
    private Long userId;
    private String productName;
    private int quantity;
    private double price;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}