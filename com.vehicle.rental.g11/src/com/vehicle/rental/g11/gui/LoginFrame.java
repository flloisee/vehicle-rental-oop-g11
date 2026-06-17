package com.vehicle.rental.g11.gui;

import javax.swing.*;
import java.awt.*;
import com.vehicle.rental.g11.dao.CustomerDAO;
import com.vehicle.rental.g11.model.Customer;
import com.vehicle.rental.g11.exception.RentalSystemException;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private JTextField regFirstName;
    private JTextField regMiddleName;
    private JTextField regLastName;
    private JTextField regSuffix;
    private JTextField regEmail;
    private JPasswordField regPassword;
    private JButton registerButton;

    public LoginFrame() {
        setTitle("Vehicle Rental System — Access");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabbedPanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel buildTabbedPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Login", buildFormPanel());
        tabbedPane.addTab("Register", buildRegistrationPanel());
        
        // Setting foreground for tabs to ensure visibility
        tabbedPane.setForeground(Color.BLACK);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        wrapper.add(tabbedPane);
        return wrapper;
    }

    private JPanel buildHeader() {
        JLabel title = new JLabel("🚗 Vehicle Rental System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel sub = new JLabel("G11 — Please log in to continue");
        sub.setForeground(new Color(180, 180, 220));
        sub.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel wrapper = new JPanel(new GridLayout(2, 1));
        wrapper.setBackground(new Color(30, 30, 60));
        wrapper.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        wrapper.add(title);
        wrapper.add(sub);

        JPanel outer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        outer.setBackground(new Color(30, 30, 60));
        outer.add(wrapper);
        return outer;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Email
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        emailField = new JTextField();
        panel.add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        passwordField = new JPasswordField();
        panel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 6, 6, 6);
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(30, 30, 60));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(loginButton, gbc);

        // Login action — teammate fills in Argon2 verify logic here
        loginButton.addActionListener(e -> handleLogin());

        // Allow Enter key to submit
        passwordField.addActionListener(e -> handleLogin());

        return panel;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Vehicle Rental System © G11");
        label.setForeground(Color.GRAY);
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        panel.add(label);
        return panel;
    }

    private JPanel buildRegistrationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 6, 5, 6);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        regFirstName = new JTextField();
        panel.add(regFirstName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panel.add(new JLabel("Middle Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        regMiddleName = new JTextField();
        panel.add(regMiddleName, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        regLastName = new JTextField();
        panel.add(regLastName, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        panel.add(new JLabel("Suffix:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        regSuffix = new JTextField();
        panel.add(regSuffix, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        regEmail = new JTextField();
        panel.add(regEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        regPassword = new JPasswordField();
        panel.add(regPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 6, 6, 6);
        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(30, 30, 60));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(registerButton, gbc);

        registerButton.addActionListener(e -> handleRegistration());
        regPassword.addActionListener(e -> handleRegistration());

        return panel;
    }

    private void handleRegistration() {
        String fName = regFirstName.getText().trim();
        String mName = regMiddleName.getText().trim();
        String lName = regLastName.getText().trim();
        String suffix = regSuffix.getText().trim();
        String email = regEmail.getText().trim();
        String password = new String(regPassword.getPassword());

        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "First Name, Last Name, Email, and Password are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Customer customer = new Customer(null, fName, mName, lName, suffix, email);
            CustomerDAO customerDAO = new CustomerDAO();
            if (customerDAO.addCustomer(customer, password)) {
                JOptionPane.showMessageDialog(this,
                    "Registration successful! You can now log in.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear fields
                regFirstName.setText("");
                regMiddleName.setText("");
                regLastName.setText("");
                regSuffix.setText("");
                regEmail.setText("");
                regPassword.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Registration failed. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RentalSystemException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Email and password are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = customerDAO.getCustomerByEmail(email);
            String storedHash = customerDAO.getPasswordByEmail(email);

            if (customer != null && storedHash != null && com.vehicle.rental.g11.service.PasswordUtil.verifyPassword(storedHash, password)) {
                JOptionPane.showMessageDialog(this,
                    "Login successful! Welcome, " + customer.getFirstName(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new MainFrame();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid email or password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RentalSystemException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
