CREATE SCHEMA IF NOT EXISTS kitchen;
SET SCHEMA kitchen;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  login VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  phone VARCHAR(255),
  email VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users_roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  roles VARCHAR(255) NOT NULL,
  CONSTRAINT fk_user_role FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS seller (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    store_name VARCHAR(255) NOT NULL,
    blocked BIT(1) DEFAULT 0,
    CONSTRAINT fk_seller FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    super_admin BIT(1) DEFAULT 0,
    CONSTRAINT fk_admin FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  role VARCHAR(255) NOT NULL,
  CONSTRAINT fk_user_roles FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS catalog (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  slug VARCHAR(255) NOT NULL,
  seller_id BIGINT NOT NULL,
  CONSTRAINT fk_catalog FOREIGN KEY (seller_id) REFERENCES seller(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS product (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  base_price DECIMAL(10,2),
  image_url VARCHAR(255),
  seller_id BIGINT NOT NULL,
  catalog_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  active BIT(1) DEFAULT 0,
  CONSTRAINT fk_product_seller FOREIGN KEY (seller_id) REFERENCES seller(id)
);

CREATE TABLE IF NOT EXISTS address (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  type VARCHAR(50),
  street VARCHAR(255),
  number VARCHAR(50),
  complement VARCHAR(255),
  district VARCHAR(255),
  city VARCHAR(255),
  state VARCHAR(255),
  zip_code VARCHAR(50),
  country VARCHAR(50),
  user_id BIGINT NOT NULL,
  CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS cart (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  creation TIMESTAMP,
  active BIT(1),
  cart_total DECIMAL(10,2),
  shipping_address_id BIGINT,
  billing_address_id BIGINT,
  CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_cart_shipping_address FOREIGN KEY (shipping_address_id) REFERENCES address(id),
  CONSTRAINT fk_cart_billing_address FOREIGN KEY (billing_address_id) REFERENCES address(id)
);

CREATE TABLE IF NOT EXISTS cart_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  cart_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT,
  item_value DECIMAL(10,2),
  CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES cart(id),
  CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE IF NOT EXISTS payments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  method VARCHAR(255),
  status VARCHAR(255),
  amount DECIMAL(10,2) NOT NULL,
  gateway_transaction_id VARCHAR(255),
  payment_approval_url VARCHAR(255),
  secure_token VARCHAR(255) UNIQUE,
  provider_order_id VARCHAR(255),
  created_at TIMESTAMP,
  cart_id BIGINT NOT NULL,
  CONSTRAINT fk_payment_cart FOREIGN KEY (cart_id) REFERENCES cart(id)
);

CREATE TABLE IF NOT EXISTS orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  creation TIMESTAMP,
  status VARCHAR(50),
  total DECIMAL(10,2),
  user_id BIGINT NOT NULL,
  payment_id BIGINT,
  shipping_address_id BIGINT,
  billing_address_id BIGINT,
  CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_order_payment FOREIGN KEY (payment_id) REFERENCES payments(id),
  CONSTRAINT fk_order_shipping_address FOREIGN KEY (shipping_address_id) REFERENCES address(id),
  CONSTRAINT fk_order_billing_address FOREIGN KEY (billing_address_id) REFERENCES address(id)
);

-- ORDER_ITEMS
CREATE TABLE IF NOT EXISTS order_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  seller_order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT,
  item_value DECIMAL(10,2),
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE IF NOT EXISTS shipping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    carrier VARCHAR(255),
    method VARCHAR(255),
    cost DECIMAL(10,2),
    estimatedDays INT,
    tracking_code VARCHAR(255),
    status VARCHAR(255),
    cart_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    CONSTRAINT fk_shipping_cart_id FOREIGN KEY (cart_id) REFERENCES cart(id),
    CONSTRAINT fk_shipping_seller_id FOREIGN KEY (seller_id) REFERENCES seller(id)
);

CREATE TABLE IF NOT EXISTS seller_order (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  seller_id BIGINT NOT NULL,
  freight_value DECIMAL(10,2),
  status VARCHAR(255),
  created_at TIMESTAMP,
  shipping_id BIGINT NOT NULL,
  CONSTRAINT fk_seller_order_order_id FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_seller_order_seller_id FOREIGN KEY (seller_id) REFERENCES seller(id),
  CONSTRAINT fk_seller_order_shipping_id FOREIGN KEY (shipping_id) REFERENCES shipping(id)
);

-- WALLETS
CREATE TABLE IF NOT EXISTS wallets (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  balance DECIMAL(10,2),
  updated_at TIMESTAMP,
  CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- WALLET_TRANSACTIONS
CREATE TABLE IF NOT EXISTS wallet_transactions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  wallet_id BIGINT NOT NULL,
  type VARCHAR(20),
  amount DECIMAL(10,2),
  description VARCHAR(255),
  status VARCHAR(50),
  created_at TIMESTAMP,
  CONSTRAINT fk_wallet_trans_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);
