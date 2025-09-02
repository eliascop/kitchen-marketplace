-- Inserir usuário admin
INSERT INTO users (id, login, email, phone, name,password) VALUES
(1, 'Administrator', 'admin@kitchen.com', '11-98403-0062', 'administrator', 'admin-12345'),
(2, 'eliascop', 'elias@kitchen.com', '11-98403-1062', 'Elias Oliveira', 'seller-12345'),
(3, 'meire.trb', 'meire.trb@kitchen.com', '11-90403-1062', 'Rose Meire Oliveira', 'user-12345');

-- Inserir role do admin
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_SELLER'),
(3, 'ROLE_USER');

-- Inserir seller
INSERT INTO seller (id, user_id, blocked, store_name) VALUES
(1, 2, false, 'Loja teste');

-- Inserir admin
INSERT INTO admin (user_id, super_admin) VALUES
(1, true);

-- Inserir catalog
INSERT INTO catalog (id,name,slug, seller_id) VALUES
(1, 'Informatica', 'informatica', 1);

-- Inserir Category
INSERT INTO category (id,name) VALUES
(1,'informatica');

-- Inserir produtos
INSERT INTO product (id, name, description, price, image_url, seller_id, catalog_id, category_id, active) VALUES
(1, 'Produto 1', 'Descricao produto 1', 15.50, 'www.image1', 1, 1, 1, true),
(2, 'Produto 2', 'Descricao produto 2', 20.00, 'www.image2', 1, 1, 1, true);

INSERT INTO address (id,type,street,number,district,city,state,zip_code,country,user_id) VALUES
(1,'SHIPPING','RUA','10','BAIRRO','CIDADE','ESTADO','06020-000','BRASIL',3),
(2,'BILLING','RUA2','11','BAIRRO2','CIDADE2','ESTADO2','06020-001','BRASIL',3);

--Inserir carrinho de compra
INSERT INTO cart (id,user_id,creation,active,cart_total, shipping_address_id, billing_address_id) VALUES
(1,3,CURRENT_TIMESTAMP(),true,35.50, 1, 2);

--Inserir items do carrinho de compra
INSERT INTO cart_items (id,cart_id,product_id,quantity,item_value) VALUES
(1,1,1,1,15.50),
(2,1,2,1,20.00);

INSERT INTO shipping (id,carrier,method,cost,estimatedDays,tracking_code,status,cart_id,seller_id) values
(1,'SEDEX','Sedex 10',12.50,5,'abc-123','PENDING',1,1);

-- Inserir pagamento
INSERT INTO payments (id, method, status, amount, gateway_transaction_id, payment_approval_url, secure_token, provider_order_id, created_at, cart_id) values
(1, 'PAYPAL', 'SUCCESS', 35.50, 'ABCDEFGHIJ', 'www.validacao=ABCDEFGHIJ', 'jihgfedcba', '10101520', CURRENT_TIMESTAMP(), 1);

-- Inserir pedido
INSERT INTO orders (id, creation, status, total, user_id, payment_id, shipping_address_id, billing_address_id) VALUES
(1, CURRENT_TIMESTAMP(), 'PENDING', 35.50, 3, 1, 1,2);

INSERT INTO seller_order (id,order_id,seller_id,freight_value,status,created_at,shipping_id) VALUES
(1,1,1,12.50,'DELIVERED',CURRENT_TIMESTAMP(),1);

-- Inserir itens do pedido
INSERT INTO order_items (id, order_id, seller_order_id, product_id, quantity, item_value) VALUES
(1, 1, 1, 1, 1, 15.50),
(2, 1, 1, 2, 1, 20.00);

-- Inserir carteira
INSERT INTO wallets (user_id, balance, updated_at) VALUES
(3, 100.00, CURRENT_TIMESTAMP());

-- Inserir transações da carteira
INSERT INTO wallet_transactions (wallet_id, type, amount, description, status, created_at) VALUES
(1, 'CREDIT', 100.00, 'Depósito inicial', 'SUCCESS', CURRENT_TIMESTAMP());
