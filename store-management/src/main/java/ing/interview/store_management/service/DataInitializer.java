package ing.interview.store_management.service;

import ing.interview.store_management.model.Permission;
import ing.interview.store_management.model.Role;
import ing.interview.store_management.model.User;
import ing.interview.store_management.repository.PermissionRepository;
import ing.interview.store_management.repository.RoleRepository;
import ing.interview.store_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createUserWithRoles();
    }

    @Transactional
    public void createUserWithRoles() {
        // Create permissions
        Permission readPermission = permissionRepository.findByName("READ")
                .orElseGet(() -> permissionRepository.save(new Permission("READ")));

        Permission writePermission = permissionRepository.findByName("WRITE")
                .orElseGet(() -> permissionRepository.save(new Permission("WRITE")));

        // Ensure roles are persisted or fetched
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role("ADMIN");
                    role.setPermissions(Set.of(readPermission, writePermission));
                    return roleRepository.save(role);
                });

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = new Role("USER");
                    role.setPermissions(Set.of(readPermission));
                    return roleRepository.save(role);
                });

        // Create or find the user "admin"
        userRepository.findByUsername("admin")
                .orElseGet(() -> {
                    User user = new User("admin", passwordEncoder.encode("admin123"));
                    user.setRole(adminRole);
                    return userRepository.save(user);
                });

        // Create or find the user "user"
        userRepository.findByUsername("user")
                .orElseGet(() -> {
                    User user = new User("user", passwordEncoder.encode("password"));
                    user.setRole(userRole);
                    return userRepository.save(user);
                });
    }
}
