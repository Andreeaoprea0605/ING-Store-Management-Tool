package ing.interview.store_management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * @param authentication The authentication object containing the user details
     * @return The generated JWT token
     */
    public String generateToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", authentication.getAuthorities());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Extract roles from the token
     *
     * @param token The JWT token
     * @return Roles extracted from the token
     */
    public List<GrantedAuthority> getAuthorities(String token) {
        Claims claims = getClaims(token);
        List<String> roles = (List<String>) claims.get("roles");
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
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
