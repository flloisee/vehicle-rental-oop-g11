package com.vehicle.rental.g11.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dimension;
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
import javax.swing.JPasswordField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.vehicle.rental.g11.dao.EmployeeDAO;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Employee;
import com.vehicle.rental.g11.service.SearchHandler;

/**
 * UI frame for managing employees (CRUD) in the system.
 */
public class EmployeeFrame extends JFrame {
    private JTable employeeTable;
    private DefaultTableModel tableModel;

    // Search field
    private JTextField searchField;
    private SearchHandler searchHandler;

    // Form fields
    private JTextField firstNameField, middleNameField, lastNameField, suffixField, emailField;
    private JPasswordField passwordField;

    private JButton addButton, updateButton, clearButton, deleteButton;

    private String selectedEmployeeID = null;
    private MainFrame mainFrame;

    public EmployeeFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setTitle("Employee Management");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UITheme.BG);
        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                EmployeeFrame.this.mainFrame.setVisible(true);
            }
        });

        setupSearchHandler();
        loadEmployees();
        setVisible(true);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createTitledBorder("Employee Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;

        // Row 0 – First & Middle Name
        JPanel row0 = new JPanel(new GridBagLayout());
        row0.setBackground(UITheme.BG);
        GridBagConstraints rc = new GridBagConstraints();
        rc.insets = new Insets(0, 0, 0, 8);
        rc.anchor = GridBagConstraints.EAST;
        // First Name
        rc.gridx = 0; rc.weightx = 0.0; rc.fill = GridBagConstraints.NONE;
        JLabel lbl = new JLabel("First Name:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setPreferredSize(new Dimension(90, lbl.getPreferredSize().height));
        row0.add(lbl, rc);
        rc.gridx = 1; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        firstNameField = new JTextField(12);
        UITheme.styleField(firstNameField);
        row0.add(firstNameField, rc);
        // Middle Name
        rc.gridx = 2; rc.weightx = 0.0; rc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Middle Name:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setPreferredSize(new Dimension(90, lbl.getPreferredSize().height));
        row0.add(lbl, rc);
        rc.gridx = 3; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        middleNameField = new JTextField(12);
        UITheme.styleField(middleNameField);
        row0.add(middleNameField, rc);
        panel.add(row0, gbc);

        // Row 1 – Last & Suffix
        gbc.gridy++;
        JPanel row1 = new JPanel(new GridBagLayout());
        row1.setBackground(UITheme.BG);
        // Last Name
        rc.gridx = 0; rc.weightx = 0.0; rc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Last Name:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setPreferredSize(new Dimension(90, lbl.getPreferredSize().height));
        row1.add(lbl, rc);
        rc.gridx = 1; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        lastNameField = new JTextField(12);
        UITheme.styleField(lastNameField);
        row1.add(lastNameField, rc);
        // Suffix
        rc.gridx = 2; rc.weightx = 0.0; rc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Suffix:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setPreferredSize(new Dimension(90, lbl.getPreferredSize().height));
        row1.add(lbl, rc);
        rc.gridx = 3; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        suffixField = new JTextField(12);
        UITheme.styleField(suffixField);
        row1.add(suffixField, rc);
        panel.add(row1, gbc);

        // Row 2 – Email and Password side by side
        gbc.gridy++;
        JPanel row2 = new JPanel(new GridBagLayout());
        row2.setBackground(UITheme.BG);
        // Email label
        rc.gridx = 0; rc.weightx = 0.0; rc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Email:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setPreferredSize(new Dimension(90, lbl.getPreferredSize().height));
        row2.add(lbl, rc);
        // Email field
        rc.gridx = 1; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        emailField = new JTextField(25);
        UITheme.styleField(emailField);
        row2.add(emailField, rc);
        // Password label
        rc.gridx = 2; rc.weightx = 0.0; rc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Password:");
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setPreferredSize(new Dimension(90, lbl.getPreferredSize().height));
        row2.add(lbl, rc);
        // Password field
        rc.gridx = 3; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(25);
        UITheme.styleField(passwordField);
        row2.add(passwordField, rc);
        panel.add(row2, gbc);



        // filler
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
                loadEmployees();
            } else {
                performSearch(query);
            }
        });
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            private void updateSearch() { searchHandler.onQueryChanged(searchField.getText()); }
        });
    }

    private void performSearch(String query) {
        tableModel.setRowCount(0);
        try {
            EmployeeDAO dao = new EmployeeDAO();
            List<Employee> results = dao.searchEmployees(query);
            for (Employee e : results) {
                tableModel.addRow(new Object[]{
                        e.getPersonID(),
                        e.getFirstName(),
                        e.getMiddleName(),
                        e.getLastName(),
                        e.getSuffix(),
                        e.getEmail()
                });
            }
        } catch (RentalSystemException ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(buildSearchPanel(), BorderLayout.NORTH);
        String[] columns = {"Employee ID", "First Name", "Middle Name", "Last Name", "Suffix", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        employeeTable = new JTable(tableModel) {
            private int hoverRow = -1;
            {
                addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(java.awt.event.MouseEvent e) {
                        int row = rowAtPoint(e.getPoint());
                        if (row != hoverRow) { hoverRow = row; repaint(); }
                    }
                });
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) { hoverRow = -1; repaint(); }
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
        UITheme.styleTable(employeeTable);
        employeeTable.setRowHeight(28);
        employeeTable.setIntercellSpacing(new java.awt.Dimension(0, 5));
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { loadSelectedRowIntoForm(); }
        });
        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBackground(UITheme.BG);
        addButton = UITheme.roundedButton("Add Employee");
        updateButton = UITheme.roundedButton("Update Employee");
        deleteButton = UITheme.roundedButton("Delete Employee");
        clearButton = UITheme.roundedButton("Clear Form");
        JButton backButton = UITheme.roundedButton("Back to Main Menu");

        addButton.addActionListener(e -> {
            String fName = firstNameField.getText().trim();
            String lName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First Name, Last Name, Email, and Password are required.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Employee emp = new Employee(null, fName, middleNameField.getText().trim(), lName,
                    suffixField.getText().trim(), email, null);
            try {
                if (new EmployeeDAO().addEmployee(emp, password)) {
                    JOptionPane.showMessageDialog(this, "Employee added successfully!");
                    clearForm();
                    loadEmployees();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add employee.");
                }
            } catch (RentalSystemException ex) {
                JOptionPane.showMessageDialog(this, "Error adding employee: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            if (selectedEmployeeID == null) {
                JOptionPane.showMessageDialog(this, "Select an employee to update.",
                        "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String password = new String(passwordField.getPassword());
            Employee emp = new Employee(selectedEmployeeID,
                    firstNameField.getText().trim(),
                    middleNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    suffixField.getText().trim(),
                    emailField.getText().trim(),
                    null);
            try {
                if (new EmployeeDAO().updateEmployee(emp, password.isEmpty() ? null : password)) {
                    JOptionPane.showMessageDialog(this, "Employee updated successfully!");
                    loadEmployees();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update employee.");
                }
            } catch (RentalSystemException ex) {
                JOptionPane.showMessageDialog(this, "Error updating employee: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            if (selectedEmployeeID == null) {
                JOptionPane.showMessageDialog(this, "Select an employee to delete.",
                        "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to permanently delete this employee?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
            try {
                if (new EmployeeDAO().deleteEmployee(selectedEmployeeID)) {
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                    clearForm();
                    loadEmployees();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete employee.");
                }
            } catch (RentalSystemException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting employee: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearForm());
        backButton.addActionListener(e -> {
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

    private void loadEmployees() {
        tableModel.setRowCount(0);
        try {
            List<Employee> employees = new EmployeeDAO().getAllEmployees();
            for (Employee e : employees) {
                tableModel.addRow(new Object[]{
                        e.getPersonID(),
                        e.getFirstName(),
                        e.getMiddleName(),
                        e.getLastName(),
                        e.getSuffix(),
                        e.getEmail()
                });
            }
        } catch (RentalSystemException ex) {
            JOptionPane.showMessageDialog(this, "Error loading employees: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedRowIntoForm() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) return;
        selectedEmployeeID = (String) tableModel.getValueAt(row, 0);
        firstNameField.setText((String) tableModel.getValueAt(row, 1));
        middleNameField.setText((String) tableModel.getValueAt(row, 2));
        lastNameField.setText((String) tableModel.getValueAt(row, 3));
        suffixField.setText((String) tableModel.getValueAt(row, 4));
        emailField.setText((String) tableModel.getValueAt(row, 5));
        passwordField.setText(""); // clear password field on selection
    }

    private void clearForm() {
        selectedEmployeeID = null;
        firstNameField.setText("");
        middleNameField.setText("");
        lastNameField.setText("");
        suffixField.setText("");
        emailField.setText("");
        passwordField.setText("");
        employeeTable.clearSelection();
    }
}
