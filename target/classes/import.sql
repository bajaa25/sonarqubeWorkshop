-- Demo data for E-Commerce Workshop
-- Users (5 total)
INSERT INTO PUBLIC.users (id, email, password, first_name, last_name, premium) VALUES (1, 'alice@example.com', 'password123', 'Alice', 'Wonder', true);
INSERT INTO PUBLIC.users (id, email, password, first_name, last_name, premium) VALUES (2, 'bob@example.com', 'admin123', 'Bob', 'Builder', false);
INSERT INTO PUBLIC.users (id, email, password, first_name, last_name, premium) VALUES (3, 'charlie@example.com', 'charlie2024', 'Charlie', 'Brown', true);
INSERT INTO PUBLIC.users (id, email, password, first_name, last_name, premium) VALUES (4, 'diana@example.com', 'diana456', 'Diana', 'Prince', false);
INSERT INTO PUBLIC.users (id, email, password, first_name, last_name, premium) VALUES (5, 'eve@example.com', 'eve789', 'Eve', 'Anderson', true);

-- Orders (10 total)
INSERT INTO PUBLIC.orders (id, user_id, product_name, quantity, price, total_amount, status) VALUES (1, 1, 'MacBook Pro 16', 1, 2499.00, 2249.10, 'DELIVERED');
INSERT INTO PUBLIC.orders (id, user_id, product_name, quantity, price, total_amount, status) VALUES (2, 2, 'iPhone 15 Pro', 2, 1199.00, 2398.00, 'SHIPPED');
INSERT INTO PUBLIC.orders (id, user_id, product_name, quantity, price, total_amount, status) VALUES (3, 3, 'Samsung Galaxy S24', 1, 899.00, 899.00, 'DELIVERED');
INSERT INTO PUBLIC.orders (id, user_id, product_name, quantity, price, total_amount, status) VALUES (4, 1, 'AirPods Pro', 3, 279.00, 837.00, 'DELIVERED');
INSERT INTO PUBLIC.orders (id, user_id, product_name, quantity, price, total_amount, status) VALUES (5, 4, 'iPad Air', 1, 679.00, 679.00, 'PENDING');
INSERT INTO PUBLIC.orders (id, user_id, product_name, quantity, price, total_amount, status) VALUES (6, 5, 'Dell XPS 15 Laptop', 1, 1899.00, 1709.10, 'SHIPPED');
INSERT INTO PUBLIC.orders (id, user_id, product_name, quantity, price, total_amount, status) VALUES (7, 2, 'Sony WH-1000XM5 Headphones', 1, 379.00, 379.00, 'DELIVERED');
INSERT INTO PUBLIC.orders (id, user_id, product_name, quantity, price, total_amount, status) VALUES (8, 3, 'Apple Watch Series 9', 1, 449.00, 404.10, 'PENDING');
INSERT INTO PUBLIC.orders (id, user_id, product_name, quantity, price, total_amount, status) VALUES (9, 4, 'Nintendo Switch OLED', 1, 349.00, 349.00, 'CANCELLED');
INSERT INTO PUBLIC.orders (id, user_id, product_name, quantity, price, total_amount, status) VALUES (10, 5, 'Kindle Paperwhite', 1, 139.00, 125.10, 'DELIVERED');