# Project Status Report

## Compliance Checklist – Quick‑Thorough Review

| # | Requirement (Section) | Status | Comments / Evidence |
|---|------------------------|--------|---------------------|
| 1 | **Login Module** (IV‑1) | **Partially implemented** | Login UI (`LoginFrame.java` | `/com.vehicle.rental.g11/src/com/vehicle/rental/g11/gui/LoginFrame.java`) validates credentials against the `Customers` table and shows error messages. A default admin account is **not** created or seeded, so the “at least one default admin” clause is missing. |
| 2 | **Main Menu Module** (IV‑2) | **Fully implemented** | Dashboard (`MainFrame.java` | `/com.vehicle.rental.g11/src/com/vehicle/rental/g11/gui/MainFrame.java`) provides navigation to Vehicles, Customers, Rentals, Reports and Logout. |
| 3 | **Add Record Module** (IV‑3) | **Fully implemented** | • Vehicles – “Add Vehicle” button (`VehicleFrame.java`).<br>• Customers – “Add Customer” button (`CustomerFrame.java`).<br>• Rentals – “Add Rental” button (`RentalFrame.java`). |
| 4 | **View Records Module** (IV‑4) | **Fully implemented** | All three entity tables are displayed in their respective frames (`VehicleFrame`, `CustomerFrame`, `RentalFrame`). |
| 5 | **Search Record Module** (IV‑5) | **Fully implemented** | Live search fields in Vehicle, Customer, and Rental frames use `SearchHandler` and DAO search methods (`VehicleDAO.searchVehicles`, `CustomerDAO.searchCustomers`, `RentalDAO.searchRentals`). |
| 6 | **Update Record Module** (IV‑6) | **Fully implemented** | Update buttons exist for Vehicles, Customers, Rentals and invoke DAO `update*` methods. |
| 7 | **Delete Record Module** (IV‑7) | **Partially implemented** | • Customer delete (hard delete) works (`CustomerFrame.deleteButton`).<br>• No UI or DAO method for deleting Vehicles.<br>• Rentals have no delete UI (only “Mark as Returned”). |
| 8 | **Report Generation Module** (IV‑8) | **Fully implemented** | `ReportFrame.java` shows total revenue, active rentals, overdue count, and an overdue‑rentals table. |
| 9 | **Input Validation** (IV‑9) | **Fully implemented** | Form fields validate non‑empty values, numeric formats, plate‑length, etc. (`VehicleFrame.validateFields`, `LoginFrame.handleLogin`, `RentalFrame` date parsing). |
|10| **Exception Handling** (IV‑10) | **Fully implemented** | All DB operations wrapped in try‑catch and re‑thrown as `RentalSystemException` (`CustomerDAO`, `VehicleDAO`, `RentalDAO`, UI frames). |
|11| **OOP Concepts – Classes & Objects** (V‑1) | **Fully implemented** | > 30 classes (model, DAO, service, GUI, exception). |
|12| **OOP Concepts – Encapsulation** (V‑2) | **Fully implemented** | Private fields with getters/setters (e.g., `Vehicle.java`). |
|13| **OOP Concepts – Constructors** (V‑3) | **Fully implemented** | Default and parameterized constructors in model classes (`Vehicle`, `Car`, `Customer`, `Rentals`). |
|14| **OOP Concepts – Inheritance** (V‑4) | **Fully implemented** | `Vehicle` abstract → `Car`, `Motorcycle`, `Truck` (`Car.java`, `Motorcycle.java`, `Truck.java`). |
|15| **OOP Concepts – Polymorphism** (V‑5) | **Fully implemented** | Method overriding (`calculateRentalCost`, `getType`). |
|16| **OOP Concepts – Abstraction** (V‑6) | **Fully implemented** | `Vehicle` is abstract. |
|17| **OOP Concepts – Methods** (V‑7) | **Fully implemented** | Business logic separated into service classes (`RentalEngine`, `PasswordUtil`, `SearchHandler`). |
|18| **OOP Concepts – Exception Handling** (V‑8) | **Fully implemented** | Custom `RentalSystemException` used throughout. |
|19| **OOP Concepts – Collections** (V‑9) | **Fully implemented** | `List`, `ArrayList`, `Map` used in DAOs and services. |
|20| **Minimum Class Requirement (≥ 5 classes)** (VI) | **Fully implemented** | > 20 classes across packages (`model`, `dao`, `service`, `gui`, `exception`). |
|21| **Database Requirement – ≥ 3 tables** (VII) | **Fully implemented** | Schema (`carls.sql`) defines `Customers`, `Vehicles`, `Rentals`. **Note:** `CustomerDAO.archiveCustomer` references a non‑existent `is_active` column – schema/DAO mismatch. |
|22| **Documentation – Title Page, Introduction, Objectives, etc.** (VIII‑1…‑12) | **Missing** | No PDF documentation, no ERD, flowchart, screenshots, or formal sections. The repository only contains a short `README.md` and a progress tracker. |
|23| **Video Demonstration** (X‑1) | **Missing** | No video files or links in the repo. |
|24| **Grading Criteria – System Functionality, OOP, DB Integration, Validation, Docs, Presentation** (X‑III) | **Partially satisfied** | Functional code meets most functional criteria, but documentation and testing are absent; Delete Vehicle & default admin are gaps. |
|25| **Testing** (IX‑1) | **Missing** | No JUnit test classes in `src/test/`. |
|26| **Logging** (IX‑2) | **Missing** | No logging framework configured. |
|27| **Security – .env handling, thread‑safe DB connection, Argon2 usage** | **Partial / Issues** | • `.env` not listed in `.gitignore` (risk of credential leak).<br>• `DatabaseConnection` singleton is not thread‑safe (reviewed in `REVIEW_FINDINGS.md`).<br>• `PasswordUtil` holds a static Argon2 instance (potential concurrency issue). |
|28| **Build & Run instructions** (AGENTS.md) | **Fully implemented** | `dot_clean .`, Maven commands, DB script location documented. |

---

### Key Gaps & Mismatches

| Area | Gap | Impact |
|------|------|--------|
| **Default admin account** | No pre‑seeded admin user; login requirement not fully met. | Demonstration may fail without an admin. |
| **Vehicle delete** | No UI button or DAO delete method for Vehicles. | "Delete Record" feature incomplete for vehicles. |
| **`archiveCustomer` method** | References column `is_active` that does not exist in the schema. | Runtime SQL errors if the method is called. |
| **Documentation** | No formal PDF, ERD, flowcharts, or screenshots. | Fails the documentation checklist (VIII). |
| **Testing** | No unit or integration tests. | Grading criterion “Testing” not satisfied. |
| **Logging** | No logging framework configured. | Reduces observability and debugging capability. |
| **Security of `.env`** | `.env` not ignored by Git; credentials could be committed. | Risk of leaking DB credentials. |
| **Thread safety** | `DatabaseConnection` singleton and static Argon2 instance are not thread‑safe. | Potential race conditions under concurrent use. |
| **Connection handling** | Single connection per app; no connection pool. | May cause bottlenecks under load. |
| **Input validation depth** | Plate‑number validation only checks length; email format not validated. | Allows malformed data to be stored. |

---

### Recommended Next Steps (Prioritized)

1. **Seed a default admin account**  
   *Add an INSERT statement to `carls.sql` (e.g., admin email `admin@example.com`, password hashed with `PasswordUtil`).*
2. **Implement Vehicle delete**  
   *Add `deleteVehicle(int vehicleID)` in `VehicleDAO` and a “Delete Vehicle” button in `VehicleFrame`.*
3. **Fix `archiveCustomer` mismatch**  
   *Either add an `is_active` column to `Customers` (with migration) or remove/replace the method with soft‑delete logic that matches the schema.*
4. **Secure `.env`**  
   *Add `.env` entry to `.gitignore`; consider loading defaults from environment variables.*
5. **Make `DatabaseConnection` thread‑safe**  
   *Apply synchronized getter or “initialization‑on‑demand holder” pattern.*
6. **Refactor `PasswordUtil`**  
   *Create a new Argon2 instance per hash/verify call or synchronize usage.*
7. **Introduce connection pooling**  
   *Integrate HikariCP (or similar) and obtain connections from the pool.*
8. **Add comprehensive unit & integration tests**  
   *JUnit 5 tests for `RentalEngine`, DAOs (using an in‑memory H2 DB with the schema), and `PasswordUtil`.*
9. **Add logging**  
   *Add SLF4J + Logback; log DAO operations, service calls, and error stack traces.*
10. **Complete required documentation**  
    *Create a PDF containing Title Page, Introduction, Objectives, Scope, System Features, OOP Concept explanations, ERD, Flowchart, Screenshots of each UI screen, source‑code overview, and conclusion.*
11. **Produce a short demo video** (5–10 min)  
    *Show login, record CRUD, report generation, and OOP concept walkthrough.*
12. **Add Javadoc** to all public classes/methods  
    *Generate API docs and link them from the README.*
13. **Optional enhancements**  
    *Add email format validation, stronger plate‑number regex, and centralise user‑facing messages in a resource bundle.*

---

**Summary**

- Functional modules are mostly complete; missing default admin and vehicle delete.
- All OOP concepts are demonstrated.
- Documentation, testing, logging, and security need substantial work.
- Address the gaps and follow the prioritized steps to achieve full compliance with the project requirements.