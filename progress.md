# PROGRESS.md
## CARLS (Centralized Automobile Rental & Leasing System) — G11
**Branch:** `add-record`
**Last Updated:** 2026-06-14
**Contributor:** Greta

---

## ✅ Files Created / Modified This Session

### 📦 model/
| File | Status | Notes |
|---|---|---|
| `Vehicle.java` | ✅ Created | Abstract base class with `calculateRentalCost()`, plate max 7 chars validation |
| `Car.java` | ✅ Created | Extends `Vehicle`, standard daily rate × days |
| `Motorcycle.java` | ✅ Created | Extends `Vehicle`, 15% discount applied |
| `Truck.java` | ✅ Created | Extends `Vehicle`, 25% surcharge applied |
| `VehicleFactory.java` | ✅ Created | Factory method — creates Car/Motorcycle/Truck by type string |
| `VehicleStatus.java` | ✅ Created | Enum matching DB ENUM: `Available, Maintenance, Cleaning, Out_of_Service, Rented, Reserved` with `getDbValue()` / `fromDbValue()` mapping |
| `Customer.java` | ✅ Created | Added `email` field to match actual schema dump |
| `Rentals.java` | ✅ Created | Added `plannedReturnDate` field to match actual schema dump, `isReturned()` helper |

---

### 🗄️ dao/
| File | Status | Notes |
|---|---|---|
| `VehicleDAO.java` | ✅ Created | `addVehicle()`, `updateVehicle()`, `plateExists()`, `getVehicleById()` — uses `VehicleStatus.getDbValue()` for enum column |
| `RentalDAO.java` | ✅ Created | `addRental()`, `updateRental()`, `getRentalById()`, `getAllRentals()`, `getRentalsByCustomer()`, `getActiveRentals()`, `markAsReturned()`, `mapRow()` helper |

---

### 🗃️ db/
| File | Status | Notes |
|---|---|---|
| `DatabaseConnection.java` | ✅ Created | Singleton pattern, DB name corrected to `carls`, loads credentials from `.env` |

---

### ⚙️ service/
| File | Status | Notes |
|---|---|---|
| `RentalEngine.java` | ✅ Created | `isVehicleAvailable()`, `calculateCost()` (uses `ChronoUnit.DAYS`), `startRental()`, `returnVehicle()`, `getTotalRevenue()`, `getActiveRentalCount()`, `isCurrentlyRented()`, `getOverdueRentals()` |
| `PasswordUtil.java` | ✅ Created | Argon2id hashing — `hashPassword()`, `verifyPassword()` |

---

### 🚨 exception/
| File | Status | Notes |
|---|---|---|
| `RentalSystemException.java` | ✅ Created | Extends `Exception`, two constructors: message-only and message+cause (wraps `SQLException`) |

---

### 🖥️ gui/
| File | Status | Notes |
|---|---|---|
| `VehicleFrame.java` | ✅ Created | Full Add + Update UI — `JTable` showing all vehicles, form panel, row click pre-fills form, `addVehicle()`, `updateVehicle()`, `validateFields()`, `clearForm()` |
| `VehicleFormPanel.java` | ✅ Created | Standalone form panel (Add/Update mode via `editingVehicle` null check) — may be kept or removed since `VehicleFrame` absorbs it |
| `MainFrame.java` | ✅ Created | Dashboard with nav buttons to each module frame, stub handlers for unbuilt teammate modules |
| `LoginFrame.java` | 🔲 Left for teammate | Empty — owned by auth module assignee |
| `CustomerFrame.java` | 🔲 Left for teammate | Empty — owned by customer module assignee |
| `RentalFrame.java` | 🔲 Left for teammate | Empty — owned by rental module assignee |
| `ReportFrame.java` | 🔲 Left for teammate | Empty — owned by report module assignee |

---

### 🚀 Root
| File | Status | Notes |
|---|---|---|
| `Main.java` | ✅ Updated | Changed from `VehicleFormPanel` test launcher to `LoginFrame::new` proper entry point |

---

## 🔧 Schema Differences Found & Handled

Actual SQL dump (`carls.sql`) had these differences from the original plan — all corrected in code:

| Table | Column | Change |
|---|---|---|
| `Customers` | `email` | Added — was missing from original design |
| `Vehicles` | `plate_number` | `varchar(7)` not `varchar(20)` — validation added |
| `Vehicles` | `status` | `ENUM` not plain `varchar` — `VehicleStatus` Java enum created to match |
| `Rentals` | `planned_return_date` | Added — was missing from original design |

---

## ⚠️ Known Issues / To-Do

- [ ] `LoginFrame.java` is empty — `Main.java` currently points to it so app won't launch until teammate fills it in. Temporary workaround: change `Main.java` back to launch `MainFrame` directly for testing.
- [ ] Pull from `main` before final push to check for conflicts.
- [ ] `db.properties` / `.env` credentials need to be set locally by each teammate — not committed to GitHub.

---

## 🌿 Git Notes

- **Working branch:** `add-record`
- **Do NOT push to:** `main`
- **Push command:** `git push origin add-record`