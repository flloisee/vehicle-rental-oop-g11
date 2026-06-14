package com.vehicle.rental.g11.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class CustomerFrame extends JFrame {

    private JTable customerTable;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField firstNameField, middleNameField, lastNameField,
                       suffixField, emailField;
    private JPasswordField passwordField;
    private JButton addButton, updateButton, clearButton;

    private String selectedCustomerID = null;

    public CustomerFrame() {
        setTitle("Customer Management");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        loadCustomers();
        setVisible(true);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 4, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Customer Details"));

        panel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        panel.add(firstNameField);

        panel.add(new JLabel("Middle Name:"));
        middleNameField = new JTextField();
        panel.add(middleNameField);

        panel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        panel.add(lastNameField);

        panel.add(new JLabel("Suffix:"));
        suffixField = new JTextField();
        panel.add(suffixField);

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        // Fill remaining grid cells
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));

        return panel;
    }

    private JScrollPane buildTablePanel() {
        String[] columns = {"Customer ID", "First Name", "Middle Name",
                            "Last Name", "Suffix", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRowIntoForm();
            }
        });

        return new JScrollPane(customerTable);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        addButton    = new JButton("Add Customer");
        updateButton = new JButton("Update Customer");
        clearButton  = new JButton("Clear Form");

        // -------------------------------------------------------
        // TODO (teammate): wire these to CustomerDAO methods
        // addButton    → CustomerDAO.addCustomer()
        // updateButton → CustomerDAO.updateCustomer()
        // -------------------------------------------------------
        addButton.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Add Customer — to be implemented.",
                "TODO", JOptionPane.INFORMATION_MESSAGE));

        updateButton.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Update Customer — to be implemented.",
                "TODO", JOptionPane.INFORMATION_MESSAGE));

        clearButton.addActionListener(e -> clearForm());

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(clearButton);

        return panel;
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        // -------------------------------------------------------
        // TODO (teammate): Replace with CustomerDAO.getAllCustomers()
        // and populate tableModel rows
        // -------------------------------------------------------
    }

    private void loadSelectedRowIntoForm() {
        int row = customerTable.getSelectedRow();
        if (row == -1) return;

        selectedCustomerID = (String) tableModel.getValueAt(row, 0);
        firstNameField.setText((String) tableModel.getValueAt(row, 1));
        middleNameField.setText((String) tableModel.getValueAt(row, 2));
        lastNameField.setText((String) tableModel.getValueAt(row, 3));
        suffixField.setText((String) tableModel.getValueAt(row, 4));
        emailField.setText((String) tableModel.getValueAt(row, 5));
        passwordField.setText(""); // never pre-fill password
    }

    private void clearForm() {
        selectedCustomerID = null;
        firstNameField.setText("");
        middleNameField.setText("");
        lastNameField.setText("");
        suffixField.setText("");
        emailField.setText("");
        passwordField.setText("");
        customerTable.clearSelection();
    }
}