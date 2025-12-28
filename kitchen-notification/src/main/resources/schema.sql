CREATE SCHEMA IF NOT EXISTS notification_service;
USE notification_service;

CREATE TABLE IF NOT EXISTS notifications (
 id BINARY(16) NOT NULL,
 created_at DATETIME(6),
 external_reference_id BIGINT NOT NULL,
 message TEXT,
 is_read BIT NOT NULL,
 reference_id BIGINT NOT NULL,
 title VARCHAR(255),
 type VARCHAR(50) NOT NULL,
 user_id BIGINT NOT NULL,
 user_type VARCHAR(50) NOT NULL,
 PRIMARY KEY (id)
) ENGINE=InnoDB;

ALTER TABLE notifications
    ADD CONSTRAINT uk_notification_unique
        UNIQUE (
                user_id,
                user_type,
                type,
                reference_id,
                external_reference_id
            );