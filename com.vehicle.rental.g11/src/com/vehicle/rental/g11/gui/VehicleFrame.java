package com.vehicle.rental.g11.gui;

import com.vehicle.rental.g11.dao.VehicleDAO;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Vehicle;
import com.vehicle.rental.g11.model.VehicleFactory;
import com.vehicle.rental.g11.model.VehicleStatus;
import com.vehicle.rental.g11.service.SearchHandler;
 
import javax.swing.*;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.vehicle.rental.g11.gui.UITheme;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleFrame extends JFrame {

    private JTable vehicleTable;
    private DefaultTableModel tableModel;
    private VehicleDAO vehicleDAO;

    // Form fields
    private JTextField brandField, modelField, plateField, rateField;
    private JTextField searchField;
    private JComboBox<String> typeBox;
    private JComboBox<VehicleStatus> statusBox;
    private JButton addButton, updateButton, clearButton;
    private SearchHandler searchHandler;
 
    private int selectedVehicleID = -1; // -1 means no row selected


    private MainFrame mainFrame;
 
    public VehicleFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        vehicleDAO = new VehicleDAO();

        setTitle("Vehicle Management");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UITheme.BG);

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
 
        setupSearchHandler();
        loadVehicles();
addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                VehicleFrame.this.mainFrame.setVisible(true);
            }
        });
        setVisible(true);
    }


    // -------------------------------------------------------
    // FORM PANEL (top) - input fields
    // -------------------------------------------------------
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 8, 8));
        panel.setBackground(UITheme.BG);
        // Styled titled border matching theme
        javax.swing.border.TitledBorder titled = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.ACCENT, 1), "Vehicle Details");
        titled.setTitleColor(UITheme.TEXT_PRIMARY);
        panel.setBorder(titled);

        JLabel lbl;
        lbl = new JLabel("Brand:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(lbl);
        brandField = new JTextField();
        UITheme.styleField(brandField);
        panel.add(brandField);

        lbl = new JLabel("Model:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(lbl);
        modelField = new JTextField();
        UITheme.styleField(modelField);
        panel.add(modelField);

        lbl = new JLabel("Type:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(lbl);
        typeBox = new JComboBox<>(new String[]{"Car", "Motorcycle", "Truck"});
        UITheme.styleComboBox(typeBox);
        panel.add(typeBox);

        lbl = new JLabel("Plate Number (max 7):");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(lbl);
        plateField = new JTextField();
        UITheme.styleField(plateField);
        panel.add(plateField);

        lbl = new JLabel("Daily Rate:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(lbl);
        rateField = new JTextField();
        UITheme.styleField(rateField);
        panel.add(rateField);

        lbl = new JLabel("Status:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(lbl);
        statusBox = new JComboBox<>(VehicleStatus.values());
        UITheme.styleComboBox(statusBox);
        panel.add(statusBox);

        return panel;
    }

    // -------------------------------------------------------
    // SEARCH PANEL
    // -------------------------------------------------------
    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(UITheme.BG);
        JLabel lbl = new JLabel("Search: ");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(lbl);
        searchField = new JTextField(20);
        UITheme.styleField(searchField);
        panel.add(searchField);
        return panel;
    }

    private void setupSearchHandler() {
        searchHandler = new SearchHandler(query -> {
            if (query == null || query.trim().isEmpty()) {
                loadVehicles();
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

    // -------------------------------------------------------
    // TABLE PANEL (middle) - shows all vehicles
    // -------------------------------------------------------
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(buildSearchPanel(), BorderLayout.NORTH);

        String[] columns = {"ID", "Brand", "Model", "Type", "Plate", "Daily Rate", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // table is read-only, edit via form
            }
        };

        vehicleTable = new JTable(tableModel) {
            private int hoverRow = -1;
            {
                // Track mouse movement to add hover effect
                addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(java.awt.event.MouseEvent e) {
                        int row = rowAtPoint(e.getPoint());
                        if (row != hoverRow) {
                            hoverRow = row;
                            repaint();
                        }
                    }
                });
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hoverRow = -1;
                        repaint();
                    }
                });
            }
            @Override
            public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                java.awt.Component c = super.prepareRenderer(renderer, row, column);
                // Apply hover background when not selected
                if (row == hoverRow && !isRowSelected(row)) {
                    c.setBackground(UITheme.PURPLE_LIGHT);
                } else {
                    c.setBackground(UITheme.BG);
                }
                return c;
            }
        };
        UITheme.styleTable(vehicleTable);
        // Modern row height and spacing
        vehicleTable.setRowHeight(28);
        vehicleTable.setIntercellSpacing(new java.awt.Dimension(0, 5));
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // When a row is clicked, load it into the form for editing
        vehicleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRowIntoForm();
            }
        });

        panel.add(new JScrollPane(vehicleTable), BorderLayout.CENTER);
        return panel;
    }

    // -------------------------------------------------------
    // BUTTON PANEL (bottom)
    // -------------------------------------------------------
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBackground(UITheme.BG);

        addButton = UITheme.roundedButton("Add Vehicle");
        updateButton = UITheme.roundedButton("Update Vehicle");
        clearButton = UITheme.roundedButton("Clear Form");
        JButton backButton = UITheme.roundedButton("Back to Main Menu");
 
        addButton.addActionListener(e -> addVehicle());
        updateButton.addActionListener(e -> updateVehicle());
        clearButton.addActionListener(e -> clearForm());
        backButton.addActionListener(e -> {
            dispose();
            mainFrame.setVisible(true);
        });
 
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(clearButton);
        panel.add(backButton);

        return panel;
    }

    // -------------------------------------------------------
    // LOAD ALL VEHICLES INTO TABLE
    // -------------------------------------------------------
    private void loadVehicles() {
        tableModel.setRowCount(0); // clear existing rows
 
        String sql = "SELECT * FROM Vehicles ORDER BY vehicleID ASC";
 
        try (Connection conn = com.vehicle.rental.g11.db.DatabaseConnection
                                    .getInstance().getConnection();
              PreparedStatement ps = conn.prepareStatement(sql);
              ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("vehicleID"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getString("type"),
                    rs.getString("plate_number"),
                    rs.getDouble("daily_rate"),
                    rs.getString("status")
                });
            }
 
        } catch (SQLException | RentalSystemException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load vehicles: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch(String query) {
        tableModel.setRowCount(0);
        try {
            List<Vehicle> results = vehicleDAO.searchVehicles(query);
            for (Vehicle v : results) {
                tableModel.addRow(new Object[]{
                    v.getVehicleID(),
                    v.getBrand(),
                    v.getModel(),
                    v.getType(),
                    v.getPlateNumber(),
                    v.getDailyRate(),
                    v.getStatus().getDbValue()
                });
            }
        } catch (RentalSystemException e) {
            JOptionPane.showMessageDialog(this, 
                "Search error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // -------------------------------------------------------
    // LOAD SELECTED ROW INTO FORM
    // -------------------------------------------------------
    private void loadSelectedRowIntoForm() {
        int row = vehicleTable.getSelectedRow();
        if (row == -1) return;

        selectedVehicleID = (int) tableModel.getValueAt(row, 0);
        brandField.setText((String) tableModel.getValueAt(row, 1));
        modelField.setText((String) tableModel.getValueAt(row, 2));
        typeBox.setSelectedItem(tableModel.getValueAt(row, 3));
        plateField.setText((String) tableModel.getValueAt(row, 4));
        rateField.setText(String.valueOf(tableModel.getValueAt(row, 5)));

        String statusStr = (String) tableModel.getValueAt(row, 6);
        statusBox.setSelectedItem(VehicleStatus.fromDbValue(statusStr));
    }

    // -------------------------------------------------------
    // ADD VEHICLE
    // -------------------------------------------------------
    private void addVehicle() {
        if (!validateFields()) return;

        String plate = plateField.getText().trim();

        try {
            if (vehicleDAO.plateExists(plate, -1)) {
                JOptionPane.showMessageDialog(this,
                    "Plate number already exists.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Vehicle v = VehicleFactory.createVehicle(
                (String) typeBox.getSelectedItem(),
                0,
                brandField.getText().trim(),
                modelField.getText().trim(),
                plate,
                Double.parseDouble(rateField.getText().trim()),
                (VehicleStatus) statusBox.getSelectedItem()
            );

            boolean success = vehicleDAO.addVehicle(v);

            if (success) {
                JOptionPane.showMessageDialog(this, "Vehicle added successfully!");
                clearForm();
                loadVehicles();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to add vehicle.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (RentalSystemException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------
    // UPDATE VEHICLE
    // -------------------------------------------------------
    private void updateVehicle() {
        if (selectedVehicleID == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a vehicle from the table first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateFields()) return;

        String plate = plateField.getText().trim();

        try {
            if (vehicleDAO.plateExists(plate, selectedVehicleID)) {
                JOptionPane.showMessageDialog(this,
                    "Plate number already used by another vehicle.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Vehicle v = VehicleFactory.createVehicle(
                (String) typeBox.getSelectedItem(),
                selectedVehicleID,
                brandField.getText().trim(),
                modelField.getText().trim(),
                plate,
                Double.parseDouble(rateField.getText().trim()),
                (VehicleStatus) statusBox.getSelectedItem()
            );

            boolean success = vehicleDAO.updateVehicle(v);

            if (success) {
                JOptionPane.showMessageDialog(this, "Vehicle updated successfully!");
                clearForm();
                loadVehicles();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update vehicle.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (RentalSystemException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------
    // VALIDATION
    // -------------------------------------------------------
    private boolean validateFields() {
        String brand  = brandField.getText().trim();
        String model  = modelField.getText().trim();
        String plate  = plateField.getText().trim();
        String rate   = rateField.getText().trim();

        if (brand.isEmpty() || model.isEmpty() || plate.isEmpty() || rate.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "All fields are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (plate.length() > 7) {
            JOptionPane.showMessageDialog(this,
                "Plate number must be 7 characters or fewer.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            double r = Double.parseDouble(rate);
            if (r <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Daily rate must be a positive number.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    // -------------------------------------------------------
    // CLEAR FORM
    // -------------------------------------------------------
    private void clearForm() {
        selectedVehicleID = -1;
        brandField.setText("");
        modelField.setText("");
        plateField.setText("");
        rateField.setText("");
        typeBox.setSelectedIndex(0);
        statusBox.setSelectedIndex(0);
        vehicleTable.clearSelection();
    }
}
