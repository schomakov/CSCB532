-- Минимални данни за локален профил (H2). admin и dimitar: password=password, роля ADMINISTRATOR
INSERT INTO users (username, password, email, role, first_name, last_name) VALUES
('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'admin@company.com', 'ADMINISTRATOR', 'Admin', 'User'),
('dimitar', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'dimitar@company.com', 'ADMINISTRATOR', 'Dimitar', 'User');
