package ing.interview.store_management.security;

import ing.interview.store_management.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;

/**
 * The SecurityConfig class used to configure the Spring Security setup for the store management app
 * It ensures that the application is secure, and only authorized users with the correct roles can access certain endpoints.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Define the AuthenticationManager Bean
     *
     * @param http The HttpSecurity object
     * @return AuthenticationManager
     * @throws Exception If an error occurs while configuring security
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // AuthenticationManager using the UserDetailsService
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    /**
     * Configures HTTP security to set up authentication and authorization rules for the app
     *
     * @param http The HttpSecurity object used to configure security settings for HTTP requests
     * @return SecurityFilterChain that defines the security filters and behavior
     * @throws Exception If an error occurs while configuring security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Define security settings for HTTP requests
        http
                .csrf().disable()  // Disable CSRF protection for stateless JWT authentication
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/authenticate", "/api/public/**").permitAll()
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/user/**", "/api/products/**","/api/users/**").hasAnyRole("USER", "ADMIN")
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures the password encoder to hash passwords using BCrypt.
     *
     * @return PasswordEncoder object (BCryptPasswordEncoder)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
