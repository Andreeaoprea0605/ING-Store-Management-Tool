package ing.interview.store_management.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller is designed to demonstrate the security configuration in a Spring application.
 * It does not include actual business logic or data handling. The primary focus is to showcase
 * how Spring Security manages access control based on user roles and permissions.
 * The endpoints provide various operations that are restricted based on the user's assigned roles
 * and permissions:
 * - Users with the "USER" role can access endpoints marked with "READ" permission.
 * - Users with the "ADMIN" role can access endpoints marked with either "READ" or "WRITE" permissions.
*/
@RestController
@RequestMapping("/api")
public class StoreManagementController {

    /**
     * This endpoint is available to users with the "ADMIN" role.
     *
     * @return A message confirming access to the admin dashboard.
     */
    @GetMapping("/admin/dashboard")
    public String getAdminDashboard() {
        return "Welcome to the admin dashboard!";
    }

    /**
     * This endpoint is available to users with either "USER" or "ADMIN" roles.
     *
     * @return A message confirming access to the user profile.
     */
    @PreAuthorize("hasAuthority('READ')")
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

    /**
     * This endpoint is only available to users with the "ADMIN" role.
     * It shows a message indicating the access to a write operation endpoint.
     *
     * @return A message confirming access to a write-enabled endpoint.
     */
    @PreAuthorize("hasAuthority('WRITE')")
    @GetMapping("/admin/write")
    public String getAdminWriteAccess() {
        return "You have WRITE access to the admin endpoint!";
    }

    /**
     * This endpoint is only available to users with the "USER" role or "ADMIN" role, and has READ access.
     * It demonstrates that users with READ access can perform a read operation.
     *
     * @return A message confirming that the user has read access.
     */
    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/user/read")
    public String getUserReadAccess() {
        return "You have READ access to the user endpoint!";
    }

    /**
     * This endpoint is only available to users with the "ADMIN" role and allows for a write operation.
     * It displays a message indicating that only admins can write data.
     *
     * @return A message confirming that the admin has write access.
     */
    @PreAuthorize("hasAuthority('WRITE')")
    @GetMapping("/admin/edit")
    public String getAdminEditAccess() {
        return "Admin can edit data here!";
    }

    /**
     * This endpoint demonstrates that a "USER" role cannot access the write operation.
     * If a user without the "WRITE" permission tries to access, they will get a 403 Forbidden error.
     *
     * @return A message explaining the restriction for users without write access.
     */
    @PreAuthorize("hasAuthority('WRITE')")
    @GetMapping("/user/edit")
    public String getUserEditAccess() {
        return "Users are not authorized to edit data!";
    }
}