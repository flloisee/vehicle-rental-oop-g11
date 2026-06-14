package com.vehicle.rental.g11;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.vehicle.rental.g11.gui.VehicleFormPanel;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Vehicle Rental System - Vehicle Form");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(450, 350);
            frame.add(new VehicleFormPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}