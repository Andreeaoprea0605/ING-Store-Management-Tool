package ing.interview.store_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * The SecurityConfig used to define the user details service for authentication.
 * Observation: I have chosen to use an in-memory user manager based on the scope of this application
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the UserDetailsService for authentication.
     *
     * @return An InMemoryUserDetailsManager to manage in-memory users.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        // Create an admin user with role "ADMIN" and one with "USER" with a password stored as plain text
        manager.createUser(User.withUsername("admin").password("{noop}admin123").roles("ADMIN").build());
        manager.createUser(User.withUsername("user").password("{noop}user123").roles("USER").build());
        return manager;
    }

    /**
     * Configures security rules for the application, including authorization based on user roles and enabling authentication.
     *
     * @param http HttpSecurity object used to configure security settings for the application
     * @return SecurityFilterChain object that defines the security filters
     * @throws Exception if an error occurs while configuring security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configure authorization rules for HTTP requests based on URL patterns
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/**").permitAll()
                )
                .formLogin(withDefaults())
                .httpBasic(withDefaults());

        return http.build();
    }
}
