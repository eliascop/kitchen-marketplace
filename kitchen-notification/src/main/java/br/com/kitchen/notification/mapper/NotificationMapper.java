package br.com.kitchen.notification.mapper;

import br.com.kitchen.notification.dto.NotificationResponseDTO;
import br.com.kitchen.notification.model.Notification;

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
