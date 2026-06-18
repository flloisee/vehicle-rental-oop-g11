package com.vehicle.rental.g11.gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Vehicle Rental System - Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildNavPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    // -------------------------------------------------------
    // HEADER
    // -------------------------------------------------------
    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 60));
        JLabel title = new JLabel("Vehicle Rental System - G11");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(title);
        return panel;
    }

    // -------------------------------------------------------
    // NAV BUTTONS - each opens its respective Frame
    // -------------------------------------------------------
    private JPanel buildNavPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JPanel row1 = new JPanel(new GridLayout(1, 3, 20, 0));
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton vehicleBtn  = makeNavButton("🚗 Vehicles");
        JButton customerBtn = makeNavButton("👤 Customers");
        JButton rentalBtn   = makeNavButton("📋 Rentals");
        JButton reportBtn   = makeNavButton("📊 Reports");
        JButton logoutBtn   = makeNavButton("🔒 Logout");

        // Set preferred size for row 2 buttons to match row 1 style
        Dimension btnSize = new Dimension(213, 200);
        reportBtn.setPreferredSize(btnSize);
        logoutBtn.setPreferredSize(btnSize);

        // Vehicle button - your module
        vehicleBtn.addActionListener(e -> new VehicleFrame());

        // Stubs for teammates' modules - open when they build them
        customerBtn.addActionListener(e -> {
            try {
                new CustomerFrame();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Customer module not yet available.",
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        rentalBtn.addActionListener(e -> {
            try {
                new RentalFrame();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Rental module not yet available.",
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        reportBtn.addActionListener(e -> {
            try {
                new ReportFrame();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Report module not yet available.",
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Logout - goes back to LoginFrame
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        row1.add(vehicleBtn);
        row1.add(customerBtn);
        row1.add(rentalBtn);

        row2.add(reportBtn);
        row2.add(logoutBtn);

        panel.add(row1);
        panel.add(row2);

        return panel;
    }

    private JButton makeNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        return btn;
    }
}