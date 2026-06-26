package com.vehicle.rental.g11.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

    // Colors moved to UITheme

    public MainFrame() {
        setTitle("CARLS — Dashboard");
        setSize(820, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildNavPanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.PURPLE);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 30, 18, 30));

        JLabel title = new JLabel("CARLS");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel sub = new JLabel("Centralized Automobile Rental & Leasing System  ·  G11");
        sub.setForeground(UITheme.HEADER_SUBTEXT);
        sub.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        text.add(title);
        text.add(sub);
        panel.add(text, BorderLayout.WEST);
        return panel;
    }

    private JPanel buildNavPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 20, 20));
        panel.setBackground(UITheme.PURPLE_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JButton vehicleBtn  = UITheme.roundedButton("Vehicles");
        JButton customerBtn = UITheme.roundedButton("Customers");
        JButton rentalBtn   = UITheme.roundedButton("Rentals");
        JButton reportBtn   = UITheme.roundedButton("Reports");
        JButton employeeBtn = UITheme.roundedButton("Employees");
        JButton logoutBtn   = makeLogoutButton("Logout");

        // Apply uniform navigation button size
        vehicleBtn.setPreferredSize(UITheme.NAV_BTN_SIZE);
        customerBtn.setPreferredSize(UITheme.NAV_BTN_SIZE);
        rentalBtn.setPreferredSize(UITheme.NAV_BTN_SIZE);
        reportBtn.setPreferredSize(UITheme.NAV_BTN_SIZE);
        employeeBtn.setPreferredSize(UITheme.NAV_BTN_SIZE);
        logoutBtn.setPreferredSize(UITheme.NAV_BTN_SIZE);

        vehicleBtn.addActionListener(e -> {
            setVisible(false);
            new VehicleFrame(this);
        });

        customerBtn.addActionListener(e -> {
            try { setVisible(false); new CustomerFrame(this); }
            catch (Exception ex) { setVisible(true); showComingSoon("Customer"); }
        });

        rentalBtn.addActionListener(e -> {
            try { setVisible(false); new RentalFrame(this); }
            catch (Exception ex) { setVisible(true); showComingSoon("Rental"); }
        });

        reportBtn.addActionListener(e -> {
            try { setVisible(false); new ReportFrame(this); }
            catch (Exception ex) { setVisible(true); showComingSoon("Report"); }
        });

        // Employees management button
        employeeBtn.addActionListener(e -> {
            setVisible(false);
            new EmployeeFrame(this);
        });

        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame(); });

        panel.add(vehicleBtn);
        panel.add(rentalBtn);
        panel.add(reportBtn);
        panel.add(customerBtn);
        panel.add(employeeBtn);
        panel.add(logoutBtn);


        return panel;
    }

    // Rounded button creation delegated to UITheme

    private JButton makeLogoutButton(String text) {
        return UITheme.roundedButton(text, UITheme.LOGOUT, UITheme.LOGOUT_HOVER);
    }

    private void showComingSoon(String name) {
        JOptionPane.showMessageDialog(this,
            name + " module not yet available.",
            "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel label = new JLabel("CARLS © G11");
        label.setForeground(UITheme.TEXT_MUTED);
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        panel.add(label);
        return panel;
    }
}