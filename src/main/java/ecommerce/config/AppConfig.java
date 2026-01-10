package ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Application Configuration
 * Fixed: No hardcoded secrets, using environment variables
 */
@Configuration
public class AppConfig {

    // Fixed: Using environment variables instead of hardcoded values
    @Value("${database.url:jdbc:h2:mem:testdb}")
    private String databaseUrl;

    @Value("${database.user:sa}")
    private String databaseUser;

    @Value("${database.password:${DB_PASSWORD:}}")
    private String databasePassword;

    // Fixed: HTTPS instead of HTTP
    @Value("${api.base.url:https://api.example.com}")
    private String apiBaseUrl;

    @Value("${debug.mode:false}")
    private boolean debugMode;

    @Value("${show.stack.traces:false}")
    private boolean showStackTraces;

    @Value("${jwt.secret:${JWT_SECRET:}}")
    private String jwtSecret;

    // Fixed: Using constants, 30 days in seconds
    @Value("${session.timeout.seconds:2592000}")
    private int sessionTimeoutSeconds;

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

    // Fixed: Specific origins instead of wildcard
    public String[] getAllowedOrigins() {
        return new String[]{"http://localhost:3000", "https://yourdomain.com"};
    }

    // Fixed: Rate limiting enabled
    public boolean isRateLimitingEnabled() {
        return true;
    }
}