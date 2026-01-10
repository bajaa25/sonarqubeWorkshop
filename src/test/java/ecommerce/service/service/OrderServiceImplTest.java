package ecommerce.service.service;

import ecommerce.domain.Order;
import ecommerce.domain.OrderStatus;
import ecommerce.repository.OrderRepository;
import ecommerce.service.IOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OrderServiceImpl
 */
@SpringBootTest
class OrderServiceImplTest {
    
    @Autowired
    private IOrderService orderService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Test
    void testGetAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        assertNotNull(orders);
        assertEquals(11, orders.size());
    }
    
    @Test
    void testGetOrderById_ExistingOrder() {
        Optional<Order> order = orderService.getOrderById(1L);
        assertTrue(order.isPresent());
        assertEquals("MacBook Pro 16", order.get().getProductName());
    }
    
    @Test
    void testGetOrderById_NonExistingOrder() {
        Optional<Order> order = orderService.getOrderById(999L);
        assertFalse(order.isPresent());
    }
    
    @Test
    void testGetOrdersByUserId() {
        List<Order> orders = orderService.getOrdersByUserId(1L);
        assertNotNull(orders);
        assertFalse(orders.isEmpty());
    }
    
    @Test
    void testSearchOrdersByProduct() {
        List<Order> orders = orderService.searchOrdersByProduct("MacBook");
        assertNotNull(orders);
        assertTrue(orders.stream().anyMatch(o -> o.getProductName().contains("MacBook")));
    }
    
    @Test
    void testCreateOrder_ValidUser() {
        Order order = orderService.createOrder(1L, "Test Product", 2, 100.0);
        assertNotNull(order);
        assertNotNull(order.getId());
        assertEquals("Test Product", order.getProductName());
        assertEquals(2, order.getQuantity());
    }
    
    @Test
    void testCreateOrder_InvalidUser() {
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(999L, "Test Product", 1, 100.0);
        });
    }
    
    @Test
    void testCalculateOrderTotal_StandardUser_SmallQuantity() {
        double total = orderService.calculateOrderTotal(100.0, 3, false);
        assertEquals(300.0, total, 0.01);
    }
    
    @Test
    void testCalculateOrderTotal_StandardUser_MediumQuantity() {
        double total = orderService.calculateOrderTotal(100.0, 7, false);
        // Should apply 3% discount
        assertEquals(679.0, total, 1.0);
    }
    
    @Test
    void testCalculateOrderTotal_StandardUser_LargeQuantity() {
        double total = orderService.calculateOrderTotal(100.0, 15, false);
        // Should apply 5% discount
        assertEquals(1425.0, total, 1.0);
    }
    
    @Test
    void testCalculateOrderTotal_PremiumUser_SmallQuantity() {
        double total = orderService.calculateOrderTotal(100.0, 3, true);
        // Should apply 10% discount
        assertEquals(270.0, total, 1.0);
    }
    
    @Test
    void testCalculateOrderTotal_PremiumUser_LargeQuantity_LowAmount() {
        double total = orderService.calculateOrderTotal(10.0, 7, true);
        // Should apply 10% discount
        assertEquals(63.0, total, 1.0);
    }
    
    @Test
    void testCalculateOrderTotal_PremiumUser_LargeQuantity_HighAmount() {
        double total = orderService.calculateOrderTotal(100.0, 7, true);
        // Should apply 15% discount
        assertEquals(595.0, total, 1.0);
    }
    
    @Test
    void testCancelOrder_ExistingOrder() {
        assertDoesNotThrow(() -> {
            orderService.cancelOrder(5L);
        });
        
        Optional<Order> order = orderService.getOrderById(5L);
        assertTrue(order.isPresent());
        assertEquals(OrderStatus.CANCELLED, order.get().getStatus());
    }
    
    @Test
    void testCancelOrder_NonExistingOrder() {
        assertDoesNotThrow(() -> {
            orderService.cancelOrder(999L);
        });
    }
    
    @Test
    void testUpdateOrderStatus() {
        orderService.updateOrderStatus(6L, OrderStatus.PROCESSING);
        
        Optional<Order> order = orderService.getOrderById(6L);
        assertTrue(order.isPresent());
        assertEquals(OrderStatus.PROCESSING, order.get().getStatus());
    }
    
    @Test
    void testProcessPayment_LargeOrder() {
        // Order with total > 1000 should succeed
        boolean result = orderService.processPayment(1L, "1234567890123456");
        assertTrue(result);
    }
    
    @Test
    void testProcessPayment_SmallOrder() {
        // Order with total < 1000 should fail
        boolean result = orderService.processPayment(7L, "1234567890123456");
        assertFalse(result);
    }
    
    @Test
    void testProcessPayment_NonExistingOrder() {
        boolean result = orderService.processPayment(999L, "1234567890123456");
        assertFalse(result);
    }
    
    @Test
    void testShipOrder_ExistingOrder() {
        assertDoesNotThrow(() -> {
            orderService.shipOrder(8L);
        });
        
        Optional<Order> order = orderService.getOrderById(8L);
        assertTrue(order.isPresent());
        assertEquals(OrderStatus.SHIPPED, order.get().getStatus());
    }
    
    @Test
    void testShipOrder_NonExistingOrder() {
        assertDoesNotThrow(() -> {
            orderService.shipOrder(999L);
        });
    }
}
