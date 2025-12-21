CREATE SCHEMA IF NOT EXISTS kitchen;
USE kitchen;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  login VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  phone VARCHAR(255),
  email VARCHAR(255)
);

-- Inserir usuário admin
INSERT INTO users (login, email, phone, name,password) VALUES
('Administrator', 'admin@kitchen.com', '11-98403-0062', 'administrator', '$2a$10$O4peZtrhK6DmTTgW6BVIwOgtWmfR8XDCxyPZbk7osS97NDiTU3a06'),
('eliascop', 'elias@kitchen.com', '11-98403-1062', 'Elias Oliveira', '$2a$10$O4peZtrhK6DmTTgW6BVIwOgtWmfR8XDCxyPZbk7osS97NDiTU3a06'),
('jonascordeiro', 'jonas.cordeiro@kitchen.com', '11-90403-1062', 'Jonas Batista Cordeiro', '$2a$10$O4peZtrhK6DmTTgW6BVIwOgtWmfR8XDCxyPZbk7osS97NDiTU3a06');

CREATE TABLE IF NOT EXISTS seller (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    store_name VARCHAR(255) NOT NULL,
    blocked BIT(1) DEFAULT 0,
    CONSTRAINT fk_seller FOREIGN KEY (user_id) REFERENCES kitchen.users(id) ON DELETE CASCADE
);

INSERT INTO seller (user_id, blocked, store_name) VALUES (2, false, 'Loja BHP');

CREATE TABLE IF NOT EXISTS admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    super_admin BIT(1) DEFAULT 0,
    CONSTRAINT fk_admin FOREIGN KEY (user_id) REFERENCES kitchen.users(id) ON DELETE CASCADE
);

INSERT INTO admin  (user_id, super_admin)
VALUES (1, true);

CREATE TABLE IF NOT EXISTS user_roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  role VARCHAR(255) NOT NULL,
  CONSTRAINT fk_user_roles FOREIGN KEY (user_id) REFERENCES kitchen.users(id) ON DELETE CASCADE
);

INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_SELLER'),
(3, 'ROLE_USER');

CREATE TABLE IF NOT EXISTS category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO category (name) VALUES
('UNCATEGORIZED');