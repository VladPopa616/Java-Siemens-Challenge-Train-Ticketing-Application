package com.vladpopa.client;

import com.vladpopa.businesslogic.TrainRepository;
import com.vladpopa.businesslogic.TrainService;
import com.vladpopa.data.Booking;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

@org.springframework.stereotype.Component
public class MainGUI extends JFrame {
    private final TrainService trainService;

    // Shared components
    private JTextArea sharedLogArea;
    private JComboBox<String> trainBox, fromBox, toBox;
    private DefaultTableModel adminTableModel;

    public MainGUI(TrainService trainService) {
        this.trainService = trainService;

        // 1. Setup Frame
        setTitle("Train Management & Ticketing System");
        setSize(1000, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 2. Initialize Shared Log (Used by both tabs)
        sharedLogArea = new JTextArea();
        sharedLogArea.setEditable(false);
        sharedLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        sharedLogArea.setBackground(new Color(245, 245, 245));

        // 3. Create Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Customer: Search & Book", createCustomerPanel());
        tabs.addTab("Admin: Management", createAdminPanel());

        add(tabs);
        setVisible(true);
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel inputs = new JPanel(new GridLayout(0, 2, 5, 5));
        inputs.setBorder(BorderFactory.createTitledBorder("Trip Details"));

        trainBox = new JComboBox<>();
        fromBox = new JComboBox<>();
        toBox = new JComboBox<>();
        JTextField emailField = new JTextField();
        JSpinner seatSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        loadDropdownData(); // Initial fill

        inputs.add(new JLabel("Select Train:"));
        inputs.add(trainBox);
        inputs.add(new JLabel("From Station:"));
        inputs.add(fromBox);
        inputs.add(new JLabel("To Station:"));
        inputs.add(toBox);
        inputs.add(new JLabel("Email Address:"));
        inputs.add(emailField);
        inputs.add(new JLabel("Number of Seats:"));
        inputs.add(seatSpinner);

        JButton bookBtn = new JButton("Confirm Booking");
        bookBtn.setPreferredSize(new Dimension(0, 40));

        bookBtn.addActionListener(e -> {
            try {
                String tId = (String) trainBox.getSelectedItem();
                int fId = extractId(fromBox.getSelectedItem().toString());
                int tIdStat = extractId(toBox.getSelectedItem().toString());
                String email = emailField.getText().trim();
                int seats = (int) seatSpinner.getValue();

                if (email.isEmpty()) {
                    sharedLogArea.append("[ERROR] Email is required.\n");
                    return;
                }

                String result = trainService.bookTicket(tId, email, tIdStat, fId, seats);
                sharedLogArea.append("[BOOKING] " + result + " (" + email + ")\n");
                scrollToBottom();
            } catch (Exception ex) {
                sharedLogArea.append("[ERROR] " + ex.getMessage() + "\n");
            }
        });

        panel.add(inputs, BorderLayout.NORTH);
        panel.add(new JScrollPane(sharedLogArea), BorderLayout.CENTER);
        panel.add(bookBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // --- TOP: Control Bars ---
        JPanel controlContainer = new JPanel(new GridLayout(2, 1));

        // Row 1: Search and Delay
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(10);
        JButton refreshBtn = new JButton("Refresh Bookings");
        JButton delayBtn = new JButton("Signal Delay");
        row1.add(new JLabel("Train ID:"));
        row1.add(searchField);
        row1.add(refreshBtn);
        row1.add(delayBtn);

        // Row 2: Resource CRUD
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addTrainBtn = new JButton("Add New Train");
        JButton addStationBtn = new JButton("Add New Station");
        JButton deleteTrainBtn = new JButton("Delete Train");
        JButton updateTrainBtn = new JButton("Update Train Capacity");
        row2.add(addTrainBtn);
        row2.add(addStationBtn);
        row2.add(deleteTrainBtn);
        row2.add(updateTrainBtn);

        controlContainer.add(row1);
        controlContainer.add(row2);

        // --- CENTER: The Table with Action Buttons ---
        adminTableModel = new DefaultTableModel(
                new String[]{"ID", "Email", "From", "To", "Seats", "Modify", "Remove"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(adminTableModel);

        // Button Renderer for columns 5 and 6
        TableCellRenderer buttonRenderer = (t, value, isSelected, hasFocus, row, column) -> {
            JButton btn = new JButton(column == 5 ? "Update" : "Delete");
            if (column == 6) btn.setForeground(Color.RED);
            return btn;
        };
        table.getColumn("Modify").setCellRenderer(buttonRenderer);
        table.getColumn("Remove").setCellRenderer(buttonRenderer);

        // Click Logic for the buttons
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col < 5) return;

                int bookingId = (int) table.getValueAt(row, 0);

                if (col == 5) { // UPDATE
                    String newSeatsStr = JOptionPane.showInputDialog("New seats for #" + bookingId);
                    if (newSeatsStr != null) {
                        try {
                            int newSeats = Integer.parseInt(newSeatsStr);

                            // 1. UPDATE THE DATABASE
                            trainService.updateBookingSeats(bookingId, newSeats);

                            // 2. UPDATE THE UI (So it reflects immediately)
                            table.setValueAt(newSeats, row, 4);

                            sharedLogArea.append("[ADMIN] Permanent Update: Booking #" + bookingId + " is now " + newSeats + " seats.\n");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Update failed: " + ex.getMessage());
                        }
                    }
                } else if (col == 6) { // DELETE
                    if (JOptionPane.showConfirmDialog(null, "Permanently delete #" + bookingId + "?") == JOptionPane.YES_OPTION) {
                        try {
                            // 1. DELETE FROM DATABASE
                            trainService.deleteBooking(bookingId);

                            // 2. REMOVE FROM UI
                            adminTableModel.removeRow(row);

                            sharedLogArea.append("[ADMIN] Permanent Delete: Booking #" + bookingId + " removed from system.\n");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Delete failed: " + ex.getMessage());
                        }
                    }
                }
            }
        });

        // --- ADMIN LISTENERS ---
        refreshBtn.addActionListener(e -> {
            String id = searchField.getText().trim().toUpperCase();
            List<Booking> bookings = trainService.getBookingsForTrain(id);
            adminTableModel.setRowCount(0);
            for (Booking b : bookings) {
                adminTableModel.addRow(new Object[]{b.getId(), b.getCustomerEmail(), b.getStartStationId(), b.getEndStationId(), b.getNumSeats(), "Update", "Delete"});
            }
        });

        delayBtn.addActionListener(e -> {
            String id = searchField.getText().trim().toUpperCase();
            String mins = JOptionPane.showInputDialog("Enter Delay Minutes:");
            if (id.isEmpty() || mins == null) return;

            trainService.updateDelay(id, Integer.parseInt(mins));
            sharedLogArea.append("\n*** ALERT: Train " + id + " delayed by " + mins + "m. Check email. ***\n");
            scrollToBottom();
        });

        addTrainBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog("New Train ID:");
            String cap = JOptionPane.showInputDialog("Capacity:");
            if (id != null && cap != null) {
                trainService.saveTrain(id.toUpperCase(), Integer.parseInt(cap));
                loadDropdownData();
                sharedLogArea.append("[ADMIN] Created Train: " + id + "\n");
            }
        });

        addStationBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Station Name:");
            if (name != null) {
                trainService.saveStation(name);
                loadDropdownData();
                sharedLogArea.append("[ADMIN] Created Station: " + name + "\n");
            }
        });

        deleteTrainBtn.addActionListener(e -> {
            String id = searchField.getText().trim().toUpperCase();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Train ID in the search box to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to PERMANENTLY delete train " + id + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // 1. Delete from Database
                    trainService.deleteTrain(id);

                    // 2. Update the UI Log
                    sharedLogArea.append("[ADMIN] SUCCESS: Train " + id + " has been removed from the system.\n");

                    // 3. Refresh Customer Dropdowns so they don't see the deleted train
                    loadDropdownData();

                    // 4. Clear the search field and table
                    searchField.setText("");
                    adminTableModel.setRowCount(0);

                    JOptionPane.showMessageDialog(this, "Train deleted successfully.");
                    scrollToBottom();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Delete Failed: " + ex.getMessage());
                    sharedLogArea.append("[ADMIN] ERROR: Could not delete " + id + " (Still has bookings?)\n");
                }
            }
        });

        // --- Update Train Listener ---
        updateTrainBtn.addActionListener(e -> {
            String id = searchField.getText().trim().toUpperCase();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the Train ID in the search box to update.");
                return;
            }

            String newCapStr = JOptionPane.showInputDialog(this, "Enter new capacity for train " + id + ":");

            if (newCapStr != null && !newCapStr.isEmpty()) {
                try {
                    int newCap = Integer.parseInt(newCapStr);

                    // 1. Update Database
                    trainService.updateTrainCapacity(id, newCap);

                    // 2. Log the change
                    sharedLogArea.append("[ADMIN] UPDATED: Train " + id + " capacity changed to " + newCap + ".\n");
                    scrollToBottom();

                    JOptionPane.showMessageDialog(this, "Train capacity updated successfully.");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number. Please enter a numeric capacity.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Update Failed: " + ex.getMessage());
                }
            }
        });

        panel.add(controlContainer, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadDropdownData() {
        trainBox.removeAllItems();
        fromBox.removeAllItems();
        toBox.removeAllItems();
        trainService.getAllTrains().forEach(t -> trainBox.addItem(t.getTrainId()));
        trainService.getAllStations().forEach(s -> {
            String item = s.getName() + " (ID: " + s.getId() + ")";
            fromBox.addItem(item);
            toBox.addItem(item);
        });
    }

    private int extractId(String text) {
        return Integer.parseInt(text.replaceAll(".*ID: (\\d+).*", "$1"));
    }

    private void scrollToBottom() {
        sharedLogArea.setCaretPosition(sharedLogArea.getDocument().getLength());
    }
}
