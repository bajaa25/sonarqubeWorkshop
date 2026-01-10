package ecommerce.dto;

/**
 * Data Transfer Object for Order Creation
 */
public class CreateOrderRequest {
    private Long userId;
    private String productName;
    private int quantity;
    private double price;

    public CreateOrderRequest() {}

    public CreateOrderRequest(Long userId, String productName, int quantity, double price) {
        this.userId = userId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getUserId() {
        return userId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}