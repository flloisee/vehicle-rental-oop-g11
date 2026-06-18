package com.vehicle.rental.g11.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.vehicle.rental.g11.dao.RentalDAO;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Rentals;
import com.vehicle.rental.g11.service.SearchHandler;

public class RentalFrame extends JFrame {


    private JTable rentalTable;
    private DefaultTableModel tableModel;

    // Search fields
    private JTextField searchField;
    private SearchHandler searchHandler;
    private RentalDAO rentalDAO;
    private com.vehicle.rental.g11.service.RentalEngine rentalEngine;


    // Form fields
    private JTextField customerIDField, vehicleIDField,

                       rentalDateField, plannedReturnDateField,
                       returnDateField, totalCostField;
    private JButton addButton, updateButton, returnButton, clearButton;

    private int selectedRentalID = -1;

    public RentalFrame() {
        rentalDAO = new RentalDAO();
        rentalEngine = new com.vehicle.rental.g11.service.RentalEngine();
        setTitle("Rental Management");
        setSize(1000, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        setupSearchHandler();
        loadRentals();
        setVisible(true);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 4, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Rental Details"));

        panel.add(new JLabel("Customer ID:"));
        customerIDField = new JTextField();
        panel.add(customerIDField);

        panel.add(new JLabel("Vehicle ID:"));
        vehicleIDField = new JTextField();
        panel.add(vehicleIDField);

        panel.add(new JLabel("Rental Date (YYYY-MM-DD):"));
        rentalDateField = new JTextField();
        panel.add(rentalDateField);

        panel.add(new JLabel("Planned Return (YYYY-MM-DD):"));
        plannedReturnDateField = new JTextField();
        panel.add(plannedReturnDateField);

        panel.add(new JLabel("Actual Return (YYYY-MM-DD):"));
        returnDateField = new JTextField();
        returnDateField.setToolTipText("Leave blank if vehicle not yet returned");
        panel.add(returnDateField);

        panel.add(new JLabel("Total Cost (auto-calculated):"));
        totalCostField = new JTextField();
        totalCostField.setEditable(false);
        totalCostField.setBackground(new Color(240, 240, 240));
        panel.add(totalCostField);

        // Fill remaining grid cells
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));

        return panel;
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Search: "));
        searchField = new JTextField(20);
        panel.add(searchField);
        return panel;
    }

    private void setupSearchHandler() {
        searchHandler = new SearchHandler(query -> {
            if (query == null || query.trim().isEmpty()) {
                loadRentals();
            } else {
                performSearch(query);
            }
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            private void updateSearch() {
                searchHandler.onQueryChanged(searchField.getText());
            }
        });
    }

    private void performSearch(String query) {
        tableModel.setRowCount(0);
        try {
            List<Rentals> results = rentalDAO.searchRentals(query);
            for (Rentals r : results) {
                tableModel.addRow(new Object[]{
                    r.getRentalID(),
                    r.getCustomerID(),
                    r.getVehicleID(),
                    r.getRentalDate(),
                    r.getPlannedReturnDate(),
                    r.getReturnDate(),
                    r.getTotalCost()
                });
            }
        } catch (RentalSystemException e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(buildSearchPanel(), BorderLayout.NORTH);

        String[] columns = {"Rental ID", "Customer ID", "Vehicle ID",
                            "Rental Date", "Planned Return",
                            "Actual Return", "Total Cost"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        rentalTable = new JTable(tableModel);
        rentalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        rentalTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRowIntoForm();
            }
        });

        panel.add(new JScrollPane(rentalTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        addButton    = new JButton("Add Rental");
        updateButton = new JButton("Update Rental");
        returnButton = new JButton("Mark as Returned");
        clearButton  = new JButton("Clear Form");

        // -------------------------------------------------------
        // Wire to RentalEngine / RentalDAO
        // -------------------------------------------------------
        addButton.addActionListener(e -> {
            try {
                String customerID = customerIDField.getText().trim();
                int vehicleID = Integer.parseInt(vehicleIDField.getText().trim());
                java.time.LocalDate rDate = java.time.LocalDate.parse(rentalDateField.getText().trim());
                java.time.LocalDate pDate = java.time.LocalDate.parse(plannedReturnDateField.getText().trim());

                if (customerID.isEmpty()) throw new Exception("Customer ID is required.");

                rentalEngine.startRental(customerID, vehicleID, rDate, pDate);
                JOptionPane.showMessageDialog(this, "Rental started successfully!");
                loadRentals();
                clearForm();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vehicle ID must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error starting rental: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            if (selectedRentalID == -1) {
                JOptionPane.showMessageDialog(this, "Please select a rental to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String customerID = customerIDField.getText().trim();
                int vehicleID = Integer.parseInt(vehicleIDField.getText().trim());
                java.time.LocalDate rDate = java.time.LocalDate.parse(rentalDateField.getText().trim());
                java.time.LocalDate pDate = java.time.LocalDate.parse(plannedReturnDateField.getText().trim());
                java.time.LocalDate retDate = returnDateField.getText().trim().isEmpty() ? 
                                            null : java.time.LocalDate.parse(returnDateField.getText().trim());
                double cost = Double.parseDouble(totalCostField.getText().trim());

                Rentals rental = new Rentals(selectedRentalID, customerID, vehicleID, rDate, pDate, retDate, cost);
                if (rentalDAO.updateRental(rental)) {
                    JOptionPane.showMessageDialog(this, "Rental updated successfully!");
                    loadRentals();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update rental.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating rental: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        returnButton.addActionListener(e -> {
            if (selectedRentalID == -1) {
                JOptionPane.showMessageDialog(this, "Please select a rental to mark as returned.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String returnDateStr = returnDateField.getText().trim();
                if (returnDateStr.isEmpty()) {
                    returnDateStr = java.time.LocalDate.now().toString();
                }
                java.time.LocalDate returnDate = java.time.LocalDate.parse(returnDateStr);
                
                rentalEngine.returnVehicle(selectedRentalID, returnDate);
                JOptionPane.showMessageDialog(this, "Vehicle returned successfully!");
                loadRentals();
                clearForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error returning vehicle: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearForm());

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(returnButton);
        panel.add(clearButton);

        return panel;
    }

    private void loadRentals() {
        tableModel.setRowCount(0);
        try {
            List<Rentals> rentals = rentalDAO.getAllRentals();
            for (Rentals r : rentals) {
                tableModel.addRow(new Object[]{
                    r.getRentalID(),
                    r.getCustomerID(),
                    r.getVehicleID(),
                    r.getRentalDate(),
                    r.getPlannedReturnDate(),
                    r.getReturnDate(),
                    r.getTotalCost()
                });
            }
        } catch (RentalSystemException e) {
            JOptionPane.showMessageDialog(this, "Error loading rentals: " + e.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedRowIntoForm() {
        int row = rentalTable.getSelectedRow();
        if (row == -1) return;

        selectedRentalID = (int) tableModel.getValueAt(row, 0);
        customerIDField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        vehicleIDField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        rentalDateField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        plannedReturnDateField.setText(String.valueOf(tableModel.getValueAt(row, 4)));

        Object returnVal = tableModel.getValueAt(row, 5);
        returnDateField.setText(returnVal != null ? returnVal.toString() : "");
        totalCostField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
    }

    private void clearForm() {
        selectedRentalID = -1;
        customerIDField.setText("");
        vehicleIDField.setText("");
        rentalDateField.setText("");
        plannedReturnDateField.setText("");
        returnDateField.setText("");
        totalCostField.setText("");
        rentalTable.clearSelection();
    }
}