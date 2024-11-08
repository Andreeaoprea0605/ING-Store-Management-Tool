package ing.interview.store_management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StoreManagementController {

    /**
     * This endpoint is available to users with the "ADMIN" role.
     *
     * @return A message confirming access to the admin endpoint.
     */
    @GetMapping("/admin/dashboard")
    public String getAdminDashboard() {
        return "Welcome to the admin dashboard!";
    }

    /**
     * This endpoint is available to users with either "USER" or "ADMIN" roles.
     *
     * @return A message confirming access to the user endpoint.
     */
    @GetMapping("/user/profile")
    public String getUserProfile() {
        return "Welcome to your user profile!";
    }

    /**
     * This endpoint is public and available to everyone.
     *
     * @return A message confirming access to a public endpoint.
     */
    @GetMapping("/public")
    public String getPublicInfo() {
        return "This is a public endpoint, accessible by everyone!";
    }
}