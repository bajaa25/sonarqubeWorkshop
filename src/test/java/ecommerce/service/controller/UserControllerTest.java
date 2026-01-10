package ecommerce.service.controller;

import ecommerce.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private IUserService userService;
    
    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    
    @Test
    void testGetUserById_Existing() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }
    
    @Test
    void testGetUserById_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testSearchUsers() throws Exception {
        mockMvc.perform(get("/api/users/search")
                .param("email", "alice"))
                .andExpect(status().isOk());
    }
    
    @Test
    void testCreateUser() throws Exception {
        String userJson = "{\"email\":\"newuser@test.com\",\"password\":\"securepass123\"," +
                "\"firstName\":\"New\",\"lastName\":\"User\",\"premium\":false}";
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk());
    }
    
    @Test
    void testCalculateDiscount_ExistingUser() throws Exception {
        mockMvc.perform(get("/api/users/1/discount")
                .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Amount:")));
    }
    
    @Test
    void testCalculateDiscount_NonExistingUser() throws Exception {
        mockMvc.perform(get("/api/users/999/discount")
                .param("amount", "100"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testGetUserSummary_ExistingUser() throws Exception {
        mockMvc.perform(get("/api/users/1/summary"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("User:")));
    }
    
    @Test
    void testGetUserSummary_NonExistingUser() throws Exception {
        mockMvc.perform(get("/api/users/999/summary"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testGetStatistics() throws Exception {
        mockMvc.perform(get("/api/users/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Total:")));
    }
}
