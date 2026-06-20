package com.vehicle.rental.g11.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    private static final Color BG           = Color.WHITE;
    private static final Color PURPLE       = new Color(120, 60, 200);
    private static final Color PURPLE_HOVER = new Color(100, 40, 180);
    private static final Color PURPLE_LIGHT = new Color(245, 240, 255);

    public MainFrame() {
        setTitle("Vehicle Rental System - Dashboard");
        setSize(820, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildNavPanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PURPLE);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 30, 18, 30));

        JLabel title = new JLabel("Vehicle Rental System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel sub = new JLabel("G11  —  Dashboard");
        sub.setForeground(new Color(210, 190, 255));
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
        panel.setBackground(PURPLE_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JPanel row1 = new JPanel(new GridLayout(1, 3, 20, 0));
        row1.setOpaque(false);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        row2.setOpaque(false);

        JButton vehicleBtn  = makeNavButton("Vehicles");
        JButton customerBtn = makeNavButton("Customers");
        JButton rentalBtn   = makeNavButton("Rentals");
        JButton reportBtn   = makeNavButton("Reports");
        JButton logoutBtn   = makeLogoutButton("Logout");

        Dimension btnSize = new Dimension(220, 164);
        reportBtn.setPreferredSize(btnSize);
        logoutBtn.setPreferredSize(btnSize);

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

    private JButton makeNavButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? PURPLE_HOVER : PURPLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeLogoutButton(String text) {
        Color red      = new Color(210, 50, 50);
        Color redHover = new Color(180, 30, 30);

        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? redHover : red);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showComingSoon(String name) {
        JOptionPane.showMessageDialog(this,
            name + " module not yet available.",
            "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel();
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel label = new JLabel("Vehicle Rental System © G11");
        label.setForeground(new Color(180, 150, 210));
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        panel.add(label);
        return panel;
    }
}