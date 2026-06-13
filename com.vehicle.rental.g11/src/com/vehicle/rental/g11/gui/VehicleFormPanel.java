package com.vehicle.rental.g11.gui;

import com.vehicle.rental.g11.dao.VehicleDAO;
import com.vehicle.rental.g11.model.Vehicle;
import com.vehicle.rental.g11.model.VehicleFactory;
import com.vehicle.rental.g11.model.VehicleStatus;

import javax.swing.*;
import java.awt.*;

public class VehicleFormPanel extends JPanel {

    private JTextField brandField, modelField, plateField, rateField;
    private JComboBox<String> typeBox;
    private JComboBox<VehicleStatus> statusBox;
    private JButton saveButton;

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private Vehicle editingVehicle; // null = Add mode, non-null = Update mode

    public VehicleFormPanel() {
        setLayout(new GridLayout(7, 2, 5, 5));

        add(new JLabel("Brand:"));
        brandField = new JTextField();
        add(brandField);

        add(new JLabel("Model:"));
        modelField = new JTextField();
        add(modelField);

        add(new JLabel("Type:"));
        typeBox = new JComboBox<>(new String[]{"Car", "Motorcycle", "Truck"});
        add(typeBox);

        add(new JLabel("Plate Number (max 7 chars):"));
        plateField = new JTextField();
        add(plateField);

        add(new JLabel("Daily Rate:"));
        rateField = new JTextField();
        add(rateField);

        add(new JLabel("Status:"));
        statusBox = new JComboBox<>(VehicleStatus.values());
        add(statusBox);

        saveButton = new JButton("Save");
        add(saveButton);

        saveButton.addActionListener(e -> saveVehicle());
    }

    // Call this to switch the form into Update mode and pre-fill fields
    public void loadVehicleForEdit(Vehicle vehicle) {
        this.editingVehicle = vehicle;
        brandField.setText(vehicle.getBrand());
        modelField.setText(vehicle.getModel());
        typeBox.setSelectedItem(vehicle.getType());
        plateField.setText(vehicle.getPlateNumber());
        rateField.setText(String.valueOf(vehicle.getDailyRate()));
        statusBox.setSelectedItem(vehicle.getStatus());
        saveButton.setText("Update");
    }

    // Reset form to Add mode
    public void resetForm() {
        editingVehicle = null;
        brandField.setText("");
        modelField.setText("");
        plateField.setText("");
        rateField.setText("");
        typeBox.setSelectedIndex(0);
        statusBox.setSelectedIndex(0);
        saveButton.setText("Save");
    }

    private void saveVehicle() {
        // ---- Validation ----
        String brand = brandField.getText().trim();
        String model = modelField.getText().trim();
        String plate = plateField.getText().trim();
        String rateText = rateField.getText().trim();
        String type = (String) typeBox.getSelectedItem();
        VehicleStatus status = (VehicleStatus) statusBox.getSelectedItem();

        if (brand.isEmpty() || model.isEmpty() || plate.isEmpty() || rateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (plate.length() > 7) {
            JOptionPane.showMessageDialog(this, "Plate number must be 7 characters or fewer.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double dailyRate;
        try {
            dailyRate = Double.parseDouble(rateText);
            if (dailyRate <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Daily rate must be a positive number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ---- ADD MODE ----
        if (editingVehicle == null) {
            if (vehicleDAO.plateExists(plate, -1)) {
                JOptionPane.showMessageDialog(this, "Plate number already exists.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Vehicle newVehicle = VehicleFactory.createVehicle(type, 0, brand, model, plate, dailyRate, status);
            boolean success = vehicleDAO.addVehicle(newVehicle);

            if (success) {
                JOptionPane.showMessageDialog(this, "Vehicle added successfully!");
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        // ---- UPDATE MODE ----
        } else {
            if (vehicleDAO.plateExists(plate, editingVehicle.getVehicleID())) {
                JOptionPane.showMessageDialog(this, "Plate number already used by another vehicle.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Vehicle updatedVehicle = VehicleFactory.createVehicle(type, editingVehicle.getVehicleID(), brand, model, plate, dailyRate, status);
            boolean success = vehicleDAO.updateVehicle(updatedVehicle);

            if (success) {
                JOptionPane.showMessageDialog(this, "Vehicle updated successfully!");
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}