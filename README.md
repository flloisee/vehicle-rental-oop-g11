# 🚗 Vehicle Rental System
> A robust Java-based management system leveraging OOP principles, MySQL, and Swing GUI.

---

## 🛠 Tech Stack
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)

---

## 🗺 Development Roadmap

### 🏗 Phase 1: Design & Architecture
- [ ] **Database Schema (Data Dictionary)**

#### `Customers`
| Column | Type | Default Value | Nullable | Extra |
| :--- | :--- | :--- | :--- | :--- |
| `customerID` | `varchar(36)` | `NULL` | `NO` | `PRIMARY KEY` |
| `first_name` | `varchar(45)` | `NULL` | `NO` | |
| `middle_name` | `varchar(45)` | `NULL` | `YES` | |
| `last_name` | `varchar(45)` | `NULL` | `NO` | |
| `suffix` | `varchar(45)` | `NULL` | `YES` | |
| `password` | `varchar(97)` | `NULL` | `NO` | |

#### `Vehicles`
| Column | Type | Default Value | Nullable | Extra |
| :--- | :--- | :--- | :--- | :--- |
| `vehicleID` | `int` | `NULL` | `NO` | `AUTO_INCREMENT` |
| `brand` | `varchar(45)` | `NULL` | `NO` | |
| `model` | `varchar(45)` | `NULL` | `NO` | |
| `type` | `varchar(45)` | `NULL` | `NO` | |
| `plate_number` | `varchar(20)` | `NULL` | `NO` | |
| `daily_rate` | `decimal(10,2)` | `NULL` | `NO` | |
| `status` | `varchar(20)` | `'Available'` | `NO` | |

#### `Rentals`
| Column | Type | Default Value | Nullable | Extra |
| :--- | :--- | :--- | :--- | :--- |
| `rentalID` | `int` | `NULL` | `NO` | `AUTO_INCREMENT` |
| `customerID` | `varchar(36)` | `NULL` | `NO` | |
| `vehicleID` | `int` | `NULL` | `NO` | |
| `rental_date` | `date` | `NULL` | `NO` | |
| `return_date` | `date` | `NULL` | `YES` | |
| `total_cost` | `decimal(10,2)` | `NULL` | `NO` | |

- [ ] **Class Hierarchy (UML)**
  - 🧩 **Abstraction:** `abstract class Vehicle`
  - 🧬 **Inheritance:** `Car`, `Motorcycle`, `Truck` $\rightarrow$ `Vehicle`
  - 🔒 **Encapsulation:** Private attributes with Getters/Setters
  - 🎭 **Polymorphism:** Overridden `calculateRentalCost()`

### 🔐 Phase 2: Infrastructure & Security
- [ ] **Dependencies** (`argon2-jvm`, `mysql-connector-j`, `junit-jupiter`)
- [ ] **Security:** `PasswordUtil` class (Argon2id)
- [ ] **Database:** Singleton `DatabaseConnection` class

### ⚙️ Phase 3: Domain Logic
- [ ] **DAO Pattern:** `CustomerDAO`, `VehicleDAO`, `RentalDAO`
- [ ] **UUID Management:** `java.util.UUID` for customer identity
- [ ] **Rental Engine:** Logic for availability and cost calculation

### 🖥 Phase 4: GUI Development (`javax.swing`)
- [ ] **Login:** `JFrame` with Argon2id authentication
- [ ] **Dashboard:** `JFrame` with `JTable` navigation
- [ ] **Forms:** `JTextField` for CRUD operations
- [ ] **Feedback:** `JOptionPane` for alerts and confirmations

### 🚀 Phase 5: Feature Integration
- [ ] **CRUD Modules:** Full implementation of Add, View, Search, Update, Delete
- [ ] **Admin Recovery:** Search & Update password via Admin role
- [ ] **Reporting:** Revenue and rental statistics module

### ✅ Phase 6: Testing & Finalization
- [ ] **Testing:** `JUnit 5` for logic and security verification
- [ ] **Robustness:** `try-catch` error handling & input validation
- [ ] **Documentation:** Final PDF (ERD, UML, Flowcharts, Screenshots)
