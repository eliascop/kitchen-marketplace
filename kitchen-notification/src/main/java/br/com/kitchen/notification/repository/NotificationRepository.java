package br.com.kitchen.notification.repository;

import br.com.kitchen.notification.domain.enums.UserType;
import br.com.kitchen.notification.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserIdAndUserTypeOrderByCreatedAtDesc(Long userId, UserType userType);
    Optional<Notification> findByIdAndUserIdAndUserType(UUID id, Long userId, UserType userType);
    long countByUserIdAndUserTypeAndReadFalse(Long userId, UserType userType);
}