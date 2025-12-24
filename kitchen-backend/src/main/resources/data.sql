USE kitchen;

INSERT INTO users (login, email, phone, name,password) VALUES
('Administrator', 'admin@kitchen.com', '11-98403-0062', 'administrator', '$2a$10$O4peZtrhK6DmTTgW6BVIwOgtWmfR8XDCxyPZbk7osS97NDiTU3a06'),
('eliascop', 'elias@kitchen.com', '11-98403-1062', 'Elias Oliveira', '$2a$10$O4peZtrhK6DmTTgW6BVIwOgtWmfR8XDCxyPZbk7osS97NDiTU3a06'),
('meire.trb', 'meire.trb@kitchen.com', '11-94074-9459', 'Rose Meire Oliveira', '$2a$10$O4peZtrhK6DmTTgW6BVIwOgtWmfR8XDCxyPZbk7osS97NDiTU3a06'),
('robsonsilva', 'rsilva.82@kitchen.com', '11-98336-5945', 'Robson da Silve Filho', '$2a$10$O4peZtrhK6DmTTgW6BVIwOgtWmfR8XDCxyPZbk7osS97NDiTU3a06'),
('jonascordeiro', 'jonas.cordeiro@kitchen.com', '11-90403-1062', 'Jonas Batista Cordeiro', '$2a$10$O4peZtrhK6DmTTgW6BVIwOgtWmfR8XDCxyPZbk7osS97NDiTU3a06');

INSERT INTO seller (user_id, blocked, store_name) VALUES
(2, false, 'Loja BHP'),
(3, false, 'Loja MeireArruda'),
(4, true, 'Loja RobsonCruzoe');

INSERT INTO admin  (user_id, super_admin) VALUES
(1, true);

INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_SELLER'),
(3, 'ROLE_SELLER'),
(4, 'ROLE_SELLER'),
(5, 'ROLE_USER');

INSERT INTO category (name) VALUES
('UNCATEGORIZED');

INSERT INTO address (city, complement, country, district, number, state,street, `type`,	zip_code, user_id) values
('Barueri','Apto !52 T29','Brasil',	'Vila São João','1398','SP','Avenida Henriqueta Mendes Guerra','SHIPPING','06401905',5),
('Barueri','Apto !52 T29','Brasil',	'Vila São João','1398','SP','Avenida Henriqueta Mendes Guerra','BILLING','06401905',5);