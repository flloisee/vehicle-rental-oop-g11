package com.vehicle.rental.g11.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Rentals;
import com.vehicle.rental.g11.service.RentalEngine;

public class ReportFrame extends JFrame {

    private JLabel totalRevenueLabel, activeRentalsLabel;
    private JLabel overdueLabel, todaysRentalsLabel;
    private JLabel insightMostRentedLabel, insightOverdueLabel, insightUnpaidLabel;
    private JLabel insightUtilizationLabel, insightTopCustomerLabel, insightAvgDurationLabel;
    private JLabel tableTitleLabel;
    
    private JTable overdueTable;
    private DefaultTableModel overdueTableModel;
    private RentalEngine rentalEngine;
    private MainFrame mainFrame;
 
    public ReportFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        rentalEngine = new RentalEngine();

        setTitle("Reports & Statistics - Modern Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UITheme.BG);

        // Create main scroll pane for all content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UITheme.BG);

        // Add panels in order
        mainPanel.add(buildSummaryCardsPanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buildInsightsPanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buildOverduePanel());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        add(buildRefreshPanel(), BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                ReportFrame.this.mainFrame.setVisible(true);
            }
        });
        
        loadReports();
        setVisible(true);
    }

    // -------------------------------------------------------
    // SUMMARY CARDS PANEL — 5 metrics at the top
    // -------------------------------------------------------
    private JPanel buildSummaryCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 12, 0));
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        totalRevenueLabel = makeValueLabel("Loading...", UITheme.SUCCESS);
        activeRentalsLabel = makeValueLabel("Loading...", UITheme.INFO);
        overdueLabel = makeValueLabel("Loading...", new Color(220, 53, 69)); // Red
        todaysRentalsLabel = makeValueLabel("Loading...", new Color(40, 167, 69)); // Green

        panel.add(makeStatCard("💰 Total Revenue", totalRevenueLabel, UITheme.SUCCESS, null));
        panel.add(makeStatCard("🚗 Active Rentals", activeRentalsLabel, UITheme.INFO, this::showActiveRentals));
        panel.add(makeStatCard("⚠️ Overdue", overdueLabel, new Color(220, 53, 69), this::showOverdueRentals));
        panel.add(makeStatCard("📅 Today's Rentals", todaysRentalsLabel, new Color(40, 167, 69), this::showTodayRentals));

        return panel;
    }

    // -------------------------------------------------------
    // INSIGHTS & ANALYTICS PANEL — Dynamic system insights
    // -------------------------------------------------------
    private JPanel buildInsightsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 12, 12));
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UITheme.ACCENT, 1), 
                "📈 Insights & Analytics"),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        // panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140)); // Removed to allow proper sizing

        insightMostRentedLabel = makeValueLabel("Loading...", UITheme.ACCENT);
        insightOverdueLabel = makeValueLabel("Loading...", UITheme.WARNING);
        insightUnpaidLabel = makeValueLabel("Loading...", UITheme.WARNING);
        insightUtilizationLabel = makeValueLabel("Loading...", UITheme.INFO);
        insightTopCustomerLabel = makeValueLabel("Loading...", UITheme.SUCCESS);
        insightAvgDurationLabel = makeValueLabel("Loading...", UITheme.ACCENT);

        panel.add(makeInsightCard("Most Rented Vehicle", insightMostRentedLabel));
        panel.add(makeInsightCard("Overdue Today", insightOverdueLabel));
        panel.add(makeInsightCard("Total Unpaid Balance", insightUnpaidLabel));
        panel.add(makeInsightCard("Vehicle Utilization Rate", insightUtilizationLabel));
        panel.add(makeInsightCard("Top Customer", insightTopCustomerLabel));
        panel.add(makeInsightCard("Avg Rental Duration", insightAvgDurationLabel));

        return panel;
    }

    // -------------------------------------------------------
    // RENTALS TABLE — dynamic list for reports
    // -------------------------------------------------------
    private JPanel buildOverduePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UITheme.ACCENT, 1),
                "📋 Rentals List"),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

        tableTitleLabel = new JLabel("Loading rental list...");
        tableTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        tableTitleLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(tableTitleLabel, BorderLayout.NORTH);

        String[] columns = {"Rental ID", "Customer", "Vehicle", "Start Date", "Due Date", "Days Overdue", "Status"};
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
            public java.awt.Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(UITheme.BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JButton refreshBtn = UITheme.roundedButton("Refresh Reports");
        refreshBtn.addActionListener(e -> loadReports());
        
        JButton backBtn = UITheme.roundedButton("← Back to Main Menu");
        backBtn.addActionListener(e -> {
            dispose();
            mainFrame.setVisible(true);
        });
        
        panel.add(refreshBtn);
        panel.add(backBtn);
        return panel;
    }

    // -------------------------------------------------------
    // HELPER METHODS FOR UI COMPONENTS
    // -------------------------------------------------------

    private JPanel makeStatCard(String title, JLabel valueLabel, Color accent, Runnable onClick) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent, 2),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        card.setBackground(UITheme.BG_CARD);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        titleLabel.setForeground(Color.GRAY);

        card.add(titleLabel);
        card.add(valueLabel);

        if (onClick != null) {
            makeClickable(card, onClick);
        }

        return card;
    }

    private void makeClickable(JPanel card, Runnable onClick) {
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(UITheme.PURPLE_LIGHT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(UITheme.BG_CARD);
            }
        };
        card.addMouseListener(adapter);
        for (Component child : card.getComponents()) {
            child.addMouseListener(adapter);
        }
    }

private JPanel makeInsightCard(String title, JLabel valueLabel) {
    JPanel card = new JPanel();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(UITheme.ACCENT, 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    card.setBackground(UITheme.BG_CARD);
    
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Arial", Font.PLAIN, 10));
    titleLabel.setForeground(Color.GRAY);
    titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    
    valueLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    
    card.add(titleLabel);
    card.add(valueLabel);
    
    return card;
}

    private JLabel makeValueLabel(String value, Color accent) {
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(accent);
        return valueLabel;
    }

    // -------------------------------------------------------
    // LOAD DATA FROM RentalEngine
    // -------------------------------------------------------
    private void loadReports() {
        try {
            // Summary Cards
            double revenue = rentalEngine.getTotalRevenue();
            totalRevenueLabel.setText(String.format("₱%.2f", revenue));

            int active = rentalEngine.getActiveRentalCount();
            activeRentalsLabel.setText(String.valueOf(active));

            List<Rentals> overdue = rentalEngine.getOverdueRentals();
            overdueLabel.setText(String.valueOf(overdue.size()));

            int todaysRentals = rentalEngine.getTodaysRentalsCount();
            todaysRentalsLabel.setText(String.valueOf(todaysRentals));

            // Insights Panel
            insightMostRentedLabel.setText(rentalEngine.getMostRentedVehicle());
            insightOverdueLabel.setText(rentalEngine.getOverdueTodayCount() + " rentals");
            
            double unpaidBalance = rentalEngine.getTotalUnpaidBalance();
            insightUnpaidLabel.setText(String.format("₱%.2f", unpaidBalance));
            
            double utilizationRate = rentalEngine.getVehicleUtilizationRate();
            insightUtilizationLabel.setText(String.format("%.1f%%", utilizationRate));
            
            insightTopCustomerLabel.setText(rentalEngine.getTopCustomer());
            
            double avgDuration = rentalEngine.getAverageRentalDuration();
            insightAvgDurationLabel.setText(String.format("%.1f days", avgDuration));

            // Populate table with all rentals by default
            populateRentalTable(rentalEngine.getAllRentals(), "All Rentals");

        } catch (RentalSystemException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load reports: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateRentalTable(List<Rentals> rentals, String title) {
        tableTitleLabel.setText(title);
        overdueTableModel.setRowCount(0);

        for (Rentals r : rentals) {
            long daysOverdue = 0;
            if (r.getPlannedReturnDate().isBefore(LocalDate.now()) && !r.isReturned()) {
                daysOverdue = ChronoUnit.DAYS.between(r.getPlannedReturnDate(), LocalDate.now());
            }
            String status = rentalEngine.getRentalStatus(r);
            String statusEmoji = getStatusEmoji(status);

            overdueTableModel.addRow(new Object[]{
                r.getRentalID(),
                r.getCustomerName(),
                r.getVehicleBrand() + " " + r.getVehicleModel(),
                r.getRentalDate(),
                r.getPlannedReturnDate(),
                daysOverdue > 0 ? daysOverdue + " days" : "-",
                statusEmoji + " " + status
            });
        }
    }

    private void showActiveRentals() {
        try {
            populateRentalTable(rentalEngine.getActiveRentals(), "Active Rentals");
        } catch (RentalSystemException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load active rentals: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showOverdueRentals() {
        try {
            populateRentalTable(rentalEngine.getOverdueRentals(), "Overdue Rentals");
        } catch (RentalSystemException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load overdue rentals: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTodayRentals() {
        try {
            populateRentalTable(rentalEngine.getTodaysRentals(), "Today's Rentals");
        } catch (RentalSystemException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load today's rentals: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getStatusEmoji(String status) {
        return switch(status) {
            case "OVERDUE" -> "🔴";
            case "ACTIVE" -> "🟢";
            case "COMPLETED" -> "✅";
            default -> "⚫";
        };
    }
}
