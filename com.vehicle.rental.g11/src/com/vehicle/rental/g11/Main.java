package com.vehicle.rental.g11;

import javax.swing.SwingUtilities;

import com.vehicle.rental.g11.dao.EmployeeDAO;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.gui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        try {
            new EmployeeDAO().seedDefaultAdmin();
        } catch (RentalSystemException e) {
            System.err.println("Warning: Could not seed default admin account: " + e.getMessage());
        }
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}