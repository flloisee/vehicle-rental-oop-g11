package com.vehicle.rental.g11.gui;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Vehicle Rental System — Login");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 60));
        panel.setPadding(new Insets(16, 0, 16, 0));

        JLabel title = new JLabel("🚗 Vehicle Rental System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title);

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
        loginButton.setForeground(Color.WHITE);
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

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Email and password are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // -------------------------------------------------------
        // TODO (teammate): Replace this stub with real auth logic
        // Steps:
        // 1. Query CustomerDAO.getCustomerByEmail(email)
        // 2. If customer found, use PasswordUtil.verifyPassword(hash, password)
        // 3. If verified, dispose() and open MainFrame
        // 4. Else show "Invalid email or password" error
        // -------------------------------------------------------
        JOptionPane.showMessageDialog(this,
            "Login module not yet implemented.\nOpening dashboard directly.",
            "Dev Mode", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new MainFrame();
    }
}