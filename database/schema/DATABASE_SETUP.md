# Database Setup Guide

This guide provides instructions on how to implement the `carls.sql` schema in your MySQL server.

## Prerequisites

- **MySQL Server** installed and running (Version 8.0 or higher recommended).
- A MySQL client (MySQL Shell, MySQL Workbench, phpMyAdmin, etc.).

## Implementation Methods

### Method 1: Using MySQL Command Line (CLI)

1. Open your terminal or command prompt.
2. Log in to your MySQL server:
   ```bash
   mysql -u your_username -p
   ```
3. Execute the SQL script by providing the path to the file:
   ```sql
   SOURCE `/path/to/vehicle-rental-oop-g11/database/schema/carls.sql`;
   ```
   *Replace `/path/to/` with the actual absolute path to the project folder.*

### Method 2: Using MySQL Workbench

1. Open **MySQL Workbench** and connect to your server.
2. Go to `File` $\rightarrow$ `Open SQL Script...`.
3. Select the `carls.sql` file.
4. Click the **Execute (Lightning Bolt)** icon to run the script.

## Verification

To ensure the database was created successfully, run the following commands in your MySQL client:

```sql
USE `carls`;
SHOW TABLES;
```

You should see the following tables:
- `Customers`
- `Vehicles`
- `Rentals`

To verify the structure of a specific table (e.g., `Vehicles`), use:
```sql
DESCRIBE `Vehicles`;
```
