package ing.interview.store_management.repository;


import ing.interview.store_management.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom query method to find a user by username
    Optional<User> findByUsername(String username);
}