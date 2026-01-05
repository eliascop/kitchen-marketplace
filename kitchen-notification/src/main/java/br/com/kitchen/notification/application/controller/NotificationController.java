package br.com.kitchen.notification.application.controller;

import br.com.kitchen.notification.application.dto.NotificationResponseDTO;
import br.com.kitchen.notification.domain.enums.UserType;
import br.com.kitchen.notification.application.mapper.NotificationMapper;
import br.com.kitchen.notification.security.UserPrincipal;
import br.com.kitchen.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notification/v1")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> listNotifications(
            @AuthenticationPrincipal UserPrincipal principal) {

        UserType userType = resolveUserType(principal);

        List<NotificationResponseDTO> notifications =
                notificationService.findByUser(principal.userId(), userType)
                        .stream()
                        .map(NotificationMapper::toDTO)
                        .toList();

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                notificationService.countUnread(
                        principal.userId(),
                        resolveUserType(principal)
                )
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAsRead(id, principal.userId(), resolveUserType(principal));
        return ResponseEntity.noContent().build();
    }

    private UserType resolveUserType(UserPrincipal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Unauthenticated");
        }
        if (principal.seller()) {
            return UserType.SELLER;
        }
        if (principal.customer()) {
            return UserType.CUSTOMER;
        }
        throw new AccessDeniedException("Unsupported user type");
    }
}