package com.vehicle.rental.g11.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.util.List;

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

import com.vehicle.rental.g11.dao.CustomerDAO;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Customer;
import com.vehicle.rental.g11.service.SearchHandler;

public class CustomerFrame extends JFrame {

    private JTable customerTable;
    private DefaultTableModel tableModel;

    // Search fields
    private JTextField searchField;
    private SearchHandler searchHandler;

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

        setupSearchHandler();
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
                loadCustomers();
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
            CustomerDAO dao = new CustomerDAO();
            List<Customer> results = dao.searchCustomers(query);
            for (Customer c : results) {
                tableModel.addRow(new Object[]{
                    c.getCustomerID(),
                    c.getFirstName(),
                    c.getMiddleName(),
                    c.getLastName(),
                    c.getSuffix(),
                    c.getEmail()
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

        panel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        return panel;
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
        addButton.addActionListener(e -> {
            String fName = firstNameField.getText();
            String mName = middleNameField.getText();
            String lName = lastNameField.getText();
            String suffix = suffixField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (fName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First Name, Email, and Password are required.",
                                              "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                com.vehicle.rental.g11.dao.CustomerDAO dao = new com.vehicle.rental.g11.dao.CustomerDAO();
                com.vehicle.rental.g11.model.Customer customer = new com.vehicle.rental.g11.model.Customer(
                    null, fName, mName, lName, suffix, email
                );
                if (dao.addCustomer(customer, password)) {
                    JOptionPane.showMessageDialog(this, "Customer added successfully!");
                    clearForm();
                    loadCustomers();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add customer.");
                }
            } catch (com.vehicle.rental.g11.exception.RentalSystemException ex) {
                JOptionPane.showMessageDialog(this, "Error adding customer: " + ex.getMessage(),
                                              "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            if (selectedCustomerID == null) {
                JOptionPane.showMessageDialog(this, "Please select a customer to update.",
                                              "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                com.vehicle.rental.g11.dao.CustomerDAO dao = new com.vehicle.rental.g11.dao.CustomerDAO();
                com.vehicle.rental.g11.model.Customer customer = new com.vehicle.rental.g11.model.Customer(
                    selectedCustomerID,
                    firstNameField.getText(),
                    middleNameField.getText(),
                    lastNameField.getText(),
                    suffixField.getText(),
                    emailField.getText()
                );
                if (dao.updateCustomer(customer)) {
                    JOptionPane.showMessageDialog(this, "Customer updated successfully!");
                    loadCustomers();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update customer.");
                }
            } catch (com.vehicle.rental.g11.exception.RentalSystemException ex) {
                JOptionPane.showMessageDialog(this, "Error updating customer: " + ex.getMessage(),
                                              "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearForm());

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(clearButton);

        return panel;
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        try {
            com.vehicle.rental.g11.dao.CustomerDAO dao = new com.vehicle.rental.g11.dao.CustomerDAO();
            java.util.List<com.vehicle.rental.g11.model.Customer> customers = dao.getAllCustomers();
            for (com.vehicle.rental.g11.model.Customer c : customers) {
                tableModel.addRow(new Object[]{
                    c.getCustomerID(),
                    c.getFirstName(),
                    c.getMiddleName(),
                    c.getLastName(),
                    c.getSuffix(),
                    c.getEmail()
                });
            }
        } catch (com.vehicle.rental.g11.exception.RentalSystemException e) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
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