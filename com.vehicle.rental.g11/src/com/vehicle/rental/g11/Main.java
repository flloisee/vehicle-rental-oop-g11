package com.vehicle.rental.g11;

import javax.swing.SwingUtilities;

import com.vehicle.rental.g11.gui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}