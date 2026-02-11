-- Пълни данни за локален профил (H2). Всички пароли: password (bcrypt)
-- Потребители: admin, dimitar (ADMIN), emp.office, emp.courier (EMPLOYEE), client.alex, client.maria (CLIENT)
INSERT INTO users (id, username, password, email, role, first_name, last_name) VALUES
(1, 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'admin@company.com', 'ADMINISTRATOR', 'Admin', 'User'),
(2, 'dimitar', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'dimitar@company.com', 'ADMINISTRATOR', 'Dimitar', 'User'),
(3, 'emp.office', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'office@company.com', 'EMPLOYEE', 'Evelyn', 'Brown'),
(4, 'emp.courier', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'courier@company.com', 'EMPLOYEE', 'George', 'Miller'),
(5, 'client.alex', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'alex@domain.com', 'CLIENT', 'Alex', 'Cole'),
(6, 'client.maria', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'maria@domain.com', 'CLIENT', 'Maria', 'Smith');

-- Компании
INSERT INTO companies (id, name, registration_number, headquarters_address_country, headquarters_address_city, headquarters_address_zip_code, headquarters_address_street, headquarters_address_details, description, created_at, updated_at) VALUES
(1, 'LogiCo', 'BG123456789', 'Bulgaria', 'Sofia', '1000', 'Liberty Ave 1', 'HQ Floor 2', 'Main logistics company', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Офиси
INSERT INTO offices (id, company_id, name, address_country, address_city, address_zip_code, address_street, address_details, phone, email, working_hours, active) VALUES
(1, 1, 'Sofia Center', 'Bulgaria', 'Sofia', '1000', 'Main St 10', 'Ground floor', '+359 2 123 456', 'sofia.center@logico.com', 'Mon-Fri 9:00-18:00', true),
(2, 1, 'Plovdiv East', 'Bulgaria', 'Plovdiv', '4000', 'River Blvd 25', NULL, '+359 32 222 333', 'plovdiv.east@logico.com', 'Mon-Fri 9:00-18:00', true);

-- Служители (id = user id)
INSERT INTO employees (id, company_id, office_id, type, phone, active) VALUES
(3, 1, 1, 'OFFICE_EMPLOYEE', '+359 888 111 222', true),
(4, 1, 2, 'COURIER', '+359 888 333 444', true);

-- Клиенти (id = user id)
INSERT INTO clients (id, phone, default_address_country, default_address_city, default_address_zip_code, default_address_street, default_address_details) VALUES
(5, '+359 888 555 666', 'Bulgaria', 'Sofia', '1000', 'Oak Street 7', 'Apt 12'),
(6, '+359 888 777 888', 'Bulgaria', 'Plovdiv', '4000', 'Maple Street 15', 'Floor 3');

-- Пратки
-- Пратки
INSERT INTO parcels (id, sender_id, recipient_name, recipient_phone, recipient_client_id, delivery_type, from_office_id, to_office_id, delivery_address_country, delivery_address_city, delivery_address_zip_code, delivery_address_street, delivery_address_details, courier_id, registered_by_employee_id, weight_kg, price, status, tracking_code, created_at, updated_at, delivered_at) VALUES
(1, 5, 'John Peterson', '+359 888 000 111', NULL, 'TO_OFFICE', 1, 2, NULL, NULL, NULL, NULL, NULL, 4, 3, 1.20, 7.80, 'REGISTERED', 'TRK-REG001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(2, 6, 'Peter Johnson', '+359 888 000 222', NULL, 'TO_OFFICE', 2, 1, NULL, NULL, NULL, NULL, NULL, 4, 3, 2.50, 9.75, 'AT_OFFICE', 'TRK-ATF002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(3, 5, 'Mary George', '+359 888 000 333', NULL, 'TO_ADDRESS', 1, NULL, 'Bulgaria', 'Plovdiv', '4000', 'Market St 3', 'Near corner', 4, 3, 0.90, 8.35, 'IN_TRANSIT', 'TRK-TRN003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(4, 6, 'Alex Cole', '+359 888 000 444', 5, 'TO_ADDRESS', 2, NULL, 'Bulgaria', 'Sofia', '1000', 'Moscow St 2', NULL, 4, 3, 3.10, 12.65, 'DELIVERED', 'TRK-DLV004', DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP));

-- H2: синхронизиране на IDENTITY за следващи вмъквания
ALTER TABLE users ALTER COLUMN id RESTART WITH 7;
ALTER TABLE companies ALTER COLUMN id RESTART WITH 2;
ALTER TABLE offices ALTER COLUMN id RESTART WITH 3;
ALTER TABLE parcels ALTER COLUMN id RESTART WITH 5;
