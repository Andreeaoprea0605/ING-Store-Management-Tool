package ing.interview.store_management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * The JwtUtil class to handle the generation, parsing and validation of JWT tokens
 */
@Component
public class JwtUtil {

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("your-256-bit-secret".getBytes());

    @Value("${jwt.expirationMillis}")
    private long expirationMillis;

    /**
     * Generates a JWT token with a configurable expiration time
     *
     * @param username The username to include in the token
     * @return The generated JWT token
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis)) // Custom expiration
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Validates the token, checking the signature and expiration
     *
     * @param token    The JWT token
     * @param username The username to validate against
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    /**
     * Verifies the token's signature and expiration, and validates the username
     *
     * @param token    The JWT token
     * @param username The username to match the token's subject
     * @return true if the token is valid, false if expired or invalid
     */
    public boolean isTokenValid(String token, String username) {
        try {
            return validateToken(token, username);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts the username (subject) from the token
     *
     * @param token The JWT token
     * @return The username (subject) extracted from the token
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Checks if the token has expired
     *
     * @param token The JWT token
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    /**
     * Extracts claims from the JWT token
     *
     * @param token The JWT token
     * @return Claims extracted from the token
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
