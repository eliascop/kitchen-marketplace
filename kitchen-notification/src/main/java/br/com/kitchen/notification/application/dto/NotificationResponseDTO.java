package br.com.kitchen.notification.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {

    private final UUID id;
    private final String title;
    private final String message;
    private final boolean read;
    private final LocalDateTime createdAt;
    private final Long referenceId;
    private final Long externalReferenceId;

}