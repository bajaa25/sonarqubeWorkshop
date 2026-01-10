package ecommerce.service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OrderController
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testGetAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    
    @Test
    void testGetOrderById_Existing() throws Exception {
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("MacBook Pro 16"));
    }
    
    @Test
    void testGetOrderById_NotFound() throws Exception {
        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testGetOrdersByUserId() throws Exception {
        mockMvc.perform(get("/api/orders/user/1"))
                .andExpect(status().isOk());
    }
    
    @Test
    void testSearchOrders() throws Exception {
        mockMvc.perform(get("/api/orders/search")
                .param("product", "MacBook"))
                .andExpect(status().isOk());
    }
    
    @Test
    void testCreateOrder() throws Exception {
        String orderJson = "{\"userId\":1,\"productName\":\"Test Product\"," +
                "\"quantity\":2,\"price\":50.0}";
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isOk());
    }
    
    @Test
    void testCancelOrder() throws Exception {
        mockMvc.perform(put("/api/orders/7/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order cancelled"));
    }
    
    @Test
    void testGetStatistics() throws Exception {
        mockMvc.perform(get("/api/orders/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Orders:")));
    }
    
    @Test
    void testProcessPayment() throws Exception {
        mockMvc.perform(post("/api/orders/1/payment")
                .param("creditCard", "1234567890123456"))
                .andExpect(status().isOk());
    }
    
    @Test
    void testShipOrder() throws Exception {
        mockMvc.perform(post("/api/orders/2/ship"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order shipped"));
    }
}
