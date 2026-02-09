-- Users (passwords are bcrypt-hashed)
INSERT INTO users (id, username, password, email, role, first_name, last_name) VALUES
    (1, 'admin', '$2a$10$6PSIH5sVDZvcq3VR2J2YT.3pXqdBI..asG.z30myo6fZw24pwT4jW', 'admin@company.com', 'ADMINISTRATOR', 'Admin', 'User')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, username, password, email, role, first_name, last_name) VALUES
    (2, 'emp.office', '$2a$10$6PSIH5sVDZvcq3VR2J2YT.3pXqdBI..asG.z30myo6fZw24pwT4jW', 'office@company.com', 'EMPLOYEE', 'Evelyn', 'Brown'),
    (3, 'emp.courier', '$2a$10$6PSIH5sVDZvcq3VR2J2YT.3pXqdBI..asG.z30myo6fZw24pwT4jW', 'courier@company.com', 'EMPLOYEE', 'George', 'Miller')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, username, password, email, role, first_name, last_name) VALUES
    (4, 'client.alex',  '$2a$10$6PSIH5sVDZvcq3VR2J2YT.3pXqdBI..asG.z30myo6fZw24pwT4jW', 'alex@domain.com',  'CLIENT', 'Alex',  'Cole'),
    (5, 'client.maria', '$2a$10$6PSIH5sVDZvcq3VR2J2YT.3pXqdBI..asG.z30myo6fZw24pwT4jW', 'maria@domain.com', 'CLIENT', 'Maria', 'Smith')
ON CONFLICT (id) DO NOTHING;

-- Companies
INSERT INTO companies (id, name, registration_number, headquarters_address_country, headquarters_address_city, headquarters_address_zip_code, headquarters_address_street, headquarters_address_details, description, created_at, updated_at) VALUES
    (1, 'LogiCo', 'BG123456789', 'Bulgaria', 'Sofia', '1000', 'Liberty Ave 1', 'HQ Floor 2', 'Main logistics company', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Offices
INSERT INTO offices (id, company_id, name, address_country, address_city, address_zip_code, address_street, address_details, phone, email, working_hours, active) VALUES
    (1, 1, 'Sofia Center', 'Bulgaria', 'Sofia', '1000', 'Main St 10', 'Ground floor', '+359 2 123 456', 'sofia.center@logico.com', 'Mon-Fri 9:00-18:00', true),
    (2, 1, 'Plovdiv East', 'Bulgaria', 'Plovdiv', '4000', 'River Blvd 25', NULL, '+359 32 222 333', 'plovdiv.east@logico.com', 'Mon-Fri 9:00-18:00', true)
ON CONFLICT (id) DO NOTHING;

-- Employees (IDs refer to users IDs)
INSERT INTO employees (id, company_id, office_id, type, phone, active) VALUES
    (2, 1, 1, 'OFFICE_EMPLOYEE', '+359 888 111 222', true),
    (3, 1, 2, 'COURIER',         '+359 888 333 444', true)
ON CONFLICT (id) DO NOTHING;

-- Clients (IDs refer to users IDs)
INSERT INTO clients (id, phone, default_address_country, default_address_city, default_address_zip_code, default_address_street, default_address_details) VALUES
    (4, '+359 888 555 666', 'Bulgaria', 'Sofia',   '1000', 'Oak Street 7',  'Apt 12'),
    (5, '+359 888 777 888', 'Bulgaria', 'Plovdiv', '4000', 'Maple Street 15', 'Floor 3')
ON CONFLICT (id) DO NOTHING;

-- Parcels
-- REGISTERED
INSERT INTO parcels (id, sender_id, recipient_name, recipient_phone, recipient_client_id, delivery_type, from_office_id, to_office_id, delivery_address_country, delivery_address_city, delivery_address_zip_code, delivery_address_street, delivery_address_details, courier_id, registered_by_employee_id, weight_kg, price, status, tracking_code, created_at, updated_at, delivered_at) VALUES
    (1, 4, 'John Peterson',   '+359 888 000 111', NULL, 'TO_OFFICE', 1, 2, NULL, NULL, NULL, NULL, NULL, 3, 2, 1.20, 7.80, 'REGISTERED', 'TRK-REG001', NOW(), NOW(), NULL)
ON CONFLICT (id) DO NOTHING;

-- AT_OFFICE
INSERT INTO parcels (id, sender_id, recipient_name, recipient_phone, recipient_client_id, delivery_type, from_office_id, to_office_id, delivery_address_country, delivery_address_city, delivery_address_zip_code, delivery_address_street, delivery_address_details, courier_id, registered_by_employee_id, weight_kg, price, status, tracking_code, created_at, updated_at, delivered_at) VALUES
    (2, 5, 'Peter Johnson',  '+359 888 000 222', NULL, 'TO_OFFICE', 2, 1, NULL, NULL, NULL, NULL, NULL, 3, 2, 2.50, 9.75, 'AT_OFFICE',   'TRK-ATF002', NOW(), NOW(), NULL)
ON CONFLICT (id) DO NOTHING;

-- IN_TRANSIT
INSERT INTO parcels (id, sender_id, recipient_name, recipient_phone, recipient_client_id, delivery_type, from_office_id, to_office_id, delivery_address_country, delivery_address_city, delivery_address_zip_code, delivery_address_street, delivery_address_details, courier_id, registered_by_employee_id, weight_kg, price, status, tracking_code, created_at, updated_at, delivered_at) VALUES
    (3, 4, 'Mary George', '+359 888 000 333', NULL, 'TO_ADDRESS', 1, NULL, 'Bulgaria', 'Plovdiv', '4000', 'Market St 3', 'Near corner', 3, 2, 0.90, 8.35, 'IN_TRANSIT', 'TRK-TRN003', NOW(), NOW(), NULL)
ON CONFLICT (id) DO NOTHING;

-- DELIVERED to registered client recipient
INSERT INTO parcels (id, sender_id, recipient_name, recipient_phone, recipient_client_id, delivery_type, from_office_id, to_office_id, delivery_address_country, delivery_address_city, delivery_address_zip_code, delivery_address_street, delivery_address_details, courier_id, registered_by_employee_id, weight_kg, price, status, tracking_code, created_at, updated_at, delivered_at) VALUES
    (4, 5, 'Alex Cole', '+359 888 000 444', 4, 'TO_ADDRESS', 2, NULL, 'Bulgaria', 'Sofia', '1000', 'Moscow St 2', NULL, 3, 2, 3.10, 12.65, 'DELIVERED', 'TRK-DLV004', NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 days', NOW() - INTERVAL '1 days')
ON CONFLICT (id) DO NOTHING;

-- Sequences sync (PostgreSQL)
SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 1), true);
SELECT setval('companies_id_seq',          COALESCE((SELECT MAX(id) FROM companies), 1), true);
SELECT setval('offices_id_seq',            COALESCE((SELECT MAX(id) FROM offices), 1), true);
SELECT setval('parcels_id_seq',            COALESCE((SELECT MAX(id) FROM parcels), 1), true);

COMMIT;

