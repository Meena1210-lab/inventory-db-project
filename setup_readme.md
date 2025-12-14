# 🚀 Smart Inventory Management System

## 📌 Project Overview

A Java-based **Inventory Management System** that uses JDBC to interact with a MySQL database. The system provides complete CRUD operations for products, stock management, supplier tracking, and comprehensive reporting.

---

## ✅ Features Implemented

### 1. **Product Management**
- ✓ Add new products
- ✓ Update product details
- ✓ Delete products
- ✓ Search by ID or Name
- ✓ Display all products

### 2. **Stock Operations**
- ✓ Stock In (Add inventory)
- ✓ Stock Out (Remove inventory with validation)
- ✓ Prevent negative stock
- ✓ Transaction history tracking

### 3. **Supplier Management**
- ✓ Add suppliers
- ✓ View all suppliers
- ✓ Link products to suppliers

### 4. **Reports**
- ✓ Low Stock Alert (products below reorder level)
- ✓ Total Inventory Value Calculation
- ✓ Category-wise Product Summary

### 5. **Data Integrity**
- ✓ Foreign key constraints
- ✓ Input validation
- ✓ Exception handling
- ✓ Transaction logging

---

## 🛠️ Technologies Used

| Component | Technology |
|-----------|-----------|
| Programming Language | Java (JDK 8+) |
| Database | MySQL 8.0+ |
| Database Connectivity | JDBC |
| IDE (Recommended) | IntelliJ IDEA / Eclipse / VS Code |

---

## 📦 Prerequisites

Before running the project, ensure you have:

1. **Java JDK 8 or higher** installed
   ```bash
   java -version
   ```

2. **MySQL Server** installed and running
   ```bash
   mysql --version
   ```

3. **MySQL JDBC Driver** (Connector/J)
   - Download from: https://dev.mysql.com/downloads/connector/j/
   - Or add Maven dependency (see below)

---

## ⚙️ Installation Steps

### Step 1: Set Up MySQL Database

1. **Start MySQL Server**
   ```bash
   # On Windows (Command Prompt as Admin)
   net start mysql80
   
   # On macOS/Linux
   sudo systemctl start mysql
   ```

2. **Login to MySQL**
   ```bash
   mysql -u root -p
   ```

3. **Create Database**
   ```sql
   CREATE DATABASE inventory_db;
   USE inventory_db;
   ```

4. **Run the SQL Schema**
   - Copy the entire content from `Database Schema - Inventory Management` artifact
   - Paste and execute in MySQL Workbench or command line

5. **Verify Installation**
   ```sql
   SHOW TABLES;
   SELECT * FROM products;
   ```

---

### Step 2: Configure Java Project

#### **Option A: Using IDE (IntelliJ/Eclipse)**

1. **Create New Java Project**
   - File → New → Project
   - Select Java Project
   - Name: `InventoryManagementSystem`

2. **Add MySQL JDBC Driver**
   - Download `mysql-connector-java-8.x.x.jar`
   - Right-click project → Build Path → Add External Archives
   - Select the JAR file

3. **Create Java File**
   - Create `InventoryManagementSystem.java`
   - Copy code from the artifact

4. **Update Database Credentials** (Line 13-15)
   ```java
   private static final String DB_URL = "jdbc:mysql://localhost:3306/inventory_db";
   private static final String DB_USER = "root";
   private static final String DB_PASSWORD = "your_password"; // Change this!
   ```

---

#### **Option B: Using Maven**

1. **Create Maven Project**
   ```bash
   mvn archetype:generate -DgroupId=com.inventory -DartifactId=inventory-system -DarchetypeArtifactId=maven-archetype-quickstart
   ```

2. **Add Dependency in `pom.xml`**
   ```xml
   <dependencies>
       <dependency>
           <groupId>mysql</groupId>
           <artifactId>mysql-connector-java</artifactId>
           <version>8.0.33</version>
       </dependency>
   </dependencies>
   ```

3. **Build Project**
   ```bash
   mvn clean install
   ```

---

### Step 3: Run the Application

#### **From IDE:**
- Right-click `InventoryManagementSystem.java` → Run

#### **From Command Line:**
```bash
# Compile
javac -cp ".;mysql-connector-java-8.x.x.jar" InventoryManagementSystem.java

# Run (Windows)
java -cp ".;mysql-connector-java-8.x.x.jar" InventoryManagementSystem

# Run (macOS/Linux)
java -cp ".:mysql-connector-java-8.x.x.jar" InventoryManagementSystem
```

---

## 📸 Sample Output

```
✓ Database connected successfully!

╔════════════════════════════════════════╗
║   INVENTORY MANAGEMENT SYSTEM          ║
╠════════════════════════════════════════╣
║  1. Product Management                 ║
║  2. Stock Operations                   ║
║  3. Supplier Management                ║
║  4. Reports                            ║
║  5. Exit                               ║
╚════════════════════════════════════════╝
Enter your choice: 
```
<img width="1920" height="1020" alt="Screenshot 2025-12-14 205044" src="https://github.com/user-attachments/assets/abefb98c-b730-4a00-a7d3-7933eee465d9" />

---

## 🧪 Testing the System

### Test Case 1: Add Product
```
1. Select Product Management → Add Product
2. Enter sample data:
   - Name: Wireless Keyboard
   - Category: Accessories
   - Quantity: 50
   - Supplier ID: 1
   - Unit Price: 1200
   - Reorder Level: 10
```

### Test Case 2: Stock Out Validation
```
1. Select Stock Operations → Stock Out
2. Try to remove more quantity than available
3. Expected: Error message preventing negative stock
```

### Test Case 3: Low Stock Alert
```
1. Select Reports → Low Stock Alert
2. View products below reorder level
3. Expected: List of products needing restocking
```

---

## 📊 Database Schema

```
suppliers (supplier_id, supplier_name, contact_person, phone, email, address)
    ↓
products (product_id, product_name, category, quantity, supplier_id, unit_price, reorder_level)
    ↓
transactions (transaction_id, product_id, transaction_type, quantity, transaction_date)
```

---

## 🎓 Project Structure

```
InventoryManagementSystem/
│
├── src/
│   └── InventoryManagementSystem.java
│
├── lib/
│   └── mysql-connector-java-8.x.x.jar
│
├── database/
│   └── schema.sql
│
├── docs/
│   ├── README.md
│   ├── ER_Diagram.png
│   └── Screenshots/
│
└── pom.xml (if using Maven)
```

---

## 🐛 Common Issues & Solutions

### Issue 1: ClassNotFoundException
**Error:** `java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver`

**Solution:** 
- Ensure MySQL JDBC driver is added to classpath
- Check JAR file version compatibility

---

### Issue 2: SQLException - Access Denied
**Error:** `Access denied for user 'root'@'localhost'`

**Solution:**
```sql
-- Reset MySQL root password
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
FLUSH PRIVILEGES;
```

---

### Issue 3: Connection Timeout
**Error:** `Communications link failure`

**Solution:**
- Verify MySQL server is running
- Check firewall settings
- Confirm port 3306 is not blocked

---

## 🔒 Security Notes

⚠️ **For Production Use:**
- Never hardcode passwords in source code
- Use environment variables or config files
- Implement proper authentication
- Use PreparedStatements (already implemented) to prevent SQL injection

---

## 📈 Future Enhancements

- [ ] GUI using JavaFX/Swing
- [ ] Barcode scanning integration
- [ ] Export reports to PDF/Excel
- [ ] Email notifications for low stock
- [ ] Multi-user authentication
- [ ] Dashboard with charts
- [ ] Mobile app integration

---

## 👨‍💻 Author & Submission

**Project:** Smart Inventory Management System  
**Technology:** Java + JDBC + MySQL  
**Course:** Database Management Systems / Java Programming  

---

## 📞 Support

For issues or questions:
1. Check documentation
2. Review error logs
3. Verify database connection
4. Test with sample data

---

## 📄 License

This project is developed for educational purposes.

---

**⭐ Star this project if you find it helpful!**
