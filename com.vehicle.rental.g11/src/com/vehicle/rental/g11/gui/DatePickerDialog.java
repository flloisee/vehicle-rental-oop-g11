package com.vehicle.rental.g11.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DatePickerDialog extends JDialog {
    private LocalDate selectedDate;
    private YearMonth currentMonth;
    private JLabel monthLabel;
    private JPanel daysPanel;
    private Consumer<LocalDate> onDateSelected;

    public DatePickerDialog(JFrame parent, LocalDate initialDate, Consumer<LocalDate> onDateSelected) {
        super(parent, "Select Date", true);
        this.selectedDate = initialDate != null ? initialDate : LocalDate.now();
        this.currentMonth = YearMonth.from(selectedDate);
        this.onDateSelected = onDateSelected;

        setLayout(new BorderLayout());
        setSize(300, 350);
        setLocationRelativeTo(parent);

        JPanel header = new JPanel(new BorderLayout());
        monthLabel = new JLabel("", JLabel.CENTER);
        JButton prevButton = UITheme.roundedButton("<", UITheme.ACCENT, UITheme.ACCENT_HOVER);
        JButton nextButton = UITheme.roundedButton(">", UITheme.ACCENT, UITheme.ACCENT_HOVER);

        prevButton.addActionListener(e -> changeMonth(-1));
        nextButton.addActionListener(e -> changeMonth(1));

        header.add(prevButton, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(nextButton, BorderLayout.EAST);

        daysPanel = new JPanel(new GridLayout(0, 7));
        
        add(header, BorderLayout.NORTH);
        add(daysPanel, BorderLayout.CENTER);

        JButton okButton = new JButton("Select");
        okButton.addActionListener(e -> {
            onDateSelected.accept(selectedDate);
            dispose();
        });
        add(okButton, BorderLayout.SOUTH);

        updateCalendar();
    }

    private void changeMonth(int delta) {
        currentMonth = currentMonth.plusMonths(delta);
        updateCalendar();
    }

    private void updateCalendar() {
        monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());
        daysPanel.removeAll();

        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : daysOfWeek) {
            JLabel label = new JLabel(day, JLabel.CENTER);
            label.setForeground(Color.GRAY);
            daysPanel.add(label);
        }

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue() % 7;

        for (int i = 0; i < dayOfWeekValue; i++) {
            daysPanel.add(new JLabel(""));
        }

        int lengthOfMonth = currentMonth.lengthOfMonth();
        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            JButton dayButton = new JButton(String.valueOf(day));
            if (date.equals(selectedDate)) {
                dayButton.setOpaque(true);
                dayButton.setBackground(UITheme.ACCENT);
                dayButton.setForeground(Color.BLACK);
            }
            dayButton.addActionListener(e -> {
                selectedDate = date;
                updateCalendar();
            });
            daysPanel.add(dayButton);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }
}
