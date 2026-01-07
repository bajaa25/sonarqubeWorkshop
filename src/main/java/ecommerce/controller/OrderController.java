package ecommerce.controller;

import ecommerce.domain.Order;
import ecommerce.service.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private static final Logger logger = LogManager.getLogger(OrderController.class);
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public List<Order> getAllOrders() {
        logger.info("Fetching all orders");
        return orderService.getAllOrders();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        logger.info("Fetching order with ID: " + id);
        Order order = orderService.getOrderById(id);
        
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Long userId) {
        logger.info("Fetching orders for user: " + userId);
        return orderService.getOrdersByUserId(userId);
    }
    
    @GetMapping("/search")
    public List<Order> searchOrders(@RequestParam String product) {
        logger.info("Searching for orders with product: " + product);
        return orderService.searchOrdersByProduct(product);
    }
    
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        logger.info("Creating order for product: " + request.getProductName());
        
        Order order = orderService.createOrder(
            request.getUserId(),
            request.getProductName(),
            request.getQuantity(),
            request.getPrice()
        );
        
        return ResponseEntity.ok(order);
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
