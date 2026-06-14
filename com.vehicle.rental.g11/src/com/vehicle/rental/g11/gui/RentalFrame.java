package com.vehicle.rental.g11.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

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

public class RentalFrame extends JFrame {

    private JTable rentalTable;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField customerIDField, vehicleIDField,
                       rentalDateField, plannedReturnDateField,
                       returnDateField, totalCostField;
    private JButton addButton, updateButton, returnButton, clearButton;

    private int selectedRentalID = -1;

    public RentalFrame() {
        setTitle("Rental Management");
        setSize(1000, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

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

    private JScrollPane buildTablePanel() {
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

        return new JScrollPane(rentalTable);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        addButton    = new JButton("Add Rental");
        updateButton = new JButton("Update Rental");
        returnButton = new JButton("Mark as Returned");
        clearButton  = new JButton("Clear Form");

        // -------------------------------------------------------
        // TODO (teammate): wire to RentalEngine / RentalDAO
        // addButton    → RentalEngine.startRental()
        // updateButton → RentalDAO.updateRental()
        // returnButton → RentalEngine.returnVehicle()
        // -------------------------------------------------------
        addButton.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Add Rental — to be implemented.",
                "TODO", JOptionPane.INFORMATION_MESSAGE));

        updateButton.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Update Rental — to be implemented.",
                "TODO", JOptionPane.INFORMATION_MESSAGE));

        returnButton.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Mark as Returned — to be implemented.",
                "TODO", JOptionPane.INFORMATION_MESSAGE));

        clearButton.addActionListener(e -> clearForm());

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(returnButton);
        panel.add(clearButton);

        return panel;
    }

    private void loadRentals() {
        tableModel.setRowCount(0);
        // -------------------------------------------------------
        // TODO (teammate): Replace with RentalDAO.getAllRentals()
        // and populate tableModel rows
        // -------------------------------------------------------
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