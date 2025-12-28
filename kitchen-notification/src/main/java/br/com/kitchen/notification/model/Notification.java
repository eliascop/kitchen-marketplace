package br.com.kitchen.notification.model;

import br.com.kitchen.notification.enums.NotificationType;
import br.com.kitchen.notification.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications",schema = "notification_service")
@NoArgsConstructor
@Data
public class Notification {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private Long referenceId;

    @Column(name = "external_reference_id", nullable = false)
    private Long externalReferenceId;

    @Column(name = "read_flag")
    private boolean read = false;

    private LocalDateTime createdAt;
    public void markAsRead() {
        this.read = true;
    }
}