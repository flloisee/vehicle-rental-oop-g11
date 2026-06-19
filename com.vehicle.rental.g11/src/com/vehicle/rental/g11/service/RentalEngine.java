package com.vehicle.rental.g11.service;

import com.vehicle.rental.g11.dao.RentalDAO;
import com.vehicle.rental.g11.dao.VehicleDAO;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Rentals;
import com.vehicle.rental.g11.model.Vehicle;
import com.vehicle.rental.g11.model.VehicleStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
}