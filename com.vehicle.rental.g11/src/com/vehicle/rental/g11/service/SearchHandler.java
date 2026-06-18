package com.vehicle.rental.g11.service;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * Generic handler to debounce search inputs to prevent query spamming.
 */
public class SearchHandler {
    private Timer debounceTimer;
    private final Consumer<String> searchAction;

    public SearchHandler(Consumer<String> searchAction) {
        this.searchAction = searchAction;
    }

    /**
     * Triggers the search action after a specified cooldown period.
     * @param query The search term
     */
    public void onQueryChanged(String query) {
        if (debounceTimer != null) {
            debounceTimer.stop();
        }

        debounceTimer = new Timer(300, e -> searchAction.accept(query));
        debounceTimer.setRepeats(false);
        debounceTimer.start();
    }
}
