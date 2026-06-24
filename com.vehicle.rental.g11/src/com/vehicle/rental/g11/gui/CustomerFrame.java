package com.vehicle.rental.g11.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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

    private JButton addButton, updateButton, clearButton, deleteButton;

    private String selectedCustomerID = null;

    private MainFrame mainFrame;
 
    public CustomerFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setTitle("Customer Management");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UITheme.BG);
        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
        // Ensure the dashboard reappears when this frame is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // MainFrame was hidden when this frame opened
                CustomerFrame.this.mainFrame.setVisible(true);
            }
        });
        setupSearchHandler();
        loadCustomers();
        setVisible(true);
    }

private JPanel buildFormPanel() {
        // Outer container – aligns everything to the left side
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;

        // -------------------- Row 0 – First & Middle Name --------------------
        JPanel row0 = new JPanel(new GridBagLayout());
        row0.setBackground(UITheme.BG);
        GridBagConstraints rc = new GridBagConstraints();
        rc.insets = new Insets(0, 0, 0, 8);
        rc.anchor = GridBagConstraints.EAST;
        // First Name label
        rc.gridx = 0; rc.weightx = 0.0; rc.fill = GridBagConstraints.NONE;
        JLabel lbl = new JLabel("First Name:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setPreferredSize(new Dimension(90, lbl.getPreferredSize().height));
        row0.add(lbl, rc);
        // First Name field (expand)
        rc.gridx = 1; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        firstNameField = new JTextField(12);
        UITheme.styleField(firstNameField);
        row0.add(firstNameField, rc);
        // Middle Name label
        rc.gridx = 2; rc.weightx = 0.0; rc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Middle Name:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setPreferredSize(new Dimension(90, lbl.getPreferredSize().height));
        row0.add(lbl, rc);
        // Middle Name field (expand)
        rc.gridx = 3; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        middleNameField = new JTextField(12);
        UITheme.styleField(middleNameField);
        row0.add(middleNameField, rc);
        panel.add(row0, gbc);

        // -------------------- Row 1 – Last & Suffix --------------------
        gbc.gridy++;
        JPanel row1 = new JPanel(new GridBagLayout());
        row1.setBackground(UITheme.BG);
        // Last Name label
        rc.gridx = 0; rc.weightx = 0.0; rc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Last Name:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setPreferredSize(new Dimension(90, lbl.getPreferredSize().height));
        row1.add(lbl, rc);
        // Last Name field (expand)
        rc.gridx = 1; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        lastNameField = new JTextField(12);
        UITheme.styleField(lastNameField);
        row1.add(lastNameField, rc);
        // Suffix label
        rc.gridx = 2; rc.weightx = 0.0; rc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Suffix:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setPreferredSize(new Dimension(90, lbl.getPreferredSize().height));
        row1.add(lbl, rc);
        // Suffix field (expand)
        rc.gridx = 3; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        suffixField = new JTextField(12);
        UITheme.styleField(suffixField);
        row1.add(suffixField, rc);
        panel.add(row1, gbc);

        // -------------------- Row 2 – Email --------------------
        gbc.gridy++;
        JPanel row2 = new JPanel(new GridBagLayout());
        row2.setBackground(UITheme.BG);
        // Email label
        rc.gridx = 0; rc.weightx = 0.0; rc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Email:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setPreferredSize(new Dimension(90, lbl.getPreferredSize().height));
        row2.add(lbl, rc);
        // Email field (expand to fill remaining width)
        rc.gridx = 1; rc.gridwidth = 3; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        emailField = new JTextField(25);
        UITheme.styleField(emailField);
        row2.add(emailField, rc);
        // Reset gridwidth for safety
        rc.gridwidth = 1;
        panel.add(row2, gbc);

        // Add filler to consume remaining horizontal space if needed
        gbc.gridy++;
        gbc.weightx = 1.0;
        panel.add(new JLabel(""), gbc);

        return panel;
    }

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

customerTable = new JTable(tableModel) {
            private int hoverRow = -1;
            {
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
                JComponent c = (JComponent) super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    c.setBackground(UITheme.PURPLE);
                    c.setForeground(java.awt.Color.WHITE);
                    c.setBorder(javax.swing.BorderFactory.createLineBorder(UITheme.PURPLE, 1));
                } else if (row == hoverRow) {
                    c.setBackground(UITheme.PURPLE_LIGHT);
                    c.setForeground(UITheme.TEXT_PRIMARY);
                    c.setBorder(null);
                } else {
                    c.setBackground(UITheme.BG);
                    c.setForeground(UITheme.TEXT_PRIMARY);
                    c.setBorder(null);
                }
                return c;
            }
        };
        UITheme.styleTable(customerTable);
        customerTable.setRowHeight(28);
        customerTable.setIntercellSpacing(new java.awt.Dimension(0, 5));
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
        panel.setBackground(UITheme.BG);

        addButton    = UITheme.roundedButton("Add Customer");
        updateButton = UITheme.roundedButton("Update Customer");
        clearButton  = UITheme.roundedButton("Clear Form");
        deleteButton = UITheme.roundedButton("Delete Customer");
        JButton backButton = UITheme.roundedButton("Back to Main Menu");
 
        // -------------------------------------------------------
        // TODO (teammate): wire these to CustomerDAO methods
        // addButton    → CustomerDAO.addCustomer()
        // updateButton → CustomerDAO.updateCustomer()
        // deleteButton → CustomerDAO.deleteCustomer()
        // -------------------------------------------------------
        addButton.addActionListener(e -> {
            String fName = firstNameField.getText();
            String mName = middleNameField.getText();
            String lName = lastNameField.getText();
            String suffix = suffixField.getText();
            String email = emailField.getText();
            if (fName.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First Name and Email are required.",
                                              "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                com.vehicle.rental.g11.dao.CustomerDAO dao = new com.vehicle.rental.g11.dao.CustomerDAO();
                com.vehicle.rental.g11.model.Customer customer = new com.vehicle.rental.g11.model.Customer(
                    null, fName, mName, lName, suffix, email
                );
                if (dao.addCustomer(customer, null)) {
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

        deleteButton.addActionListener(e -> {
            if (selectedCustomerID == null) {
                JOptionPane.showMessageDialog(this, "Please select a customer to permanently delete.",
                                              "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to PERMANENTLY DELETE this customer?\nThis action cannot be undone!",
                "Confirm Permanent Delete", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                com.vehicle.rental.g11.dao.CustomerDAO dao = new com.vehicle.rental.g11.dao.CustomerDAO();
                if (dao.deleteCustomer(selectedCustomerID)) {
                    JOptionPane.showMessageDialog(this, "Customer permanently deleted successfully!",
                                                  "Delete Success", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadCustomers();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete customer. (Customer not found)",
                                                  "Delete Failed", JOptionPane.WARNING_MESSAGE);
                }
            } catch (com.vehicle.rental.g11.exception.RentalSystemException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting customer: " + ex.getMessage(),
                                              "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearForm());
        backButton.addActionListener(e -> {
            // Show the dashboard before disposing to avoid a moment with no visible windows
            mainFrame.setVisible(true);
            dispose();
        });
 
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(clearButton);
        panel.add(backButton);

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

    }

    private void clearForm() {
        selectedCustomerID = null;
        firstNameField.setText("");
        middleNameField.setText("");
        lastNameField.setText("");
        suffixField.setText("");
        emailField.setText("");
        customerTable.clearSelection();
    }
}