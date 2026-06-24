package com.vehicle.rental.g11.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import com.vehicle.rental.g11.dao.EmployeeDAO;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Employee;

public class LoginFrame extends JFrame {

    // Colours are provided by UITheme

    // Login fields
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JButton        loginButton;

    // Register fields
    private JTextField     regFirstName, regMiddleName, regLastName, regSuffix, regEmail;
    private JPasswordField regPassword;
    private JButton        registerButton;

    // Tab state

    private JButton tabLogin, tabRegister;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public LoginFrame() {
        setTitle("Vehicle Rental System — Access");
        setSize(440, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG);

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
        // Set default button so ENTER triggers login when login tab is active
        getRootPane().setDefaultButton(loginButton);

        setVisible(true);
    }

    // ── HEADER ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Icon circle
        JLabel icon = new JLabel("VRS", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icon.setForeground(Color.WHITE);
        icon.setFont(new Font("Arial", Font.BOLD, 14));
        icon.setPreferredSize(new Dimension(54, 54));
        icon.setMaximumSize(new Dimension(54, 54));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Vehicle Rental System", SwingConstants.CENTER);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("G11  —  Please log in to continue", SwingConstants.CENTER);
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(icon);
        panel.add(Box.createVerticalStrut(12));
        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(sub);

        return panel;
    }

    // ── CENTER (tabs + cards) ─────────────────────────────────────────────────

    private JPanel buildCenter() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UITheme.BG);
        outer.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        outer.add(buildTabBar(),   BorderLayout.NORTH);
        outer.add(buildCardPanel(), BorderLayout.CENTER);

        return outer;
    }

    private JPanel buildTabBar() {
        JPanel bar = new JPanel(new GridLayout(1, 2, 0, 0));
        bar.setBackground(UITheme.BG_CARD);
        bar.setBorder(new MatteBorder(1, 1, 0, 1, UITheme.FIELD_BORDER));

        tabLogin    = makeTabButton("Login",    true);
        tabRegister = makeTabButton("Register", false);

        tabLogin.addActionListener(e -> switchTab(true));
        tabRegister.addActionListener(e -> switchTab(false));

        bar.add(tabLogin);
        bar.add(tabRegister);
        return bar;
    }

    private JButton makeTabButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        applyTabStyle(btn, active);
        // Hover effect for inactive tabs
        final boolean isActive = active;
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!isActive) btn.setBackground(UITheme.ACCENT_HOVER);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (!isActive) btn.setBackground(UITheme.BG_CARD);
            }
        });
        return btn;
    }

    private void applyTabStyle(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(UITheme.ACCENT);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(UITheme.BG_CARD);
            btn.setForeground(UITheme.TEXT_MUTED);
        }
    }

    private void switchTab(boolean showLogin) {
        // Update tab styles
        applyTabStyle(tabLogin,    showLogin);
        applyTabStyle(tabRegister, !showLogin);
        // Show the appropriate card
        cardLayout.show(cardPanel, showLogin ? "login" : "register");
        // Set default button for Enter key activation
        if (showLogin) {
            getRootPane().setDefaultButton(loginButton);
        } else {
            getRootPane().setDefaultButton(registerButton);
        }
    }
    // NOTE: The original implementation was modified to include default button handling.
    // The original call to applyTabStyle and cardLayout.show was moved inside this method.
    // This ensures the Enter key triggers the correct action regardless of focus.

    private JPanel buildCardPanel() {
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(UITheme.BG_CARD);
        cardPanel.setBorder(new MatteBorder(0, 1, 1, 1, UITheme.FIELD_BORDER));

        cardPanel.add(buildLoginCard(),    "login");
        cardPanel.add(buildRegisterCard(), "register");

        return cardPanel;
    }

    // ── LOGIN CARD ────────────────────────────────────────────────────────────

    private JPanel buildLoginCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridwidth = 1;

        // Email label
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(makeLabel("Email"), gbc);

        // Email field
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 14, 0);
        emailField = makeTextField();
        panel.add(emailField, gbc);

        // Password label
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(makeLabel("Password"), gbc);

        // Password field
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 22, 0);
        passwordField = makePasswordField();
        panel.add(passwordField, gbc);

        // Login button
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        loginButton = makeAccentButton("Login");
        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());

        return panel;
    }

    // ── REGISTER CARD ─────────────────────────────────────────────────────────

    private JPanel buildRegisterCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;

        row = addField(panel, gbc, row, "First Name *");
        regFirstName = makeTextField();
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(regFirstName, gbc);

        row = addField(panel, gbc, row, "Middle Name");
        regMiddleName = makeTextField();
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(regMiddleName, gbc);

        row = addField(panel, gbc, row, "Last Name *");
        regLastName = makeTextField();
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(regLastName, gbc);

        row = addField(panel, gbc, row, "Suffix");
        regSuffix = makeTextField();
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(regSuffix, gbc);

        row = addField(panel, gbc, row, "Email *");
        regEmail = makeTextField();
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(regEmail, gbc);

        row = addField(panel, gbc, row, "Password *");
        regPassword = makePasswordField();
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(regPassword, gbc);

        registerButton = makeAccentButton("Create Account");
        gbc.gridy = row; gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(registerButton, gbc);

        registerButton.addActionListener(e -> handleRegistration());
        regPassword.addActionListener(e -> handleRegistration());

        return panel;
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int row, String labelText) {
        gbc.gridy = row;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(makeLabel(labelText), gbc);
        return row + 1;
    }

    // ── FOOTER ────────────────────────────────────────────────────────────────

    private JPanel buildFooter() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 12, 0));
        JLabel label = new JLabel("Vehicle Rental System © G11");
        label.setForeground(UITheme.TEXT_MUTED);
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        panel.add(label);
        return panel;
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(UITheme.TEXT_MUTED);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        return lbl;
    }

    private JTextField makeTextField() {
        JTextField f = new JTextField();
        UITheme.styleField(f);
        return f;
    }

    private JPasswordField makePasswordField() {
        JPasswordField f = new JPasswordField();
        UITheme.styleField(f);
        return f;
    }

    // Field styling delegated to UITheme.styleField

    private JButton makeAccentButton(String text) {
        JButton btn = UITheme.roundedButton(text);
        // Adjust height to match original accent button size
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 40));
        return btn;
    }

    // ── HANDLERS (unchanged logic) ────────────────────────────────────────────

    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Email and password are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            Employee employee       = employeeDAO.getEmployeeByEmail(email);
            String storedHash       = employeeDAO.getPasswordHashByEmail(email);

            if (employee != null && storedHash != null &&
                com.vehicle.rental.g11.service.PasswordUtil.verifyPassword(storedHash, password)) {
                JOptionPane.showMessageDialog(this,
                    "Login successful! Welcome, " + employee.getFirstName(),
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

    private void handleRegistration() {
        String fName    = regFirstName.getText().trim();
        String mName    = regMiddleName.getText().trim();
        String lName    = regLastName.getText().trim();
        String suffix   = regSuffix.getText().trim();
        String email    = regEmail.getText().trim();
        String password = new String(regPassword.getPassword());

        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "First Name, Last Name, Email, and Password are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Employee employee       = new Employee(null, fName, mName, lName, suffix, email, null);
            EmployeeDAO employeeDAO = new EmployeeDAO();
            if (employeeDAO.addEmployee(employee, password)) {
                JOptionPane.showMessageDialog(this,
                    "Registration successful! You can now log in.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                regFirstName.setText(""); regMiddleName.setText("");
                regLastName.setText("");  regSuffix.setText("");
                regEmail.setText("");     regPassword.setText("");
                switchTab(true);
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
}