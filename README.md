# Vehicle Rental System
> Java management system using OOP, MySQL, and Swing.

---

## Tech Stack
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)

---

## Build & Run (Eclipse)

- **Clean before compiling** – ensures no stale class files remain.
- **macOS cleanup** – from the project root run:

```bash
dot_clean .
```

## Database Migration

1. **Create a MySQL database** (if not already created). The schema file is located at:
   ```text
   database/schema/vehicle_rental_g11_oop.sql
   ```
2. **Load the schema** using the MySQL CLI:
   ```bash
   mysql -u YOUR_USER -p < database/schema/vehicle_rental_g11_oop.sql
   ```
   Replace `YOUR_USER` with your MySQL username. You will be prompted for the password.
   - Alternatively, after logging into MySQL, you can run:
     ```sql
     SOURCE path/to/vehicle_rental_g11_oop.sql;
     ```
3. **Configure connection** by copying the example env file and updating credentials:
```bash
# macOS / Linux
cp com.vehicle.rental.g11/.env.example com.vehicle.rental.g11/.env
```

```cmd
:: Windows Command Prompt
copy com.vehicle.rental.g11\.env.example com.vehicle.rental.g11\.env
```

```powershell
# PowerShell
Copy-Item -Path "com.vehicle.rental.g11\.env.example" -Destination "com.vehicle.rental.g11\.env"
```
   Ensure `DB_URL`, `DB_USER`, and `DB_PASSWORD` match your local setup.

### Edited Schema Overview

- **Person**: stores generic person information (`personID`, `first_name`, `middle_initial`, `last_name`, `suffix`, `email`).
- **Employee**: extends `Person` with a `password` field; `personID` is a foreign key to `Person`.
- **Customer**: maps a `personID` to a customer role; also references `Person`.
- **Vehicles**: vehicle inventory with fields `vehicleID`, `brand`, `model`, `type`, `plate_number`, `daily_rate`, and `status` (enum).
- **Rentals**: rental records linking a `personID` (customer) and `vehicleID` with dates and total cost.

These tables reflect the current relational structure used by the application.


## Development Roadmap

### Phase 1: Design & Architecture
- [ ] **Database Schema (Data Dictionary)** – *ongoing – schema being refined*

#### `Customers`
| Column | Type | Default Value | Nullable | Extra |
| :--- | :--- | :--- | :--- | :--- |
| `customerID` | `varchar(36)` |  | `NO` | `PRIMARY KEY` |
| `first_name` | `varchar(45)` |  | `NO` | |
| `middle_name` | `varchar(45)` | `NULL` | `YES` | |
| `last_name` | `varchar(45)` |  | `NO` | |
| `suffix` | `varchar(45)` | `NULL` | `YES` | |
| `email` | `varchar(100)` |  | `NO` | |
| `password` | `varchar(97)` |  | `NO` | |

#### `Vehicles`
| Column | Type | Default Value | Nullable | Extra |
| :--- | :--- | :--- | :--- | :--- |
| `vehicleID` | `int` |  | `NO` | `AUTO_INCREMENT` |
| `brand` | `varchar(45)` |  | `NO` | |
| `model` | `varchar(45)` |  | `NO` | |
| `type` | `varchar(45)` |  | `NO` | |
| `plate_number` | `varchar(7)` |  | `NO` | |
| `daily_rate` | `decimal(10,2)` |  | `NO` | |
| `status` | `enum('Available','Maintenance','Cleaning','Out of Service','Rented','Reserved')` | `'Available'` | `NO` | |

#### `Rentals`
| Column | Type | Default Value | Nullable | Extra |
| :--- | :--- | :--- | :--- | :--- |
| `rentalID` | `int` |  | `NO` | `AUTO_INCREMENT` |
| `customerID` | `varchar(36)` |  | `NO` | |
| `vehicleID` | `int` |  | `NO` | |
| `rental_date` | `date` |  | `NO` | |
| `planned_return_date` | `date` |  | `NO` | |
| `return_date` | `date` | `NULL` | `YES` | |
| `total_cost` | `decimal(10,2)` |  | `NO` | |

- [X] **Class Hierarchy (UML)**
  - **Abstraction:** `abstract class Vehicle`
  - **Inheritance:** `Car`, `Motorcycle`, `Truck` $\rightarrow$ `Vehicle`
  - **Encapsulation:** Private attributes with Getters/Setters
  - **Polymorphism:** Overridden `calculateRentalCost()`

### Phase 2: Infrastructure & Security
- [X] **Dependencies** (`argon2-jvm`, `mysql-connector-j`, `junit-jupiter`)
- [X] **Security:** `PasswordUtil` class (Argon2id)
- [X] **Database:** Singleton `DatabaseConnection` class
- [X] **Login Module**: User authentication & validation logic

### Phase 3: Core Management (CRUD)
- [X] **DAO Pattern:** `CustomerDAO`, `VehicleDAO`, `RentalDAO`
- [X] **UUID Management:** `java.util.UUID` for customer identity
- [X] **Add Record Module**: Field validation & database insertion
- [x] **View Records Module**: Data retrieval & table display
- [x] **Search Record Module**: Query-based record lookup
- [X] **Update Record Module**: Record verification & modification
- [ ] **Delete Record Module**: Confirmation-based record removal

### Phase 4: Analysis & Reporting
- [x] **Rental Engine**: Logic for availability and cost calculation
- [X] **Report Generation Module**: Revenue and rental statistics

### Phase 5: System Integration & GUI (`javax.swing`)
- [X] **Main Menu Module**: Central navigation hub
- [X] **GUI Components**: `JFrame` dashboards, `JTextField` forms, and `JOptionPane` feedback
- [X] **Navigation Flow**: Module transitions

### Phase 6: Testing & Finalization
- [ ] **Testing:** `JUnit 5` for logic and security verification
- [X] **Robustness:** `try-catch` error handling & input validation
- [ ] **Documentation:** Final PDF (ERD, UML, Flowcharts, Screenshots)

### Project Progress

### Known Issues / Gaps
- **Default admin account** not seeded; login requires an admin user.
- **Vehicle delete** UI and DAO method are missing.
- `archiveCustomer` method references a non‑existent `is_active` column in the schema.
- No formal documentation PDF (title page, ERD, flowcharts, screenshots).
- No JUnit tests for business logic or DAOs.
- No logging framework (SLF4J/Logback) configured.
- `.env` file not ignored by Git, exposing credentials.
- `DatabaseConnection` singleton is not thread‑safe.
- `PasswordUtil` holds a static Argon2 instance, potential concurrency issue.
- Input validation could be stronger (email format, plate number regex).
`[################----]` 81%

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
