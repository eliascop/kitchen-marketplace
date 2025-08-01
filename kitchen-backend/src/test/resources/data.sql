-- Inserir usuários
INSERT INTO kitchen.users (login, password, name, phone, email, user_type, paypal_payer_id)
VALUES
('john_admin', '123123', 'John Admin', '11988888888','admin@example.com', 'user', NULL),
('john_doe', '123456', 'John Doe', '11999999999', 'john@example.com', 'user', NULL);

-- Inserir produtos
INSERT INTO kitchen.product (name, description, type, price)
VALUES
  ('Arroz Doce', 'Arroz doce tradicional', 'MEAL', 15.50),
  ('Feijão Tropeiro', 'Feijão tropeiro mineiro', 'MEAL', 20.00),
  ('Suco de Laranja', 'Suco natural', 'DRINK', 7.00);

-- Inserir pedido
INSERT INTO kitchen.orders (creation, status, total, user_id, payment_id)
VALUES (CURRENT_TIMESTAMP(), 'PENDING', 35.50, 2, NULL);

-- Inserir itens do pedido
INSERT INTO kitchen.orderItems (order_id, product_id, quantity, item_value)
VALUES
  (1, 1, 1, 15.50),
  (1, 2, 1, 20.00);

-- Inserir carteira
INSERT INTO kitchen.wallets (user_id, balance, updated_at)
VALUES (1, 100.00, CURRENT_TIMESTAMP());

-- Inserir transações da carteira
INSERT INTO kitchen.wallet_transactions (wallet_id, type, amount, description, status, created_at)
VALUES
  (1, 'CREDIT', 100.00, 'Depósito inicial', 'SUCCESS', CURRENT_TIMESTAMP());
