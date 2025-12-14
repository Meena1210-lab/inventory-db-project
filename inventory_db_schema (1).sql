-- ===================================
-- INVENTORY MANAGEMENT DATABASE SCHEMA
-- ===================================

-- Drop existing tables if any
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS users;

-- 1. SUPPLIERS TABLE
CREATE TABLE suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    supplier_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(15),
    email VARCHAR(100),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. PRODUCTS TABLE
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT DEFAULT 0 CHECK (quantity >= 0),
    supplier_id INT,
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
    reorder_level INT DEFAULT 10,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL
);

-- 3. TRANSACTIONS TABLE
CREATE TABLE transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    transaction_type ENUM('IN', 'OUT') NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarks TEXT,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

-- 4. USERS TABLE (Optional - for authentication)
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===================================
-- SAMPLE DATA INSERTION
-- ===================================

-- Insert sample suppliers
INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) VALUES
('TechSupply Co.', 'John Smith', '9876543210', 'john@techsupply.com', '123 Tech Street, Mumbai'),
('ElectroMart', 'Sarah Johnson', '9876543211', 'sarah@electromart.com', '456 Electronic Ave, Delhi'),
('Office Depot', 'Mike Brown', '9876543212', 'mike@officedepot.com', '789 Supply Road, Bangalore');

-- Insert sample products
INSERT INTO products (product_name, category, quantity, supplier_id, unit_price, reorder_level) VALUES
('Dell Laptop i5', 'Electronics', 25, 1, 45000.00, 5),
('HP Printer LaserJet', 'Electronics', 15, 1, 12000.00, 3),
('Logitech Mouse Wireless', 'Accessories', 100, 2, 850.00, 20),
('Samsung Monitor 24inch', 'Electronics', 30, 2, 15000.00, 5),
('Office Chair Ergonomic', 'Furniture', 50, 3, 5500.00, 10),
('Whiteboard Markers Pack', 'Stationery', 200, 3, 150.00, 50),
('Kingston USB 32GB', 'Accessories', 80, 1, 450.00, 25);

-- Insert sample transactions
INSERT INTO transactions (product_id, transaction_type, quantity, remarks) VALUES
(1, 'IN', 10, 'Initial stock purchase'),
(1, 'OUT', 2, 'Sold to customer XYZ'),
(3, 'IN', 50, 'Bulk order restocking'),
(3, 'OUT', 15, 'Office supplies order'),
(5, 'IN', 20, 'Furniture inventory update');

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'ADMIN'),
('user1', 'user123', 'USER');

-- ===================================
-- USEFUL VIEWS
-- ===================================

-- View: Low Stock Alert
CREATE VIEW low_stock_items AS
SELECT 
    p.product_id,
    p.product_name,
    p.category,
    p.quantity,
    p.reorder_level,
    s.supplier_name
FROM products p
LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id
WHERE p.quantity <= p.reorder_level;

-- View: Inventory Value
CREATE VIEW inventory_value AS
SELECT 
    category,
    COUNT(*) as total_products,
    SUM(quantity) as total_quantity,
    SUM(quantity * unit_price) as total_value
FROM products
GROUP BY category;

-- ===================================
-- VERIFICATION QUERIES
-- ===================================

-- Check all products
SELECT * FROM products;

-- Check low stock items
SELECT * FROM low_stock_items;

-- Check total inventory value
SELECT * FROM inventory_value;

-- Check all transactions
SELECT 
    t.transaction_id,
    p.product_name,
    t.transaction_type,
    t.quantity,
    t.transaction_date
FROM transactions t
JOIN products p ON t.product_id = p.product_id
ORDER BY t.transaction_date DESC;