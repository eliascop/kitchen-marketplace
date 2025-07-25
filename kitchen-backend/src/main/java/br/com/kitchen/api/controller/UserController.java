package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.UserDTO;
import br.com.kitchen.api.model.User;
import br.com.kitchen.api.record.CustomUserDetails;
import br.com.kitchen.api.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users/v1")
@SecurityRequirement(name = "bearer-key")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDTO> findAll() {
        return service.findAllUsers();
    }

    @GetMapping("/{id}")
    public  ResponseEntity<?> findUserById(@PathVariable Long id) {
        return service.findUserById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found", "code", 404)));
    }

    @GetMapping("/search")
    public List<UserDTO> findByName(@RequestParam String name) {
        return service.findUserByName(name);
    }

    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        User userUpdated = service.updateUser(userDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserDTO(userUpdated));
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/seller/update")
    public ResponseEntity<?> updateSellerStore(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestParam String storeName, boolean storeStatus) {
        try {

            service.updateSellerStore(userDetails.user(), storeName, storeStatus);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of(
                            "message", "Store [" + storeName + "] was updated ",
                            "code", HttpStatus.OK.value()
                    ));
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "code", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "An error occurred while update Store",
                            "details", ex.getMessage()
                    ));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/promote/admin")
    public ResponseEntity<?> promoteUserToAdmin(@RequestParam String login) {
        service.promoteToAdmin(login);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "User ["+ login + "] is now ADMIN.",
                        "code", HttpStatus.OK.value()
                ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/promote/seller")
    public ResponseEntity<?> promoteUserToSeller(@RequestParam String login, String storeName) {
        service.promoteToSeller(login, storeName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "User ["+ login + "] is now Seller.",
                        "code", HttpStatus.OK.value()
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            Optional<User> user = service.findById(id);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "message", "User not found",
                                "code", HttpStatus.NOT_FOUND.value()
                        ));
            }

            service.deleteUser(id);
            return ResponseEntity.ok(Map.of(
                    "message", "User deleted successfully",
                    "code", HttpStatus.OK.value()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "code", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "An error occurred while deleting the user",
                            "details", e.getMessage()
                    ));
        }
    }
}
