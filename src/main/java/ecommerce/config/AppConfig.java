package ecommerce.config;

import org.springframework.context.annotation.Configuration;

/**
 * Application Configuration
 * 
 * SECURITY HOTSPOTS: Unsichere Konfiguration!
 */
@Configuration
public class AppConfig {

    private String databaseUrl = "jdbc:mysql://localhost:3306/ecommerce";
    private String databaseUser = "root";
    private String databasePassword = "password123";

    private String apiBaseUrl = "http://api.example.com";

    private boolean debugMode = true;

    private boolean showStackTraces = true;

    private String jwtSecret = "mySecretKey12345";

    private int sessionTimeoutSeconds = 2592000;
    
    public String getDatabaseUrl() {
        return databaseUrl;
    }
    
    public String getDatabaseUser() {
        return databaseUser;
    }
    
    public String getDatabasePassword() {
        return databasePassword;
    }
    
    public String getApiBaseUrl() {
        return apiBaseUrl;
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
    
    public boolean isShowStackTraces() {
        return showStackTraces;
    }
    
    public String getJwtSecret() {
        return jwtSecret;
    }
    
    public int getSessionTimeoutSeconds() {
        return sessionTimeoutSeconds;
    }

    public String[] getAllowedOrigins() {
        return new String[]{"*"};
    }

    public boolean isRateLimitingEnabled() {
        return false;
    }

    public boolean isAdminUser(String username, String password) {
        return username.equals("admin") && password.equals("admin123");
    }
}
