# Vehicle Rental System
> Java management system using OOP, MySQL, and Swing.

---

## Tech Stack
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)

---

## Development Roadmap

### Phase 1: Design & Architecture
- [X] **Database Schema (Data Dictionary)**

#### `Customers`
| Column | Type | Default Value | Nullable | Extra |
| :--- | :--- | :--- | :--- | :--- |
| `customerID` | `varchar(36)` | `NULL` | `NO` | `PRIMARY KEY` |
| `first_name` | `varchar(45)` | `NULL` | `NO` | |
| `middle_name` | `varchar(45)` | `NULL` | `YES` | |
| `last_name` | `varchar(45)` | `NULL` | `NO` | |
| `suffix` | `varchar(45)` | `NULL` | `YES` | |
| `email` | `varchar(100)` | `NULL` | `NO` | |
| `password` | `varchar(97)` | `NULL` | `NO` | |

#### `Vehicles`
| Column | Type | Default Value | Nullable | Extra |
| :--- | :--- | :--- | :--- | :--- |
| `vehicleID` | `int` | `NULL` | `NO` | `AUTO_INCREMENT` |
| `brand` | `varchar(45)` | `NULL` | `NO` | |
| `model` | `varchar(45)` | `NULL` | `NO` | |
| `type` | `varchar(45)` | `NULL` | `NO` | |
| `plate_number` | `varchar(7)` | `NULL` | `NO` | |
| `daily_rate` | `decimal(10,2)` | `NULL` | `NO` | |
| `status` | `enum('Available', 'Maintenance', 'Cleaning', 'Out of Service', 'Rented', 'Reserved')` | `'Available'` | `NO` | |

#### `Rentals`
| Column | Type | Default Value | Nullable | Extra |
| :--- | :--- | :--- | :--- | :--- |
| `rentalID` | `int` | `NULL` | `NO` | `AUTO_INCREMENT` |
| `customerID` | `varchar(36)` | `NULL` | `NO` | |
| `vehicleID` | `int` | `NULL` | `NO` | |
| `rental_date` | `date` | `NULL` | `NO` | |
| `planned_return_date` | `date` | `NULL` | `NO` | |
| `return_date` | `date` | `NULL` | `YES` | |
| `total_cost` | `decimal(10,2)` | `NULL` | `NO` | |

- [ ] **Class Hierarchy (UML)**
  - **Abstraction:** `abstract class Vehicle`
  - **Inheritance:** `Car`, `Motorcycle`, `Truck` $\rightarrow$ `Vehicle`
  - **Encapsulation:** Private attributes with Getters/Setters
  - **Polymorphism:** Overridden `calculateRentalCost()`

### Phase 2: Infrastructure & Security
- [X] **Dependencies** (`argon2-jvm`, `mysql-connector-j`, `junit-jupiter`)
- [ ] **Security:** `PasswordUtil` class (Argon2id)
- [ ] **Database:** Singleton `DatabaseConnection` class
- [ ] **Login Module**: User authentication & validation logic

### Phase 3: Core Management (CRUD)
- [ ] **DAO Pattern:** `CustomerDAO`, `VehicleDAO`, `RentalDAO`
- [ ] **UUID Management:** `java.util.UUID` for customer identity
- [ ] **Add Record Module**: Field validation & database insertion
- [ ] **View Records Module**: Data retrieval & table display
- [ ] **Search Record Module**: Query-based record lookup
- [ ] **Update Record Module**: Record verification & modification
- [ ] **Delete Record Module**: Confirmation-based record removal

### Phase 4: Analysis & Reporting
- [ ] **Rental Engine**: Logic for availability and cost calculation
- [ ] **Report Generation Module**: Revenue and rental statistics

### Phase 5: System Integration & GUI (`javax.swing`)
- [ ] **Main Menu Module**: Central navigation hub
- [ ] **GUI Components**: `JFrame` dashboards, `JTextField` forms, and `JOptionPane` feedback
- [ ] **Navigation Flow**: Module transitions

### Phase 6: Testing & Finalization
- [ ] **Testing:** `JUnit 5` for logic and security verification
- [ ] **Robustness:** `try-catch` error handling & input validation
- [ ] **Documentation:** Final PDF (ERD, UML, Flowcharts, Screenshots)

## Task Delegation
- **Carl**: Login Module, Main Menu Module
- **Greta**: Add Record Module, Update Record Module
- **Francis**: View Records Module, Search Record Module
- **Jamaco**: Delete Record Module, Report Generation Module

## Error Handling Strategy
`RentalSystemException` handles domain errors (e.g., `VehicleUnavailableException`). A GUI handler catches these to trigger `JOptionPane` alerts, reducing `try-catch` boilerplate.

## Project Structure
```text
com.vehicle.rental.g11/src/com/vehicle/rental/g11/
├── Main.java                    # Application entry point
├── model/                       # OOP entities
│   ├── Vehicle.java             # Abstract class
│   ├── Car.java                 # Subclass
│   ├── Motorcycle.java          # Subclass
│   ├── Truck.java               # Subclass
│   ├── Customer.java            # Customer entity
│   └── Rental.java              # Rental entity
├── dao/                         # CRUD Data Access Objects
│   ├── CustomerDAO.java
│   ├── VehicleDAO.java
│   └── RentalDAO.java
├── service/                     # Business logic & utils
│   ├── DatabaseConnection.java  # Singleton connection
│   ├── PasswordUtil.java        # Argon2id hashing
│   └── RentalEngine.java        # Cost & availability logic
├── gui/                         # Swing GUI components
│   ├── LoginFrame.java          # Login module
│   ├── MainFrame.java           # Main menu module
│   ├── CustomerFrame.java       # Customer CRUD
│   ├── VehicleFrame.java        # Vehicle CRUD
│   ├── RentalFrame.java         # Rental CRUD
│   └── ReportFrame.java         # Report module
└── exception/                   # Custom exceptions
    └── RentalSystemException.java
```
