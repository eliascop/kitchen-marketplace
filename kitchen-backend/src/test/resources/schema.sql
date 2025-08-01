CREATE SCHEMA IF NOT EXISTS kitchen;
SET SCHEMA kitchen;

-- USERS
CREATE TABLE IF NOT EXISTS kitchen.users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  login VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  phone VARCHAR(255),
  email VARCHAR(255),
  user_type VARCHAR(31) NOT NULL DEFAULT 'user',
  paypal_payer_id VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS kitchen.users_roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  roles VARCHAR(255) NOT NULL,
  CONSTRAINT fk_user_role FOREIGN KEY (user_id) REFERENCES kitchen.users(id)
);

-- PRODUCTS
CREATE TABLE IF NOT EXISTS kitchen.product (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  type VARCHAR(50),
  price DECIMAL(10,2),
  active BIT(1) DEFAULT 0
);

-- WALLETS
CREATE TABLE IF NOT EXISTS kitchen.wallets (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  balance DECIMAL(10,2),
  updated_at TIMESTAMP,
  CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES kitchen.users(id)
);

-- WALLET_TRANSACTIONS
CREATE TABLE IF NOT EXISTS kitchen.wallet_transactions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  wallet_id BIGINT NOT NULL,
  type VARCHAR(20), -- CREDIT, DEBIT, etc.
  amount DECIMAL(10,2),
  description VARCHAR(255),
  status VARCHAR(50),
  created_at TIMESTAMP,
  CONSTRAINT fk_wallet_trans_wallet FOREIGN KEY (wallet_id) REFERENCES kitchen.wallets(id)
);

-- ORDERS
CREATE TABLE IF NOT EXISTS kitchen.orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  creation TIMESTAMP,
  status VARCHAR(50),
  total DECIMAL(10,2),
  user_id BIGINT NOT NULL,
  payment_id BIGINT,
  CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES kitchen.users(id)
);

-- ORDER_ITEMS
CREATE TABLE IF NOT EXISTS kitchen.orderItems (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT,
  item_value DECIMAL(10,2),
  CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES kitchen.orders(id),
  CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id) REFERENCES kitchen.product(id)
);
