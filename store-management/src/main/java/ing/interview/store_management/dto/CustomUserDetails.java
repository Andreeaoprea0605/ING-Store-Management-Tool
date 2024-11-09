package ing.interview.store_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * This class represents the details of a user for auth process
 */
@AllArgsConstructor
@Data
public class CustomUserDetails implements UserDetails {
    private String username;
    private String password;
    private List<GrantedAuthority> authorities;
}
