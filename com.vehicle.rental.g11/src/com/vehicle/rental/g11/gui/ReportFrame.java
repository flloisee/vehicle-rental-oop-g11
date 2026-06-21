package com.vehicle.rental.g11.gui;

import com.vehicle.rental.g11.service.RentalEngine;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Rentals;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.vehicle.rental.g11.gui.UITheme;

public class ReportFrame extends JFrame {

    private JLabel totalRevenueLabel, activeRentalsLabel, overdueLabel;
    private JTable overdueTable;
    private DefaultTableModel overdueTableModel;
    private RentalEngine rentalEngine;

    private MainFrame mainFrame;
 
    public ReportFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        rentalEngine = new RentalEngine();

        setTitle("Reports & Statistics");
        setSize(800, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UITheme.BG);

        add(buildStatsPanel(), BorderLayout.NORTH);
        add(buildOverduePanel(), BorderLayout.CENTER);
        add(buildRefreshPanel(), BorderLayout.SOUTH);

        loadReports();
addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                ReportFrame.this.mainFrame.setVisible(true);
            }
        });
        setVisible(true);
    }

    // -------------------------------------------------------
    // STATS PANEL — summary numbers at the top
    // -------------------------------------------------------
    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 16, 0));
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        totalRevenueLabel = makeValueLabel("Loading...", UITheme.ACCENT);
        activeRentalsLabel = makeValueLabel("Loading...", UITheme.ACCENT);
        overdueLabel = makeValueLabel("Loading...", UITheme.ACCENT);

        panel.add(makeStatCard("Total Revenue", totalRevenueLabel, UITheme.SUCCESS));
        panel.add(makeStatCard("Active Rentals", activeRentalsLabel, UITheme.INFO));
        panel.add(makeStatCard("Overdue", overdueLabel, UITheme.WARNING));

        return panel;
    }

    private JPanel makeStatCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.ACCENT, 2),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        // Set card background to match theme's card background
        card.setBackground(UITheme.BG_CARD);

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
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createTitledBorder("Overdue Rentals"));

        String[] columns = {"Rental ID", "Customer ID", "Customer Name", "Vehicle ID", "Vehicle Brand", "Vehicle Model",
                                   "Planned Return", "Days Overdue"};
        overdueTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        overdueTable = new JTable(overdueTableModel) {
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
                java.awt.Component c = super.prepareRenderer(renderer, row, column);
                if (row == hoverRow && !isRowSelected(row)) {
                    c.setBackground(UITheme.PURPLE_LIGHT);
                } else {
                    c.setBackground(UITheme.BG);
                }
                return c;
            }
        };
        UITheme.styleTable(overdueTable);
        overdueTable.setRowHeight(28);
        overdueTable.setIntercellSpacing(new java.awt.Dimension(0, 5));
        overdueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(overdueTable), BorderLayout.CENTER);

        return panel;
    }

    // -------------------------------------------------------
    // REFRESH BUTTON
    // -------------------------------------------------------
    private JPanel buildRefreshPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshBtn = UITheme.roundedButton("Refresh Reports");
        refreshBtn.addActionListener(e -> loadReports());
        
        JButton backBtn = UITheme.roundedButton("Back to Main Menu");
        backBtn.addActionListener(e -> {
            dispose();
            mainFrame.setVisible(true);
        });
        panel.setBackground(UITheme.BG);
        
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
