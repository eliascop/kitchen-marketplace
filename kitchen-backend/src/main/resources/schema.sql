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

CREATE TABLE IF NOT EXISTS seller (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     user_id BIGINT NOT NULL,
     store_name VARCHAR(255) NOT NULL,
     blocked BIT(1) DEFAULT 0,
     CONSTRAINT fk_seller FOREIGN KEY (user_id) REFERENCES kitchen.users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS admin (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     user_id BIGINT NOT NULL,
     super_admin BIT(1) DEFAULT 0,
     CONSTRAINT fk_admin FOREIGN KEY (user_id) REFERENCES kitchen.users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_roles (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     user_id BIGINT NOT NULL,
     role VARCHAR(255) NOT NULL,
     CONSTRAINT fk_user_roles FOREIGN KEY (user_id) REFERENCES kitchen.users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS category (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS address (
     id bigint AUTO_INCREMENT PRIMARY KEY,
     city varchar(255) DEFAULT NULL,
     complement varchar(255) DEFAULT NULL,
     country varchar(255) DEFAULT NULL,
     district varchar(255) DEFAULT NULL,
     number varchar(255) DEFAULT NULL,
     state varchar(255) DEFAULT NULL,
     street varchar(255) DEFAULT NULL,
     type enum('BILLING','SHIPPING') DEFAULT NULL,
     zip_code varchar(255) DEFAULT NULL,
     user_id bigint DEFAULT NULL,
     CONSTRAINT fk_address FOREIGN KEY (user_id) REFERENCES kitchen.users(id) ON DELETE CASCADE
);
