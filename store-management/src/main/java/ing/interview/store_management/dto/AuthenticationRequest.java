package ing.interview.store_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the request data for user authentication (login)
 */
@Data
@AllArgsConstructor
public class AuthenticationRequest {

    private String username;
    private String password;
}
