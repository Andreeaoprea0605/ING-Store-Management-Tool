package ing.interview.store_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the response data after user authentication (login)
 */
@Data
@AllArgsConstructor
public class AuthenticationResponse {

    private String jwt;
}
