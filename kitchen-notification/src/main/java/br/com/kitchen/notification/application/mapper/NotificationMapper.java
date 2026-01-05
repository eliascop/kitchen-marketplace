package br.com.kitchen.notification.application.mapper;

import br.com.kitchen.notification.application.dto.NotificationResponseDTO;
import br.com.kitchen.notification.domain.model.Notification;

public class NotificationMapper {
    public static NotificationResponseDTO toDTO(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .referenceId(notification.getReferenceId())
                .externalReferenceId(notification.getExternalReferenceId())
                .build();
    }

}
