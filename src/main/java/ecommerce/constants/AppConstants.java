package ecommerce.constants;

/**
 * Application Constants
 * Fixed: No more magic numbers!
 */
public final class AppConstants {

    // User Categories
    public static final int BRONZE_THRESHOLD = 5;
    public static final int SILVER_THRESHOLD = 10;
    public static final int GOLD_THRESHOLD = 20;
    public static final int PREMIUM_ELIGIBILITY_ORDERS = 10;
    public static final double PREMIUM_ELIGIBILITY_AMOUNT = 1000.0;

    // Discounts
    public static final double PREMIUM_DISCOUNT = 0.10;
    public static final double BULK_DISCOUNT_5 = 0.03;
    public static final double BULK_DISCOUNT_10 = 0.05;
    public static final double PREMIUM_BULK_DISCOUNT = 0.10;
    public static final double PREMIUM_BULK_HIGH_DISCOUNT = 0.15;

    // Quantity Thresholds
    public static final int BULK_QUANTITY_SMALL = 5;
    public static final int BULK_QUANTITY_LARGE = 10;
    public static final double BULK_AMOUNT_THRESHOLD = 100.0;
    public static final double PAYMENT_LARGE_ORDER_THRESHOLD = 1000.0;

    // Validation
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MIN_NAME_LENGTH = 2;
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }
}