package ecommerce.controller;

import ecommerce.domain.Order;
import ecommerce.domain.OrderStatus;
import ecommerce.dto.CreateOrderRequest;
import ecommerce.service.IOrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Order Controller
 * Fixed: No business logic, only routing
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LogManager.getLogger(OrderController.class);

    // Fixed: Using interface
    private final IOrderService orderService;

    @Autowired
    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        logger.info("GET /api/orders");
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        logger.info("GET /api/orders/{}", id);
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Long userId) {
        logger.info("GET /api/orders/user/{}", userId);
        return orderService.getOrdersByUserId(userId);
    }

    @GetMapping("/search")
    public List<Order> searchOrders(@RequestParam String product) {
        logger.info("GET /api/orders/search");
        return orderService.searchOrdersByProduct(product);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        logger.info("POST /api/orders");

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
        logger.info("PUT /api/orders/{}/cancel", id);
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order cancelled");
    }

    @GetMapping("/statistics")
    public ResponseEntity<String> getStatistics() {
        logger.info("GET /api/orders/statistics");

        // Fixed: Simple aggregation only, no complex business logic
        List<Order> orders = orderService.getAllOrders();

        double totalRevenue = orders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        long cancelledOrders = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CANCELLED)
                .count();

        double avgOrderValue = orders.isEmpty() ? 0.0 : totalRevenue / orders.size();

        String stats = String.format("Orders: %d, Cancelled: %d, Revenue: €%.2f, Avg: €%.2f",
                orders.size(), cancelledOrders, totalRevenue, avgOrderValue);

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<String> processPayment(
            @PathVariable Long id,
            @RequestParam String creditCard) {

        // Fixed: No logging of sensitive payment data
        logger.info("POST /api/orders/{}/payment", id);

        boolean success = orderService.processPayment(id, creditCard);

        if (success) {
            return ResponseEntity.ok("Payment processed");
        }

        return ResponseEntity.badRequest().body("Payment failed");
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<String> shipOrder(@PathVariable Long id) {
        // Note: In real app, add authentication check here!
        logger.info("POST /api/orders/{}/ship", id);
        orderService.shipOrder(id);
        return ResponseEntity.ok("Order shipped");
    }
}