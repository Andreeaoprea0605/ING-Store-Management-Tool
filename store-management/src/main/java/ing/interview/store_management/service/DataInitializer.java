package ing.interview.store_management.service;

import ing.interview.store_management.model.Role;
import ing.interview.store_management.model.User;
import ing.interview.store_management.repository.RoleRepository;
import ing.interview.store_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *      This class initialize and save users with roles in an H2 in-memory
 * database, which is suitable for development and testing purposes. In a production environment,
 * a persistent database such as PostgreSQL/MySQL should be used instead.
 *      H2 is used in this project for its simplicity and ease of setup, allowing focus on
 * demonstrating functionality without needing extensive database configuration. For production,
 * I will configure a persistent relational database to ensure data durability and security.
 */
@Service
public class DataInitializer implements CommandLineRunner{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createUserWithRoles();
    }

    @Transactional
    public void createUserWithRoles() {
        // Ensure roles are persisted or fetched
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role("ADMIN");
                    return roleRepository.save(role);  // Save the new "ADMIN" role
                });

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = new Role("USER");
                    return roleRepository.save(role);  // Save the new "USER" role
                });

        // Create or find the user "admin"
        userRepository.findByUsername("admin")
                .orElseGet(() -> {
                    User user = new User("admin", passwordEncoder.encode("admin123"));
                    user.getRoles().add(adminRole);  // Add the admin role to the user
                    return userRepository.save(user);  // Save the user (roles will be persisted)
                });

        // Create or find the user "user"
        userRepository.findByUsername("user")
                .orElseGet(() -> {
                    User user = new User("user", passwordEncoder.encode("password"));
                    user.getRoles().add(userRole);  // Add the user role to the user
                    return userRepository.save(user);  // Save the user (roles will be persisted)
                });

        // Print the created users and roles for verification
        System.out.println("Andreea: " + userRepository.findAll());
        System.out.println("Andreea: " + roleRepository.findAll());
    }
}
