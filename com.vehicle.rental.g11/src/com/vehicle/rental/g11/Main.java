package com.vehicle.rental.g11;

import com.vehicle.rental.g11.gui.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}