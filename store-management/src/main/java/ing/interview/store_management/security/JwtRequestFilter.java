package ing.interview.store_management.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * The JwtRequestFilter will intercept each request, check if the JWT is valid, if it is then auth the user
 */
@Component
@WebFilter("/*")
public class JwtRequestFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = Logger.getLogger(JwtRequestFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String token = httpRequest.getHeader("Authorization");
        String username;

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            try {
                // Extract username and validate the token
                username = jwtUtil.extractUsername(token);
                if (jwtUtil.isTokenValid(token, username)) {
                    // Extract user roles (if applicable) and set authentication
                    List<GrantedAuthority> authorities = jwtUtil.getAuthorities(token);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // Token is invalid or expired
                    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpResponse.getWriter().write("Invalid or expired token");
                    return;
                }
            } catch (Exception e) {
                // Catch parsing or any other exceptions
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Invalid token format");
                return;
            }
        } else {
            // Token is missing or incorrectly formatted
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.getWriter().write("Authorization header missing or incorrect format");
            return;
        }

        // Continue the filter chain if the token is valid
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("JWT Request Filter Initialized");
        String jwtExpirationTime = filterConfig.getInitParameter("jwtExpirationTime");
        if (jwtExpirationTime != null) {
            logger.info("JWT Expiration Time: " + jwtExpirationTime);
        }
    }

    @Override
    public void destroy() {
        logger.info("JWT Request Filter Destroyed");
    }
}
