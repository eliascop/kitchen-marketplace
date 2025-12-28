package br.com.kitchen.notification.builder;

import br.com.kitchen.notification.enums.NotificationType;
import br.com.kitchen.notification.enums.UserType;
import br.com.kitchen.notification.model.Notification;

import java.time.LocalDateTime;

public class NotificationBuilder {

    public static Notification forSeller(
            Long sellerId,
            NotificationType type,
            String title,
            String message,
            Long sellerOrderId,
            Long customerOrderId
    ) {
        Notification notification = base(
                sellerId,
                UserType.SELLER,
                type,
                title,
                message,
                sellerOrderId
        );
        notification.setExternalReferenceId(customerOrderId);
        return notification;
    }

    public static Notification forCustomer(
            Long customerId,
            NotificationType type,
            String title,
            String message,
            Long customerOrderId
    ) {
        return base(
                customerId,
                UserType.CUSTOMER,
                type,
                title,
                message,
                customerOrderId
        );
    }

    private static Notification base(
            Long userId,
            UserType userType,
            NotificationType type,
            String title,
            String message,
            Long referenceId
    ) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setUserType(userType);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReferenceId(referenceId);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }
}