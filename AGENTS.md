**Build & Run**
- macOS: run `dot_clean .` in the module directory before any Maven command.
- Requires Java 21 (set `JAVA_HOME` accordingly).
- All Maven commands must be executed **inside `com.vehicle.rental.g11`** where the `pom.xml` lives.
- Clean: `mvn clean`
- Compile: `mvn compile`
- Test (JUnit 5): `mvn test`
- Package runnable JAR with dependencies: `mvn package`
- Run the app: `java -jar target/vehicle-rental-system-1.0.0.jar`

**Database**
- Requires a local MySQL instance (≥ 8.0).
- Schema defined in `database/schema/vehicle_rental_g11_oop.sql`; load it via MySQL CLI (`SOURCE <path>/vehicle_rental_g11_oop.sql`) or Workbench. See `database/schema/DATABASE_SETUP.md` for detailed setup instructions.
- Connection settings are read from a `.env` file (loaded by `dotenv-java`), must be located in `com.vehicle.rental.g11/`.
- Create `.env` by copying the example: `cp com.vehicle.rental.g11/.env.example com.vehicle.rental.g11/.env`, then adjust `DB_URL`, `DB_USER`, `DB_PASSWORD` as needed.

**Entry Point**
- `com.vehicle.rental.g11.Main` launches the Swing UI via `LoginFrame`.
- All UI classes reside under `src/com/vehicle/rental/g11/gui/`.

**Package Layout**
- `model/` – OOP entities (`Vehicle`, `Car`, `Motorcycle`, `Truck`, `Customer`, `Rental`).
- `dao/` – CRUD Data Access Objects.
- `service/` – business logic (`RentalEngine`, `PasswordUtil`, `SearchHandler`).
- `exception/` – custom `RentalSystemException`.
- `gui/` – Swing frames and panels.

**Testing**
- No test sources currently; add JUnit 5 tests under `src/test/` and run with `mvn test`.

**Common Gotchas**
- Forgetting to run `dot_clean .` on macOS leads to compilation errors due to hidden `._*` files.
- If no `.env` is present, defaults are `DB_USER=root` and empty `DB_PASSWORD` – ensure the file reflects your MySQL credentials.
- Maven `shade` plugin sets the main class to `com.vehicle.rental.g11.Main`; the packaged JAR is executable.
