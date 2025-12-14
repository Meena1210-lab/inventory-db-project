import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Smart Inventory Management System
 * Main Application Class with Console Interface
 */
public class InventoryManagementSystem {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/inventory_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Meena_1210";

    private static Connection connection = null;
    private static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Database connected successfully!\n");

            // Show main menu
            showMainMenu();

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    // ===================================
    // MAIN MENU
    // ===================================
    private static void showMainMenu() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║   INVENTORY MANAGEMENT SYSTEM          ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  1. Product Management                 ║");
            System.out.println("║  2. Stock Operations                   ║");
            System.out.println("║  3. Supplier Management                ║");
            System.out.println("║  4. Reports                            ║");
            System.out.println("║  5. Exit                               ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("Enter your choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1: productMenu(); break;
                case 2: stockMenu(); break;
                case 3: supplierMenu(); break;
                case 4: reportsMenu(); break;
                case 5:
                    System.out.println("\n✓ Thank you for using the system!");
                    return;
                default:
                    System.out.println("✗ Invalid choice! Try again.");
            }
        }
    }

    // ===================================
    // PRODUCT MANAGEMENT MENU
    // ===================================
    private static void productMenu() {
        while (true) {
            System.out.println("\n--- PRODUCT MANAGEMENT ---");
            System.out.println("1. Add Product");
            System.out.println("2. Update Product");
            System.out.println("3. Delete Product");
            System.out.println("4. Search Product");
            System.out.println("5. Display All Products");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1: addProduct(); break;
                case 2: updateProduct(); break;
                case 3: deleteProduct(); break;
                case 4: searchProduct(); break;
                case 5: displayAllProducts(); break;
                case 6: return;
                default: System.out.println("✗ Invalid choice!");
            }
        }
    }

    // Add new product
    private static void addProduct() {
        try {
            System.out.println("\n--- ADD NEW PRODUCT ---");

            scanner.nextLine(); // Clear buffer
            System.out.print("Product Name: ");
            String name = scanner.nextLine();

            System.out.print("Category: ");
            String category = scanner.nextLine();

            System.out.print("Quantity: ");
            int quantity = getIntInput();

            System.out.print("Supplier ID: ");
            int supplierId = getIntInput();

            System.out.print("Unit Price: ");
            double unitPrice = getDoubleInput();

            System.out.print("Reorder Level: ");
            int reorderLevel = getIntInput();

            String sql = "INSERT INTO products (product_name, category, quantity, supplier_id, unit_price, reorder_level) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setInt(3, quantity);
            pstmt.setInt(4, supplierId);
            pstmt.setDouble(5, unitPrice);
            pstmt.setInt(6, reorderLevel);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✓ Product added successfully!");
            }

            pstmt.close();

        } catch (SQLException e) {
            System.err.println("✗ Error adding product: " + e.getMessage());
        }
    }

    // Update product
    private static void updateProduct() {
        try {
            System.out.print("\nEnter Product ID to update: ");
            int productId = getIntInput();

            // Check if product exists
            if (!productExists(productId)) {
                System.out.println("✗ Product not found!");
                return;
            }

            scanner.nextLine(); // Clear buffer
            System.out.print("New Product Name (or press Enter to skip): ");
            String name = scanner.nextLine();

            System.out.print("New Category (or press Enter to skip): ");
            String category = scanner.nextLine();

            System.out.print("New Unit Price (or 0 to skip): ");
            double price = getDoubleInput();

            StringBuilder sql = new StringBuilder("UPDATE products SET ");
            boolean hasUpdate = false;

            if (!name.isEmpty()) {
                sql.append("product_name = '").append(name).append("'");
                hasUpdate = true;
            }

            if (!category.isEmpty()) {
                if (hasUpdate) {
					sql.append(", ");
				}
                sql.append("category = '").append(category).append("'");
                hasUpdate = true;
            }

            if (price > 0) {
                if (hasUpdate) {
					sql.append(", ");
				}
                sql.append("unit_price = ").append(price);
                hasUpdate = true;
            }

            if (!hasUpdate) {
                System.out.println("✗ No updates provided!");
                return;
            }

            sql.append(" WHERE product_id = ").append(productId);

            Statement stmt = connection.createStatement();
            int rows = stmt.executeUpdate(sql.toString());

            if (rows > 0) {
                System.out.println("✓ Product updated successfully!");
            }

            stmt.close();

        } catch (SQLException e) {
            System.err.println("✗ Error updating product: " + e.getMessage());
        }
    }

    // Delete product
    private static void deleteProduct() {
        try {
            System.out.print("\nEnter Product ID to delete: ");
            int productId = getIntInput();

            if (!productExists(productId)) {
                System.out.println("✗ Product not found!");
                return;
            }

            scanner.nextLine();
            System.out.print("Are you sure? (yes/no): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("yes")) {
                String sql = "DELETE FROM products WHERE product_id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, productId);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("✓ Product deleted successfully!");
                }
                pstmt.close();
            } else {
                System.out.println("✗ Deletion cancelled.");
            }

        } catch (SQLException e) {
            System.err.println("✗ Error deleting product: " + e.getMessage());
        }
    }

    // Search product
    private static void searchProduct() {
        try {
            scanner.nextLine();
            System.out.print("\nSearch by (1) ID or (2) Name: ");
            int choice = getIntInput();

            String sql;
            PreparedStatement pstmt;

            if (choice == 1) {
                System.out.print("Enter Product ID: ");
                int id = getIntInput();
                sql = "SELECT * FROM products WHERE product_id = ?";
                pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, id);
            } else {
                scanner.nextLine();
                System.out.print("Enter Product Name: ");
                String name = scanner.nextLine();
                sql = "SELECT * FROM products WHERE product_name LIKE ?";
                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, "%" + name + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            displayProductResults(rs);

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("✗ Error searching product: " + e.getMessage());
        }
    }

    // Display all products
    private static void displayAllProducts() {
        try {
            String sql = "SELECT p.*, s.supplier_name FROM products p " +
                         "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                         "ORDER BY p.product_id";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n" + "=".repeat(120));
            System.out.printf("%-5s %-25s %-15s %-10s %-20s %-12s %-10s%n",
                "ID", "Name", "Category", "Quantity", "Supplier", "Price", "Reorder");
            System.out.println("=".repeat(120));

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                System.out.printf("%-5d %-25s %-15s %-10d %-20s ₹%-11.2f %-10d%n",
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getString("supplier_name") != null ? rs.getString("supplier_name") : "N/A",
                    rs.getDouble("unit_price"),
                    rs.getInt("reorder_level")
                );
            }

            if (!hasResults) {
                System.out.println("No products found.");
            }

            System.out.println("=".repeat(120));

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("✗ Error displaying products: " + e.getMessage());
        }
    }

    // ===================================
    // STOCK OPERATIONS MENU
    // ===================================
    private static void stockMenu() {
        while (true) {
            System.out.println("\n--- STOCK OPERATIONS ---");
            System.out.println("1. Stock In (Add Stock)");
            System.out.println("2. Stock Out (Remove Stock)");
            System.out.println("3. View Transaction History");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1: stockIn(); break;
                case 2: stockOut(); break;
                case 3: viewTransactions(); break;
                case 4: return;
                default: System.out.println("✗ Invalid choice!");
            }
        }
    }

    // Stock In operation
    private static void stockIn() {
        try {
            System.out.print("\nEnter Product ID: ");
            int productId = getIntInput();

            if (!productExists(productId)) {
                System.out.println("✗ Product not found!");
                return;
            }

            System.out.print("Enter Quantity to Add: ");
            int quantity = getIntInput();

            if (quantity <= 0) {
                System.out.println("✗ Quantity must be positive!");
                return;
            }

            scanner.nextLine();
            System.out.print("Remarks (optional): ");
            String remarks = scanner.nextLine();

            // Update product quantity
            String updateSql = "UPDATE products SET quantity = quantity + ? WHERE product_id = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateSql);
            updateStmt.setInt(1, quantity);
            updateStmt.setInt(2, productId);
            updateStmt.executeUpdate();

            // Record transaction
            String transSql = "INSERT INTO transactions (product_id, transaction_type, quantity, remarks) " +
                             "VALUES (?, 'IN', ?, ?)";
            PreparedStatement transStmt = connection.prepareStatement(transSql);
            transStmt.setInt(1, productId);
            transStmt.setInt(2, quantity);
            transStmt.setString(3, remarks);
            transStmt.executeUpdate();

            System.out.println("✓ Stock added successfully!");

            updateStmt.close();
            transStmt.close();

        } catch (SQLException e) {
            System.err.println("✗ Error in stock in: " + e.getMessage());
        }
    }

    // Stock Out operation
    private static void stockOut() {
        try {
            System.out.print("\nEnter Product ID: ");
            int productId = getIntInput();

            if (!productExists(productId)) {
                System.out.println("✗ Product not found!");
                return;
            }

            // Check current stock
            int currentStock = getCurrentStock(productId);
            System.out.println("Current Stock: " + currentStock);

            System.out.print("Enter Quantity to Remove: ");
            int quantity = getIntInput();

            if (quantity <= 0) {
                System.out.println("✗ Quantity must be positive!");
                return;
            }

            if (quantity > currentStock) {
                System.out.println("✗ Insufficient stock! Available: " + currentStock);
                return;
            }

            scanner.nextLine();
            System.out.print("Remarks (optional): ");
            String remarks = scanner.nextLine();

            // Update product quantity
            String updateSql = "UPDATE products SET quantity = quantity - ? WHERE product_id = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateSql);
            updateStmt.setInt(1, quantity);
            updateStmt.setInt(2, productId);
            updateStmt.executeUpdate();

            // Record transaction
            String transSql = "INSERT INTO transactions (product_id, transaction_type, quantity, remarks) " +
                             "VALUES (?, 'OUT', ?, ?)";
            PreparedStatement transStmt = connection.prepareStatement(transSql);
            transStmt.setInt(1, productId);
            transStmt.setInt(2, quantity);
            transStmt.setString(3, remarks);
            transStmt.executeUpdate();

            System.out.println("✓ Stock removed successfully!");
            System.out.println("Remaining Stock: " + (currentStock - quantity));

            updateStmt.close();
            transStmt.close();

        } catch (SQLException e) {
            System.err.println("✗ Error in stock out: " + e.getMessage());
        }
    }

    // View transaction history
    private static void viewTransactions() {
        try {
            String sql = "SELECT t.transaction_id, p.product_name, t.transaction_type, " +
                         "t.quantity, t.transaction_date, t.remarks " +
                         "FROM transactions t " +
                         "JOIN products p ON t.product_id = p.product_id " +
                         "ORDER BY t.transaction_date DESC LIMIT 20";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n" + "=".repeat(100));
            System.out.printf("%-8s %-25s %-8s %-10s %-20s %-20s%n",
                "Trans ID", "Product", "Type", "Quantity", "Date", "Remarks");
            System.out.println("=".repeat(100));

            while (rs.next()) {
                System.out.printf("%-8d %-25s %-8s %-10d %-20s %-20s%n",
                    rs.getInt("transaction_id"),
                    rs.getString("product_name"),
                    rs.getString("transaction_type"),
                    rs.getInt("quantity"),
                    rs.getTimestamp("transaction_date").toString(),
                    rs.getString("remarks") != null ? rs.getString("remarks") : ""
                );
            }

            System.out.println("=".repeat(100));

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("✗ Error viewing transactions: " + e.getMessage());
        }
    }

    // ===================================
    // SUPPLIER MANAGEMENT MENU
    // ===================================
    private static void supplierMenu() {
        while (true) {
            System.out.println("\n--- SUPPLIER MANAGEMENT ---");
            System.out.println("1. Add Supplier");
            System.out.println("2. View All Suppliers");
            System.out.println("3. Back to Main Menu");
            System.out.print("Choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1: addSupplier(); break;
                case 2: viewSuppliers(); break;
                case 3: return;
                default: System.out.println("✗ Invalid choice!");
            }
        }
    }

    // Add supplier
    private static void addSupplier() {
        try {
            scanner.nextLine();
            System.out.println("\n--- ADD NEW SUPPLIER ---");
            System.out.print("Supplier Name: ");
            String name = scanner.nextLine();

            System.out.print("Contact Person: ");
            String contact = scanner.nextLine();

            System.out.print("Phone: ");
            String phone = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Address: ");
            String address = scanner.nextLine();

            String sql = "INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) " +
                         "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.setString(5, address);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✓ Supplier added successfully!");
            }

            pstmt.close();

        } catch (SQLException e) {
            System.err.println("✗ Error adding supplier: " + e.getMessage());
        }
    }

    // View all suppliers
    private static void viewSuppliers() {
        try {
            String sql = "SELECT * FROM suppliers ORDER BY supplier_id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n" + "=".repeat(120));
            System.out.printf("%-5s %-25s %-20s %-15s %-30s%n",
                "ID", "Supplier Name", "Contact Person", "Phone", "Email");
            System.out.println("=".repeat(120));

            while (rs.next()) {
                System.out.printf("%-5d %-25s %-20s %-15s %-30s%n",
                    rs.getInt("supplier_id"),
                    rs.getString("supplier_name"),
                    rs.getString("contact_person"),
                    rs.getString("phone"),
                    rs.getString("email")
                );
            }

            System.out.println("=".repeat(120));

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("✗ Error viewing suppliers: " + e.getMessage());
        }
    }

    // ===================================
    // REPORTS MENU
    // ===================================
    private static void reportsMenu() {
        while (true) {
            System.out.println("\n--- REPORTS ---");
            System.out.println("1. Low Stock Alert");
            System.out.println("2. Total Inventory Value");
            System.out.println("3. Category-wise Summary");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1: lowStockReport(); break;
                case 2: inventoryValueReport(); break;
                case 3: categoryReport(); break;
                case 4: return;
                default: System.out.println("✗ Invalid choice!");
            }
        }
    }

    // Low stock alert
    private static void lowStockReport() {
        try {
            String sql = "SELECT * FROM low_stock_items";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n" + "=".repeat(100));
            System.out.println("⚠️  LOW STOCK ALERT");
            System.out.println("=".repeat(100));
            System.out.printf("%-5s %-30s %-15s %-10s %-15s %-20s%n",
                "ID", "Product", "Category", "Stock", "Reorder Lvl", "Supplier");
            System.out.println("=".repeat(100));

            boolean hasLowStock = false;
            while (rs.next()) {
                hasLowStock = true;
                System.out.printf("%-5d %-30s %-15s %-10d %-15d %-20s%n",
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getInt("reorder_level"),
                    rs.getString("supplier_name")
                );
            }

            if (!hasLowStock) {
                System.out.println("✓ All products have sufficient stock!");
            }

            System.out.println("=".repeat(100));

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("✗ Error generating low stock report: " + e.getMessage());
        }
    }

    // Total inventory value
    private static void inventoryValueReport() {
        try {
            String sql = "SELECT * FROM inventory_value";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n" + "=".repeat(80));
            System.out.println("💰 TOTAL INVENTORY VALUE");
            System.out.println("=".repeat(80));
            System.out.printf("%-20s %-15s %-15s %-20s%n",
                "Category", "Products", "Total Qty", "Total Value (₹)");
            System.out.println("=".repeat(80));

            double grandTotal = 0;
            while (rs.next()) {
                double value = rs.getDouble("total_value");
                grandTotal += value;
                System.out.printf("%-20s %-15d %-15d ₹%-19.2f%n",
                    rs.getString("category"),
                    rs.getInt("total_products"),
                    rs.getInt("total_quantity"),
                    value
                );
            }

            System.out.println("=".repeat(80));
            System.out.printf("%-51s ₹%-19.2f%n", "GRAND TOTAL:", grandTotal);
            System.out.println("=".repeat(80));

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("✗ Error generating value report: " + e.getMessage());
        }
    }

    // Category-wise report
    private static void categoryReport() {
        try {
            String sql = "SELECT category, COUNT(*) as product_count, " +
                         "SUM(quantity) as total_quantity " +
                         "FROM products GROUP BY category";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("📊 CATEGORY-WISE SUMMARY");
            System.out.println("=".repeat(60));
            System.out.printf("%-25s %-15s %-15s%n",
                "Category", "Products", "Total Quantity");
            System.out.println("=".repeat(60));

            while (rs.next()) {
                System.out.printf("%-25s %-15d %-15d%n",
                    rs.getString("category"),
                    rs.getInt("product_count"),
                    rs.getInt("total_quantity")
                );
            }

            System.out.println("=".repeat(60));

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("✗ Error generating category report: " + e.getMessage());
        }
    }

    // ===================================
    // UTILITY METHODS
    // ===================================

    // Check if product exists
    private static boolean productExists(int productId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM products WHERE product_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, productId);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        boolean exists = rs.getInt(1) > 0;
        rs.close();
        pstmt.close();
        return exists;
    }

    // Get current stock of a product
    private static int getCurrentStock(int productId) throws SQLException {
        String sql = "SELECT quantity FROM products WHERE product_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, productId);
        ResultSet rs = pstmt.executeQuery();
        int stock = 0;
        if (rs.next()) {
            stock = rs.getInt("quantity");
        }
        rs.close();
        pstmt.close();
        return stock;
    }

    // Display product results
    private static void displayProductResults(ResultSet rs) throws SQLException {
        System.out.println("\n" + "=".repeat(100));
        System.out.printf("%-5s %-25s %-15s %-10s %-12s %-10s%n",
            "ID", "Name", "Category", "Quantity", "Price", "Reorder");
        System.out.println("=".repeat(100));

        boolean hasResults = false;
        while (rs.next()) {
            hasResults = true;
            System.out.printf("%-5d %-25s %-15s %-10d ₹%-11.2f %-10d%n",
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getDouble("unit_price"),
                rs.getInt("reorder_level")
            );
        }

        if (!hasResults) {
            System.out.println("No products found.");
        }

        System.out.println("=".repeat(100));
    }

    // Safe integer input
    private static int getIntInput() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (Exception e) {
                System.out.print("Invalid input! Enter a number: ");
                scanner.nextLine(); // Clear buffer
            }
        }
    }

    // Safe double input
    private static double getDoubleInput() {
        while (true) {
            try {
                return scanner.nextDouble();
            } catch (Exception e) {
                System.out.print("Invalid input! Enter a number: ");
                scanner.nextLine(); // Clear buffer
            }
        }
    }

    // Close database connection
    private static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("\n✓ Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
