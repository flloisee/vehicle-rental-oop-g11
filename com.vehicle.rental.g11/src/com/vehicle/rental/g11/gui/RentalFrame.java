package com.vehicle.rental.g11.gui;

import java.time.LocalDate;
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
import com.vehicle.rental.g11.dao.VehicleDAO;
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
    private VehicleDAO vehicleDAO;
    private com.vehicle.rental.g11.dao.CustomerDAO customerDAO;
    private com.vehicle.rental.g11.service.RentalEngine rentalEngine;



    // Form fields
    private JTextField customerNameField, customerIDField, vehicleBrandModelField, vehiclePlateField, vehicleIDField,
                       rentalDateField, plannedReturnDateField,
                       returnDateField, totalCostField;
    private JButton addButton, updateButton, returnButton, clearButton;

    private int selectedRentalID = -1;

    private MainFrame mainFrame;
 
    public RentalFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        rentalDAO = new RentalDAO();
        vehicleDAO = new VehicleDAO();
        customerDAO = new com.vehicle.rental.g11.dao.CustomerDAO();
        rentalEngine = new com.vehicle.rental.g11.service.RentalEngine();
        setTitle("Rental Management");
        setSize(1250, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        setupSearchHandler();
        setupCustomerNameListener();
        setupVehicleSearchListener();
        loadRentals();
        setVisible(true);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 4, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Rental Details"));

        panel.add(new JLabel("Customer Name:"));
        customerNameField = new JTextField();
        panel.add(customerNameField);

        panel.add(new JLabel("Customer ID:"));
        customerIDField = new JTextField();
        customerIDField.setEditable(false);
        customerIDField.setBackground(new Color(240, 240, 240));
        panel.add(customerIDField);

        panel.add(new JLabel("Vehicle Brand/Model:"));
        vehicleBrandModelField = new JTextField();
        panel.add(vehicleBrandModelField);

        panel.add(new JLabel("Plate Number:"));
        vehiclePlateField = new JTextField();
        panel.add(vehiclePlateField);

        panel.add(new JLabel("Vehicle ID:"));
        vehicleIDField = new JTextField();
        vehicleIDField.setEditable(false);
        vehicleIDField.setBackground(new Color(240, 240, 240));
        panel.add(vehicleIDField);

        panel.add(new JLabel("Rental Date:"));
        rentalDateField = new JTextField();
        panel.add(createDatePickerPanel(rentalDateField));

        panel.add(new JLabel("Planned Return:"));
        plannedReturnDateField = new JTextField();
        panel.add(createDatePickerPanel(plannedReturnDateField));

        panel.add(new JLabel("Actual Return:"));
        returnDateField = new JTextField();
        returnDateField.setToolTipText("Leave blank if vehicle not yet returned");
        panel.add(createDatePickerPanel(returnDateField));

        panel.add(new JLabel("Total Cost (auto-calculated):"));
        totalCostField = new JTextField();
        totalCostField.setEditable(false);
        totalCostField.setBackground(new Color(240, 240, 240));
        panel.add(totalCostField);

        panel.add(new JLabel(""));
        panel.add(new JLabel(""));

        return panel;
    }

    private void updateAutoCalculatedCost() {
        try {
            String vIdStr = vehicleIDField.getText().trim();
            String rDateStr = rentalDateField.getText().trim();
            String pDateStr = plannedReturnDateField.getText().trim();
            String retDateStr = returnDateField.getText().trim();

            if (vIdStr.isEmpty() || rDateStr.isEmpty() || pDateStr.isEmpty()) {
                totalCostField.setText("");
                return;
            }

            int vehicleID = Integer.parseInt(vIdStr);
            LocalDate rentalDate = LocalDate.parse(rDateStr);
            LocalDate plannedReturnDate = LocalDate.parse(pDateStr);
            LocalDate actualReturnDate = retDateStr.isEmpty() ? null : LocalDate.parse(retDateStr);

            double cost = rentalEngine.calculateCost(vehicleID, rentalDate, plannedReturnDate, actualReturnDate);
            totalCostField.setText(String.format("%.2f", cost));
        } catch (Exception ex) {
            totalCostField.setText("");
        }
    }

    private JPanel createDatePickerPanel(JTextField field) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 0));
        field.setEditable(false);
        field.setBackground(new Color(240, 240, 240));

        JButton selectBtn = new JButton("Select Date");
        selectBtn.addActionListener(e -> {
            LocalDate initial = null;
            if (!field.getText().isEmpty()) {
                try {
                    initial = LocalDate.parse(field.getText());
                } catch (Exception ex) {
                    // Ignore parse error, use default now() in dialog
                }
            }
            new DatePickerDialog(this, initial, date -> {
                field.setText(date.toString());
                updateAutoCalculatedCost();
            }).setVisible(true);
        });

        panel.add(field);
        panel.add(selectBtn);
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

    private void setupCustomerNameListener() {
        customerNameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateCustomerID(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateCustomerID(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateCustomerID(); }

            private void updateCustomerID() {
                String name = customerNameField.getText().trim();
                if (name.isEmpty()) {
                    customerIDField.setText("");
                    return;
                }
                try {
                    List<com.vehicle.rental.g11.model.Customer> customers = customerDAO.searchCustomers(name);
                    if (customers.size() == 1) {
                        customerIDField.setText(customers.get(0).getCustomerID());
                    } else if (customers.size() > 1) {
                        customerIDField.setText("Multiple found");
                    } else {
                        customerIDField.setText("Not found");
                    }
                } catch (RentalSystemException e) {
                    customerIDField.setText("Error");
                }
            }
        });
    }

    private void setupVehicleSearchListener() {
        javax.swing.event.DocumentListener vehicleListener = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateVehicleID(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateVehicleID(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateVehicleID(); }

            private void updateVehicleID() {
                String brandModel = vehicleBrandModelField.getText().trim();
                String plate = vehiclePlateField.getText().trim();

                if (brandModel.isEmpty() && plate.isEmpty()) {
                    vehicleIDField.setText("");
                    updateAutoCalculatedCost();
                    return;
                }

                try {
                    List<com.vehicle.rental.g11.model.Vehicle> vehicles = vehicleDAO.searchVehicles(brandModel, plate);
                    if (vehicles.size() == 1) {
                        vehicleIDField.setText(String.valueOf(vehicles.get(0).getVehicleID()));
                    } else if (vehicles.size() > 1) {
                        vehicleIDField.setText("Multiple found");
                    } else {
                        vehicleIDField.setText("Not found");
                    }
                    updateAutoCalculatedCost();
                } catch (RentalSystemException e) {
                    vehicleIDField.setText("Error");
                    updateAutoCalculatedCost();
                }
            }
        };

        vehicleBrandModelField.getDocument().addDocumentListener(vehicleListener);
        vehiclePlateField.getDocument().addDocumentListener(vehicleListener);
    }


    private void performSearch(String query) {
        tableModel.setRowCount(0);
        try {
            List<Rentals> results = rentalDAO.searchRentals(query);
                for (Rentals r : results) {
                    tableModel.addRow(new Object[]{
                        r.getRentalID(),
                        r.getCustomerID(),
                        r.getFirstName(),
                        r.getLastName(),
                        r.getVehicleID(),
                        r.getVehicleBrand(),
                        r.getVehicleModel(),
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

        String[] columns = {"Rental ID", "Customer ID", "First Name", "Last Name", "Vehicle ID", "Vehicle Brand", "Vehicle Model",
                                  "Rental Date", "Planned Return", "Actual Return", "Total Cost"};
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
        JButton backButton = new JButton("Back to Main Menu");
 
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

                Rentals rental = new Rentals(selectedRentalID, customerID, null, vehicleID, null, null, rDate, pDate, retDate, cost);
                if (rentalDAO.updateRental(rental)) {
                    if (retDate != null) {
                        try {
                            rentalEngine.returnVehicle(selectedRentalID, retDate);
                        } catch (RentalSystemException ex) {
                            // Rental record is already updated, but vehicle status update might fail
                            // In a real app, we'd handle this more robustly
                        }
                    }
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
        backButton.addActionListener(e -> {
            dispose();
            mainFrame.setVisible(true);
        });
 
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(returnButton);
        panel.add(clearButton);
        panel.add(backButton);

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
                    r.getFirstName(),
                    r.getLastName(),
                    r.getVehicleID(),
                    r.getVehicleBrand(),
                    r.getVehicleModel(),
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

    private String cellText(Object value) {
        return value != null ? value.toString() : "";
    }

    private void loadSelectedRowIntoForm() {
        int row = rentalTable.getSelectedRow();
        if (row == -1) return;

        selectedRentalID = (int) tableModel.getValueAt(row, 0);
        String firstName = cellText(tableModel.getValueAt(row, 2));
        String lastName = cellText(tableModel.getValueAt(row, 3));
        customerNameField.setText(firstName + " " + lastName);
        customerIDField.setText(cellText(tableModel.getValueAt(row, 1)));
        vehicleIDField.setText(cellText(tableModel.getValueAt(row, 4)));
        rentalDateField.setText(cellText(tableModel.getValueAt(row, 7)));
        plannedReturnDateField.setText(cellText(tableModel.getValueAt(row, 8)));

        Object returnVal = tableModel.getValueAt(row, 9);
        returnDateField.setText(returnVal != null ? returnVal.toString() : "");
        totalCostField.setText(cellText(tableModel.getValueAt(row, 10)));
    }

    private void clearForm() {
        selectedRentalID = -1;
        customerNameField.setText("");
        customerIDField.setText("");
        vehicleBrandModelField.setText("");
        vehiclePlateField.setText("");
        vehicleIDField.setText("");
        rentalDateField.setText("");
        plannedReturnDateField.setText("");
        returnDateField.setText("");
        totalCostField.setText("");
        rentalTable.clearSelection();
    }
}