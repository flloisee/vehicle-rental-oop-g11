package com.vehicle.rental.g11.gui;

import com.vehicle.rental.g11.service.RentalEngine;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Rentals;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportFrame extends JFrame {

    private JLabel totalRevenueLabel, activeRentalsLabel, overdueLabel;
    private JTable overdueTable;
    private DefaultTableModel overdueTableModel;
    private RentalEngine rentalEngine;

    public ReportFrame() {
        rentalEngine = new RentalEngine();

        setTitle("Reports & Statistics");
        setSize(800, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildStatsPanel(), BorderLayout.NORTH);
        add(buildOverduePanel(), BorderLayout.CENTER);
        add(buildRefreshPanel(), BorderLayout.SOUTH);

        loadReports();
        setVisible(true);
    }

    // -------------------------------------------------------
    // STATS PANEL — summary numbers at the top
    // -------------------------------------------------------
    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 16, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        totalRevenueLabel = makeValueLabel("Loading...", new Color(34, 139, 34));
        activeRentalsLabel = makeValueLabel("Loading...", new Color(30, 30, 180));
        overdueLabel = makeValueLabel("Loading...", new Color(180, 30, 30));

        panel.add(makeStatCard("Total Revenue", totalRevenueLabel, new Color(34, 139, 34)));
        panel.add(makeStatCard("Active Rentals", activeRentalsLabel, new Color(30, 30, 180)));
        panel.add(makeStatCard("Overdue", overdueLabel, new Color(180, 30, 30)));

        return panel;
    }

    private JPanel makeStatCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent, 2),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);

        card.add(titleLabel);
        card.add(valueLabel);

        return card;
    }

    private JLabel makeValueLabel(String value, Color accent) {
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 22));
        valueLabel.setForeground(accent);
        return valueLabel;
    }

    // -------------------------------------------------------
    // OVERDUE TABLE — rentals past their planned return date
    // -------------------------------------------------------
    private JPanel buildOverduePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Overdue Rentals"));

        String[] columns = {"Rental ID", "Customer ID", "Customer Name", "Vehicle ID", "Vehicle Brand", "Vehicle Model",
                                   "Planned Return", "Days Overdue"};
        overdueTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        overdueTable = new JTable(overdueTableModel);
        overdueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(overdueTable), BorderLayout.CENTER);

        return panel;
    }

    // -------------------------------------------------------
    // REFRESH BUTTON
    // -------------------------------------------------------
    private JPanel buildRefreshPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshBtn = new JButton("Refresh Reports");
        refreshBtn.addActionListener(e -> loadReports());
        
        JButton backBtn = new JButton("Back to Main Menu");
        backBtn.addActionListener(e -> {
            dispose();
            new MainFrame();
        });
        
        panel.add(refreshBtn);
        panel.add(backBtn);
        return panel;
    }

    // -------------------------------------------------------
    // LOAD DATA FROM RentalEngine
    // -------------------------------------------------------
    private void loadReports() {
        try {
            // Revenue
            double revenue = rentalEngine.getTotalRevenue();
            totalRevenueLabel.setText(String.format("\u20B1%.2f", revenue));

            // Active rentals
            int active = rentalEngine.getActiveRentalCount();
            activeRentalsLabel.setText(String.valueOf(active));

            // Overdue
            List<Rentals> overdue = rentalEngine.getOverdueRentals();
            overdueLabel.setText(String.valueOf(overdue.size()));

            // Populate overdue table
            overdueTableModel.setRowCount(0);
            for (Rentals r : overdue) {
                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(
                    r.getPlannedReturnDate(),
                    java.time.LocalDate.now()
                );
                    overdueTableModel.addRow(new Object[]{
                        r.getRentalID(),
                        r.getCustomerID(),
                        r.getCustomerName(),
                        r.getVehicleID(),
                        r.getVehicleBrand(),
                        r.getVehicleModel(),
                        r.getPlannedReturnDate(),
                        daysOverdue + " days"
                    });
            }

        } catch (RentalSystemException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load reports: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
