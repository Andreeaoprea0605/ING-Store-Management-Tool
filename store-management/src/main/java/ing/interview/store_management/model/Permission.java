package ing.interview.store_management.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * The Permission Do
 */
@Entity
@Data
@Table(name = "store_permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();
}

