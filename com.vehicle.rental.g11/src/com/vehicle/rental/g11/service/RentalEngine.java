package com.vehicle.rental.g11.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.vehicle.rental.g11.dao.RentalDAO;
import com.vehicle.rental.g11.dao.VehicleDAO;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Rentals;
import com.vehicle.rental.g11.model.Vehicle;
import com.vehicle.rental.g11.model.VehicleStatus;

public class RentalEngine {

    private final VehicleDAO vehicleDAO;
    private final RentalDAO rentalDAO;

    public RentalEngine() {
        this.vehicleDAO = new VehicleDAO();
        this.rentalDAO = new RentalDAO();
    }

    // -------------------------------------------------------
    // AVAILABILITY CHECK
    // -------------------------------------------------------

    // Returns true only if vehicle exists AND status is Available
    public boolean isVehicleAvailable(int vehicleID) throws RentalSystemException {
        Vehicle vehicle = vehicleDAO.getVehicleById(vehicleID);

        if (vehicle == null) {
            throw new RentalSystemException("Vehicle with ID " + vehicleID + " does not exist.");
        }

        return vehicle.getStatus() == VehicleStatus.Available;
    }

    // -------------------------------------------------------
    // COST CALCULATION
    // -------------------------------------------------------

    // Calculates rental cost using the vehicle's own polymorphic method
    // Days are calculated from rentalDate to plannedReturnDate (inclusive)
    public double calculateCost(int vehicleID, LocalDate rentalDate, LocalDate plannedReturnDate, LocalDate actualReturnDate)
            throws RentalSystemException {

        if (rentalDate == null) {
            throw new RentalSystemException("Rental date cannot be null.");
        }

        LocalDate endDate = (actualReturnDate != null) ? actualReturnDate : plannedReturnDate;

        if (endDate == null) {
            throw new RentalSystemException("End date (planned or actual return date) cannot be null.");
        }

        if (!endDate.isAfter(rentalDate)) {
            throw new RentalSystemException("Return date must be after rental date.");
        }

        Vehicle vehicle = vehicleDAO.getVehicleById(vehicleID);

        if (vehicle == null) {
            throw new RentalSystemException("Vehicle with ID " + vehicleID + " does not exist.");
        }

        int days = (int) ChronoUnit.DAYS.between(rentalDate, endDate);

        return vehicle.calculateRentalCost(days);
    }

    // -------------------------------------------------------
    // START RENTAL
    // -------------------------------------------------------

    // Full flow: check availability → calculate cost → save rental → update vehicle status
    public Rentals startRental(String customerID, int vehicleID,
                                LocalDate rentalDate, LocalDate plannedReturnDate)
            throws RentalSystemException {

        // 1. Check vehicle is available
        if (!isVehicleAvailable(vehicleID)) {
            throw new RentalSystemException("Vehicle is not available for rental.");
        }

        // 2. Validate dates
        if (!plannedReturnDate.isAfter(rentalDate)) {
            throw new RentalSystemException("Planned return date must be after rental date.");
        }

        // 3. Calculate cost
        double totalCost = calculateCost(vehicleID, rentalDate, plannedReturnDate, null);

        // 4. Build Rentals object (rentalID=0, DB will auto-increment; returnDate=null, not returned yet)
        Rentals rental = new Rentals(0, customerID, null, vehicleID, null, null, rentalDate, plannedReturnDate, null, totalCost);

        // 5. Save rental to DB
        boolean saved = rentalDAO.addRental(rental);
        if (!saved) {
            throw new RentalSystemException("Failed to save rental record to database.");
        }

        // 6. Update vehicle status to Rented
        Vehicle vehicle = vehicleDAO.getVehicleById(vehicleID);
        vehicle.setStatus(VehicleStatus.Rented);
        boolean updated = vehicleDAO.updateVehicle(vehicle);
        if (!updated) {
            throw new RentalSystemException("Rental saved but failed to update vehicle status.");
        }

        return rental;
    }

    // -------------------------------------------------------
    // RETURN VEHICLE
    // -------------------------------------------------------

    // Marks rental as returned and sets vehicle back to Available
    public void returnVehicle(int rentalID, LocalDate actualReturnDate)
            throws RentalSystemException {

        // 1. Get the rental
        Rentals rental = rentalDAO.getRentalById(rentalID);
        if (rental == null) {
            throw new RentalSystemException("Rental with ID " + rentalID + " does not exist.");
        }

        // 2. Check not already returned
        // Removed the strict check to allow updating the return date of an already returned vehicle
        /*
        if (rental.isReturned()) {
            throw new RentalSystemException("This rental has already been returned.");
        }
        */

        // 3. Validate return date
        if (actualReturnDate.isBefore(rental.getRentalDate())) {
            throw new RentalSystemException("Return date cannot be before the rental start date.");
        }

        // 4. Recalculate cost and mark rental as returned
        double updatedCost = calculateCost(rental.getVehicleID(), rental.getRentalDate(), rental.getPlannedReturnDate(), actualReturnDate);
        rental.setReturnDate(actualReturnDate);
        rental.setTotalCost(updatedCost);

        boolean updated = rentalDAO.updateRental(rental);
        if (!updated) {
            throw new RentalSystemException("Failed to update rental record with return date and final cost.");
        }

        // 5. Set vehicle back to Available
        Vehicle vehicle = vehicleDAO.getVehicleById(rental.getVehicleID());
        if (vehicle != null) {
            vehicle.setStatus(VehicleStatus.Available);
            vehicleDAO.updateVehicle(vehicle);
        }
    }

    // -------------------------------------------------------
    // REPORTING HELPERS (used by ReportPanel - Phase 5)
    // -------------------------------------------------------

    // Total revenue across all completed rentals
    public double getTotalRevenue() throws RentalSystemException {
        List<Rentals> all = rentalDAO.getAllRentals();
        return all.stream()
                  .filter(Rentals::isReturned)
                  .mapToDouble(Rentals::getTotalCost)
                  .sum();
    }

    // Count of currently active (unreturned) rentals
    public int getActiveRentalCount() throws RentalSystemException {
        return rentalDAO.getActiveRentals().size();
    }

    // Check if a specific vehicle is currently rented out
    public boolean isCurrentlyRented(int vehicleID) throws RentalSystemException {
        List<Rentals> active = rentalDAO.getActiveRentals();
        return active.stream()
                     .anyMatch(r -> r.getVehicleID() == vehicleID);
    }

    // Get all overdue rentals (planned return date passed, not yet returned)
    public List<Rentals> getOverdueRentals() throws RentalSystemException {
        List<Rentals> active = rentalDAO.getActiveRentals();
        LocalDate today = LocalDate.now();

        return active.stream()
                     .filter(r -> r.getPlannedReturnDate().isBefore(today))
                     .toList();
    }

    public List<Rentals> getPendingPaymentRentals() throws RentalSystemException {
        return getOverdueRentals();
    }

    public List<Rentals> getTodaysRentals() throws RentalSystemException {
        LocalDate today = LocalDate.now();
        return rentalDAO.getAllRentals().stream()
                        .filter(r -> r.getRentalDate().equals(today))
                        .toList();
    }

    public List<Rentals> getActiveRentals() throws RentalSystemException {
        return rentalDAO.getActiveRentals();
    }

    public List<Rentals> getAllRentals() throws RentalSystemException {
        return rentalDAO.getAllRentals();
    }

    // -------------------------------------------------------
    // DASHBOARD ANALYTICS METHODS
    // -------------------------------------------------------

    // Get count of overdue rentals, which represent pending payment balances
    public int getPendingPaymentsCount() throws RentalSystemException {
        return getOverdueRentals().size();
    }

    // Get total unpaid balance from overdue rentals
    public double getTotalUnpaidBalance() throws RentalSystemException {
        return getOverdueRentals().stream()
                                  .mapToDouble(Rentals::getTotalCost)
                                  .sum();
    }

    // Get count of rentals happening today
    public int getTodaysRentalsCount() throws RentalSystemException {
        List<Rentals> all = rentalDAO.getAllRentals();
        LocalDate today = LocalDate.now();
        return (int) all.stream()
                        .filter(r -> r.getRentalDate().equals(today))
                        .count();
    }

    // Get count of overdue rentals today
    public int getOverdueTodayCount() throws RentalSystemException {
        return getOverdueRentals().size();
    }

    // Get the most rented vehicle and its count
    public String getMostRentedVehicle() throws RentalSystemException {
        List<Rentals> all = rentalDAO.getAllRentals();
        return all.stream()
                  .collect(java.util.stream.Collectors.groupingBy(
                      r -> r.getVehicleBrand() + " " + r.getVehicleModel(),
                      java.util.stream.Collectors.counting()
                  ))
                  .entrySet().stream()
                  .max(java.util.Map.Entry.comparingByValue())
                  .map(e -> e.getKey() + " (" + e.getValue() + " rentals)")
                  .orElse("No rentals yet");
    }

    // Get vehicle utilization rate (% of vehicles currently rented)
    public double getVehicleUtilizationRate() throws RentalSystemException {
        int totalVehicles = vehicleDAO.getAllVehicles().size();
        if (totalVehicles == 0) return 0;
        
        int rentedVehicles = (int) vehicleDAO.getAllVehicles().stream()
                                             .filter(v -> v.getStatus() == VehicleStatus.Rented)
                                             .count();
        return (rentedVehicles * 100.0) / totalVehicles;
    }

    // Get top customer by rental count
    public String getTopCustomer() throws RentalSystemException {
        List<Rentals> all = rentalDAO.getAllRentals();
        return all.stream()
                  .collect(java.util.stream.Collectors.groupingBy(
                      Rentals::getCustomerName,
                      java.util.stream.Collectors.counting()
                  ))
                  .entrySet().stream()
                  .max(java.util.Map.Entry.comparingByValue())
                  .map(e -> e.getKey() + " (" + e.getValue() + " rentals)")
                  .orElse("No customers yet");
    }

    // Get average rental duration in days
    public double getAverageRentalDuration() throws RentalSystemException {
        List<Rentals> all = rentalDAO.getAllRentals();
        if (all.isEmpty()) return 0;
        
        return all.stream()
                  .mapToLong(r -> ChronoUnit.DAYS.between(r.getRentalDate(), r.getPlannedReturnDate()))
                  .average()
                  .orElse(0);
    }

    // Determine rental status: only ACTIVE, OVERDUE, or COMPLETED
    public String getRentalStatus(Rentals rental) {
        if (!rental.isReturned()) {
            LocalDate today = LocalDate.now();
            if (rental.getPlannedReturnDate().isBefore(today)) {
                return "OVERDUE";
            } else {
                return "ACTIVE";
            }
        } else {
            return "COMPLETED";
        }
    }

    // Get all vehicles for utilization calculation
    public java.util.List<Vehicle> getAllVehicles() throws RentalSystemException {
        return vehicleDAO.getAllVehicles();
    }
}