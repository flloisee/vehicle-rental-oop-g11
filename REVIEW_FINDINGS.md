# Review Findings – CARLS (G11)

*Prepared by Code‑Reviewer on **2026‑06‑19**.*

---

## 📋 Table of Contents
1. [Security Review](#security-review)
2. [Correctness & Logic Review](#correctness--logic-review)
3. [Performance & Resource Management](#performance--resource-management)
4. [Maintainability & Code Quality](#maintainability--code-quality)
5. [Design & Architecture Review](#design--architecture-review)
6. [Test Coverage & Quality](#test-coverage--quality)
7. [Documentation Review](#documentation-review)
8. [Dependency & License Scan](#dependency--license-scan)
9. [Actionable Todo List](#actionable-todo-list)
10. [Closing Thoughts](#closing-thoughts)

---

## 1️⃣ Security Review
| # | Area | Severity | Findings & Risks | Recommendation |
|---|------|----------|----------------|----------------|
| 1 | Database credentials exposure | ⚠️ Medium | `.env` is loaded from the project root. If accidentally committed, DB URL/USER/PASSWORD could be leaked. | Add `.gitignore` entry for `.env`. Load from `System.getenv` as a fallback. |
| 2 | Singleton `DatabaseConnection` not thread‑safe | ⚠️ Medium | Lazy‑instantiation without synchronization may create two instances under concurrent access. | Use a synchronized getter or the *Initialization‑on‑Demand holder* pattern. |
| 3 | Argon2 instance reuse in `PasswordUtil` | ⚠️ Medium | A static `Argon2` object may not be thread‑safe; concurrent hashing could corrupt internal state. | Create a fresh Argon2 instance per operation or synchronize access. |
| 4 | Plate‑number validation too lax | ⚠️ Low | Only length is checked; malformed strings could be stored and later used in concatenated SQL. | Enforce a regex like `^[A-Z0-9]{1,7}$` and normalise to upper‑case. |
| 5 | Detailed SQL errors are exposed | ⚠️ Low | DAO methods include raw `SQLException` messages in `RentalSystemException`. | Log the detailed exception internally and return a generic user‑friendly message. |
| 6 | No TLS enforcement for MySQL connections | ⚠️ Low | Default URL is plain `jdbc:mysql://`. | Append `?useSSL=true&requireSSL=true` to the default URL or enforce via environment variable. |
| 7 | Argon2 static instance thread‑safety | ⚠️ Medium | Static Argon2 object may be accessed concurrently, risking state corruption. | Create a fresh Argon2 instance per operation or synchronize usage. |
| 8 | ResultSet not closed in DAO methods | ⚠️ Medium | Several DAO methods (e.g., `getRentalById`, `searchRentals`, `plateExists`) create a `ResultSet` without try‑with‑resources, leading to potential connection leaks. | Use try‑with‑resources for `ResultSet` or include it in the resource bracket. |

---

## 2️⃣ Correctness & Logic Review
| # | Issue | Severity | Recommendation |
|---|-------|----------|----------------|
| 1 | Off‑by‑one day calculation in `RentalEngine.calculateCost` | ⚠️ Medium | Clarify business rule. If same‑day rentals are allowed, change validation to `!endDate.isBefore(rentalDate)`. If inclusive days are required, add `+1` after `ChronoUnit.DAYS.between`. |
| 2 | `Rentals` constructors discard brand/model for new rentals | ⚠️ Low | Provide a dedicated constructor that only needs the mandatory fields, or lazily fetch brand/model when displaying. |
| 3 | `VehicleDAO.getVehicleById` may return `null` and cause NPE in caller | ⚠️ Low | Throw `RentalSystemException` when the vehicle is not found instead of returning `null`. |
| 4 | Commented‑out double‑return guard in `returnVehicle` | ⚠️ Low | Re‑introduce the check or create a dedicated “extend return” flow. |
| 5 | `SearchHandler` (UI) not inspected – potential similar validation gaps | ⚠️ Low | Apply the same validation standards as the service/DAO layers. |

---

## 3️⃣ Performance & Resource Management
| # | Issue | Severity | Recommendation |
|---|------|----------|----------------|
| 1 | Single `Connection` without pooling | ⚠️ Medium | Replace singleton connection with a connection pool (e.g., HikariCP). Acquire/close connections via try‑with‑resources. |
| 2 | `VehicleDAO.searchVehicles` builds SQL dynamically each call | ⚠️ Low | Acceptable for low volume; consider caching the query string if needed. |
| 3 | `RentalEngine.getOverdueRentals` loads all active rentals then filters in Java | ⚠️ Low | Add the date filter to the SQL (`WHERE planned_return_date < CURDATE() AND return_date IS NULL`). |
| 4 | No logging framework used | ⚠️ Low | Add SLF4J + Logback, log entry/exit of service methods and any long‑running queries. |
| 5 | ResultSet not closed in DAO methods (resource leaks) | ⚠️ Medium | Several DAO methods create ResultSet without try‑with‑resources, risking connection exhaustion. | Use try‑with‑resources for ResultSet or include it in the resource list. |

---

## 4️⃣ Maintainability & Code Quality
| # | Observation | Severity | Recommendation |
|---|--------------|----------|----------------|
| 1 | Inconsistent fully‑qualified exception names in `VehicleDAO` | ⚠️ Low | Use regular imports for `RentalSystemException`. |
| 2 | Mixed static vs fully‑qualified imports | ⚠️ Low | Adopt a consistent import style across the codebase. |
| 3 | Cyclomatic complexity of `returnVehicle` approaching 10 | ⚠️ Low | Extract validation steps into private helper methods. |
| 4 | Magic numbers (iterations, memory) lack documentation | ⚠️ Low | Add Javadoc explaining why the chosen Argon2 parameters were selected. |
| 5 | No Javadoc on public classes/methods | ⚠️ Low | Generate Javadoc for the service layer, DAO layer, and model objects. |
| 6 | GUI code directly calls DAOs (violates MVC) | ⚠️ Medium | Introduce a thin controller/presenter layer that mediates between Swing UI and the service layer. |
| 7 | Hard‑coded raw SQL strings scattered throughout DAOs | ⚠️ Low | Move column/table names to a constants class or use a lightweight query‑builder. |
| 8 | Enum value `Out_of_Service` stores a display string with space | ⚠️ Low | Document the `dbValue` mapping; consider renaming to `OUT_OF_SERVICE`. |

---

## 5️⃣ Design & Architecture Review
| # | Principle | Assessment | Suggested Improvement |
|---|-----------|------------|------------------------|
| 1 | **Single Responsibility** (SOLID) | `RentalEngine` handles availability, cost, and reporting. | Split reporting helpers into a `RentalReportingService`. |
| 2 | **Open/Closed** | Adding a new vehicle type requires editing `VehicleFactory`. | Use a registration map (`Map<String, Supplier<Vehicle>>`) so new types can be added without modifying the factory. |
| 3 | **Dependency Inversion** | `RentalEngine` creates DAO instances directly. | Inject DAO interfaces via constructor (or a tiny DI container). |
| 4 | **DRY – Validation** | Plate‑number validation lives only in the model setter. | Centralise validation in a `VehicleValidator` utility used by both model and DAO. |
| 5 | **Error‑Handling Consistency** | Mix of `RentalSystemException` and `IllegalArgumentException`. | Document when each exception type should be used; consider making `RentalSystemException` unchecked for convenience. |
| 6 | **Null Return Values** | DAO methods like `VehicleDAO.getVehicleById` return `null`, forcing callers to handle NPE. | Return `Optional<Vehicle>` or throw a specific exception to enforce explicit handling. |

---

## 6️⃣ Test Coverage & Quality
- **Current state:** No test classes found in the repository.
- **Recommendations:**
  1. Add **unit tests** for `RentalEngine` (availability, cost calculations, start/return flows) using JUnit 5 + Mockito.
  2. Add **DAO integration tests** using an in‑memory H2 database that loads the MySQL schema (`carls.sql`).
  3. Add **PasswordUtil** tests to verify hash/verify round‑trip and salt uniqueness.
  4. Configure **static analysis** (SpotBugs, PMD, Checkstyle) in the Maven lifecycle (`mvn verify`).
  5. Aim for **> 80 % line coverage** and ensure no critical findings from the static analysis tools.

---

## 7️⃣ Documentation Review
| Area | Current State | Recommendation |
|------|---------------|----------------|
| README | Exists but focuses on roadmap. | Keep as a high‑level overview; add a **section** linking to `REVIEW_FINDINGS.md`. |
| Javadoc | Missing on most public APIs. | Generate Javadoc for all public classes/methods. |
| Inline comments | Sparse; some dead/commented code remains. | Remove dead code, keep comments that explain *why* something is done. |
| SQL schema | Provided as a `.sql` file, but no migration tool. | Adopt Flyway (or Liquibase) migrations; place them in `src/main/resources/db/migration`. |
| Error messages | Mixed technical/user‑facing messages. | Centralise user‑facing messages in an enum or properties file. |

---

## 8️⃣ Dependency & License Scan
| Dependency | Suggested Action |
|-----------|-----------------|
| `argon2-jvm` | Verify you are on the latest secure version (≥ 2.7). |
| `dotenv-java` | Keep up‑to‑date; no known CVEs. |
| `mysql‑connector‑j` | Ensure version ≥ 8.0.33 (addresses recent CVEs). |
| `JUnit / Mockito` (to be added) | Use the latest releases. |

Run `mvn dependency:tree` and feed the output into **OWASP Dependency‑Check** to confirm no transitive vulnerabilities.

---

## 9️⃣ Actionable Todo List
| # | Action | Owner | Priority |
|---|--------|-------|----------|
| 1 | Make `DatabaseConnection.getInstance()` thread‑safe (synchronised or holder pattern). | Dev | High |
| 2 | Refactor `PasswordUtil` to create a new Argon2 instance per operation or synchronise usage. | Dev | High |
| 3 | Enforce stricter plate‑number regex in `Vehicle.setPlateNumber`. | Dev | Medium |
| 4 | Reinstate/clarify the double‑return guard in `RentalEngine.returnVehicle`. | Dev | Medium |
| 5 | Replace single `Connection` with a HikariCP pool and adjust DAO `getConn()`. | DevOps/DBA | Medium |
| 6 | Clarify off‑by‑one rental‑day calculation (validation or +1 days). | Dev | Medium |
| 7 | Add comprehensive Javadoc for all public APIs. | Docs | Low |
| 8 | Add unit & integration tests (JUnit 5, H2). | QA/Test | High |
| 9 | Introduce SLF4J + Logback logging throughout services/DAOs. | Dev | Low |
|10 | Create/Update `README.md` with a link to `REVIEW_FINDINGS.md`. | Docs | Low |
|11 | Migrate DB schema to Flyway migrations. | DevOps | Low |
|12 | Run OWASP Dependency‑Check and remediate any findings. | Security | Medium |
|13 | Extract DAO interfaces and inject them into `RentalEngine` (DI). | Dev | Medium |
|14 | Remove dead/commented code (e.g., the commented‑out return‑check block). | Dev | Low |
|15 | Ensure all DAO `ResultSet` objects are managed with try‑with‑resources. | Dev | Medium |
|16 | Refactor DAO getters to return `Optional<T>` instead of `null`. | Dev | Medium |
|17 | Add email format validation and password strength checks in `CustomerDAO`. | Dev | Medium |

---

## 🔚 Closing Thoughts
The project already demonstrates a solid OOP design and a functional Swing UI. By addressing the **thread‑safety**, **security hardening**, **testing gaps**, and **architectural refinements** outlined above, the codebase will satisfy the checklist requirements (no critical vulnerabilities, > 80 % test coverage, cyclomatic complexity < 10) and become easier to maintain, extend, and deploy.

Feel free to request deeper dive examples (e.g., the connection‑pool implementation or a sample JUnit test) – happy coding! 
