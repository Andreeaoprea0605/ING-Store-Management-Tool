package ing.interview.store_management.controller;

import ing.interview.store_management.dto.AuthenticationRequest;
import ing.interview.store_management.dto.AuthenticationResponse;
import ing.interview.store_management.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles authentication endpoints for example the login
 * Provides token-based authentication for the users
 */
@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Handles the user login by authenticating the user and returning a JWT token
     *
     * @param authenticationRequest The authentication request information (username and password).
     * @return ResponseEntity containing the JWT token.
     */
    @PostMapping
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());

            authenticationManager.authenticate(authenticationToken);
            String jwt = jwtUtil.generateToken(authenticationToken);
            return ResponseEntity.ok(new AuthenticationResponse(jwt));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
