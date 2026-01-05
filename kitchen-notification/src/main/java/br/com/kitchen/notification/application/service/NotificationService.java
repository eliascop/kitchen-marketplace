package br.com.kitchen.notification.application.service;

import br.com.kitchen.notification.domain.enums.UserType;
import br.com.kitchen.notification.domain.model.Notification;
import br.com.kitchen.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;

    public void saveAll(List<Notification> notifications) {
        notifications.forEach(notification -> {
            try {
                repository.save(notification);
            } catch (DataIntegrityViolationException ex) {
                log.info("Duplicate notification ignored");
            }
        });
    }

    @Transactional(readOnly = true)
    public List<Notification> findByUser(Long userId, UserType userType) {
        return repository.findByUserIdAndUserTypeOrderByCreatedAtDesc(userId, userType);
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId, UserType userType) {
        return repository.countByUserIdAndUserTypeAndReadFalse(userId, userType);
    }

    @Transactional
    public void markAsRead(UUID id, Long userId, UserType userType) {
        Notification notification = repository
                .findByIdAndUserIdAndUserType(id, userId, userType)
                .orElseThrow(() -> new AccessDeniedException("Forbidden"));

        notification.markAsRead();
    }
}
