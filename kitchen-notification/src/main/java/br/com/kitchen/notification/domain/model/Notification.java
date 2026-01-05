package br.com.kitchen.notification.domain.model;

import br.com.kitchen.notification.domain.enums.NotificationType;
import br.com.kitchen.notification.domain.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "notifications",
        schema = "notification_service",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notification_unique",
                        columnNames = {
                                "user_id",
                                "user_type",
                                "type",
                                "reference_id",
                                "external_reference_id"
                        }
                )
        }
)

@NoArgsConstructor
@Data
public class Notification {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(name = "external_reference_id", nullable = false)
    private Long externalReferenceId;

    @Column(name = "read_flag", columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private boolean read = false;

    private LocalDateTime createdAt;
    public void markAsRead() {
        this.read = true;
    }
}