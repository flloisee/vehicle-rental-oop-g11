package com.vehicle.rental.g11.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.vehicle.rental.g11.gui.UITheme;

public class MainFrame extends JFrame {

    // Colors moved to UITheme

    public MainFrame() {
        setTitle("Vehicle Rental System - Dashboard");
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

        JLabel title = new JLabel("Vehicle Rental System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel sub = new JLabel("G11  —  Dashboard");
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
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 20));
        panel.setBackground(UITheme.PURPLE_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JPanel row1 = new JPanel(new GridLayout(1, 3, 20, 0));
        row1.setOpaque(false);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        row2.setOpaque(false);

        JButton vehicleBtn  = UITheme.roundedButton("Vehicles");
        JButton customerBtn = UITheme.roundedButton("Customers");
        JButton rentalBtn   = UITheme.roundedButton("Rentals");
        JButton reportBtn   = UITheme.roundedButton("Reports");
        JButton logoutBtn   = makeLogoutButton("Logout");

        // Apply uniform navigation button size
        vehicleBtn.setPreferredSize(UITheme.NAV_BTN_SIZE);
        customerBtn.setPreferredSize(UITheme.NAV_BTN_SIZE);
        rentalBtn.setPreferredSize(UITheme.NAV_BTN_SIZE);
        reportBtn.setPreferredSize(UITheme.NAV_BTN_SIZE);
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

        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame(); });

        row1.add(vehicleBtn);
        row1.add(customerBtn);
        row1.add(rentalBtn);
        row2.add(reportBtn);
        row2.add(logoutBtn);

        panel.add(row1);
        panel.add(row2);
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
        JLabel label = new JLabel("Vehicle Rental System © G11");
        label.setForeground(UITheme.TEXT_MUTED);
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        panel.add(label);
        return panel;
    }
}