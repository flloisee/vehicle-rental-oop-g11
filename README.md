# Vehicle Rental System
> Java management system using OOP, MySQL, Swing, and Maven.

---

## Tech Stack
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

---

## Build & Run (Maven)

All Maven commands execute inside `com.vehicle.rental.g11/` where `pom.xml` lives.

Requires **Java 21** and **MySQL 8.0+**.

```bash
# macOS cleanup — removes ._* files that cause javac errors
dot_clean com.vehicle.rental.g11/

mvn clean
mvn compile
mvn package                 # produces target/vehicle-rental-system-1.0.0.jar

java -jar target/vehicle-rental-system-1.0.0.jar
```

## Database Setup

1. **Create the database** and load the schema:

```bash
mysql -u YOUR_USER -p < database/schema/vehicle_rental_g11_oop.sql
```

2. **Configure connection** — copy the example env file and update credentials:

```bash
cp com.vehicle.rental.g11/.env.example com.vehicle.rental.g11/.env
```

Edit `com.vehicle.rental.g11/.env` to match your local MySQL setup:

```
DB_URL=jdbc:mysql://localhost:3306/vehicle_rental_g11_oop
DB_USER=root
DB_PASSWORD=your_password
```

A migration script is available at `database/schema/migration.sql` for upgrading from the legacy denormalized schema.

### Schema Overview

- **Person** — base table (`personID` UUID, `first_name`, `middle_initial`, `last_name`, `suffix`, `email`).
- **Employee** — extends Person with a `password` (Argon2id hash); FK `personID` → `Person`.
- **Customer** — role table referencing Person; FK `personID` → `Person`.
- **Vehicles** — inventory (`vehicleID` AUTO_INCREMENT, `brand`, `model`, `type`, `plate_number`, `daily_rate`, `status` enum).
- **Rentals** — records linking a `personID` (customer FK → `Customer`) and `vehicleID` with dates and total cost.

---

## Development Roadmap

### Phase 1: Design & Architecture
- [X] **Database Schema (EERD)** — normalized `Person`/`Employee`/`Customer`/`Vehicles`/`Rentals`
- [X] **Class Hierarchy (UML)**
  - **Abstraction:** `abstract class Vehicle`
  - **Inheritance:** `Car`, `Motorcycle`, `Truck` → `Vehicle`
  - **Encapsulation:** Private attributes with Getters/Setters
  - **Polymorphism:** Overridden `calculateRentalCost()`

#### `Person` (base table)
| Column | Type | Nullable | Notes |
| :--- | :--- | :--- | :--- |
| `personID` | `varchar(36)` | NO | UUID, PRIMARY KEY |
| `first_name` | `varchar(45)` | NO | |
| `middle_initial` | `varchar(45)` | YES | |
| `last_name` | `varchar(45)` | NO | |
| `suffix` | `varchar(45)` | YES | |
| `email` | `varchar(100)` | NO | |

#### `Employee`
| Column | Type | Nullable | Notes |
| :--- | :--- | :--- | :--- |
| `personID` | `varchar(36)` | NO | PK, FK → `Person` ON DELETE CASCADE |
| `password` | `varchar(97)` | NO | Argon2id hash |

#### `Customer`
| Column | Type | Nullable | Notes |
| :--- | :--- | :--- | :--- |
| `personID` | `varchar(36)` | NO | PK, FK → `Person` ON DELETE CASCADE |

#### `Vehicles`
| Column | Type | Default | Nullable | Notes |
| :--- | :--- | :--- | :--- | :--- |
| `vehicleID` | `int` | | NO | AUTO_INCREMENT |
| `brand` | `varchar(45)` | | NO | |
| `model` | `varchar(45)` | | NO | |
| `type` | `varchar(45)` | | NO | |
| `plate_number` | `varchar(7)` | | NO | |
| `daily_rate` | `decimal(10,2)` | | NO | |
| `status` | `enum(...)` | `'Available'` | NO | Available, Maintenance, Cleaning, Out of Service, Rented, Reserved |

#### `Rentals`
| Column | Type | Nullable | Notes |
| :--- | :--- | :--- | :--- |
| `rentalID` | `int` | NO | AUTO_INCREMENT |
| `personID` | `varchar(36)` | NO | FK → `Customer` |
| `vehicleID` | `int` | NO | FK → `Vehicles` |
| `rental_date` | `date` | NO | |
| `planned_return_date` | `date` | NO | |
| `return_date` | `date` | YES | NULL while active |
| `total_cost` | `decimal(10,2)` | NO | |

### Phase 2: Infrastructure & Security
- [X] **Dependencies** (`mysql-connector-j`, `argon2-jvm`, `dotenv-java`)
- [X] **Security:** `PasswordUtil` class (Argon2id)
- [X] **Database:** `DatabaseConnection` singleton
- [X] **Login Module**: User authentication & validation

### Phase 3: Core Management (CRUD)
- [X] **DAO Pattern:** `CustomerDAO`, `EmployeeDAO`, `VehicleDAO`, `RentalDAO`
- [X] **UUID Management:** `java.util.UUID` for person identity
- [X] **Add Record Module**: Field validation & database insertion
- [X] **View Records Module**: Data retrieval & table display
- [X] **Search Record Module**: Query-based record lookup
- [X] **Update Record Module**: Record verification & modification
- [x] **Delete Record Module**: Confirmation-based record removal

### Phase 4: Analysis & Reporting
- [X] **Rental Engine**: Availability and cost calculation
- [X] **Report Generation Module**: Revenue and rental statistics

### Phase 5: System Integration & GUI (`javax.swing`)
- [X] **Main Menu Module**: Central navigation hub
- [X] **GUI Components**: `JFrame` dashboards, `JTextField` forms, `JOptionPane` feedback
- [X] **Navigation Flow**: Module transitions

### Phase 6: Finalization
- [X] **Robustness:** `try-catch` error handling & input validation
- [ ] **Documentation:** Final PDF (ERD, UML, Flowcharts, Screenshots)

---

## Known Issues / Gaps
- **Default admin account** not seeded; login requires an admin user inserted manually.
- **Vehicle delete** UI and DAO method are missing.
- No JUnit tests for business logic, DAOs, or security.
- No logging framework (SLF4J/Logback) configured.
- `DatabaseConnection` singleton is not thread-safe.
- `PasswordUtil` holds a static Argon2 instance (potential concurrency issue).
- Input validation could be stronger (email format, plate number regex).

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
├── Main.java                        # Entry point (SwingUtilities.invokeLater)
├── db/
│   └── DatabaseConnection.java      # Singleton connection manager (dotenv-java)
├── model/                           # OOP entities
│   ├── Vehicle.java                 # Abstract base class
│   ├── Car.java                     # Subclass (standard rate)
│   ├── Motorcycle.java              # Subclass (15% discount)
│   ├── Truck.java                   # Subclass (25% surcharge)
│   ├── VehicleFactory.java          # Factory: creates by type string
│   ├── VehicleStatus.java           # Enum (6 statuses)
│   ├── Customer.java                # Customer entity
│   ├── Employee.java                # Employee entity
│   └── Rentals.java                 # Rental entity
├── dao/                             # CRUD Data Access Objects
│   ├── CustomerDAO.java
│   ├── EmployeeDAO.java
│   ├── VehicleDAO.java
│   └── RentalDAO.java
├── service/                         # Business logic & utilities
│   ├── RentalEngine.java            # Cost & availability logic
│   ├── PasswordUtil.java            # Argon2id hashing
│   └── SearchHandler.java           # Debounced Swing timer search
├── gui/                             # Swing GUI components
│   ├── LoginFrame.java              # Login screen
│   ├── MainFrame.java               # Main menu / dashboard
│   ├── CustomerFrame.java           # Customer CRUD
│   ├── EmployeeFrame.java           # Employee CRUD
│   ├── VehicleFrame.java            # Vehicle CRUD
│   ├── VehicleFormPanel.java        # Reusable vehicle form
│   ├── RentalFrame.java             # Rental CRUD
│   ├── ReportFrame.java             # Reports (revenue, overdue)
│   ├── DatePickerDialog.java        # Date selection dialog
│   └── UITheme.java                 # Theme constants
└── exception/
    └── RentalSystemException.java   # Unifying domain exception
```
