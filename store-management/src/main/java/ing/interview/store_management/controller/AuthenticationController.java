package ing.interview.store_management.controller;

import ing.interview.store_management.dto.AuthenticationRequest;
import ing.interview.store_management.dto.AuthenticationResponse;
import ing.interview.store_management.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles authentication endpoints for example the login
 * Provides token-based authentication for the users
 */
@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Handles the user login by authenticating the user and returning a JWT token
     *
     * @param credentials The login credentials (username and password).
     * @return ResponseEntity containing the JWT token.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest credentials) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getUsername(),
                        credentials.getPassword()
                )
        );

        String jwt = jwtUtil.generateToken(authentication);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
