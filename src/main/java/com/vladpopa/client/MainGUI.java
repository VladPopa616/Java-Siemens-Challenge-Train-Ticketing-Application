package com.vladpopa.client;

import com.vladpopa.businesslogic.TrainRepository;
import com.vladpopa.businesslogic.TrainService;
import com.vladpopa.data.Booking;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@org.springframework.stereotype.Component
public class MainGUI extends JFrame {
    private final TrainService trainService;

    // Shared Log Area for cross-panel alerts
    private JTextArea sharedLogArea;
    private JComboBox<String> trainBox, fromBox, toBox;
    private DefaultTableModel adminTableModel;

    public MainGUI(TrainService trainService) {
        this.trainService = trainService;

        // 1. Setup Frame Properties
        setTitle("Train Ticketing System");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 2. INITIALIZE SHARED COMPONENTS FIRST
        // This must happen before createCustomerPanel or createAdminPanel are called
        sharedLogArea = new JTextArea();
        sharedLogArea.setEditable(false);
        sharedLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        sharedLogArea.setBackground(new Color(245, 245, 245));

        // 3. Setup Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Customer: Search & Book", createCustomerPanel());
        tabs.addTab("Admin: Management", createAdminPanel());

        add(tabs);
        setVisible(true);
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Input Grid
        JPanel inputs = new JPanel(new GridLayout(0, 2, 5, 5));
        inputs.setBorder(BorderFactory.createTitledBorder("Trip Details"));

        trainBox = new JComboBox<>();
        fromBox = new JComboBox<>();
        toBox = new JComboBox<>();
        JTextField emailField = new JTextField();
        JSpinner seatSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        // Load data from DB
        loadDropdownData();

        inputs.add(new JLabel("Select Train:"));
        inputs.add(trainBox);
        inputs.add(new JLabel("From:"));
        inputs.add(fromBox);
        inputs.add(new JLabel("To:"));
        inputs.add(toBox);
        inputs.add(new JLabel("Email:"));
        inputs.add(emailField);
        inputs.add(new JLabel("Seats:"));
        inputs.add(seatSpinner);

        JButton bookBtn = new JButton("Confirm Booking");
        bookBtn.setFont(new Font("Arial", Font.BOLD, 14));

        // Use the shared area
        JScrollPane logScroll = new JScrollPane(sharedLogArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Booking History & System Alerts"));

        bookBtn.addActionListener(e -> {
            try {
                String tId = (String) trainBox.getSelectedItem();
                int fId = extractId(fromBox.getSelectedItem().toString());
                int tIdStat = extractId(toBox.getSelectedItem().toString());
                String email = emailField.getText().trim();
                int seats = (int) seatSpinner.getValue();

                String result = trainService.bookTicket(tId, email, tIdStat, fId, seats);
                sharedLogArea.append("[BOOKING] " + result + " for " + email + "\n");
            } catch (Exception ex) {
                sharedLogArea.append("[ERROR] Booking failed: " + ex.getMessage() + "\n");
            }
            scrollToBottom();
        });

        panel.add(inputs, BorderLayout.NORTH);
        panel.add(logScroll, BorderLayout.CENTER);
        panel.add(bookBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // 1. TOP: The Control Bar (Re-including the Search Field)
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // RESTORED: The explicit Train ID field used by the listeners below
        JTextField searchField = new JTextField(10);
        JButton refreshBtn = new JButton("Refresh Bookings");
        JButton delayBtn = new JButton("Signal Delay");

        top.add(new JLabel("Train ID:"));
        top.add(searchField); // Adding the field back to the UI
        top.add(refreshBtn);
        top.add(delayBtn);

        // 2. CENTER: The Bookings Table
        adminTableModel = new DefaultTableModel(new String[]{"ID", "Email", "From", "To", "Seats"}, 0);
        JTable table = new JTable(adminTableModel);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tableScroll.setPreferredSize(new Dimension(850, 300));

        // 3. LISTENERS: Using the restored searchField
        refreshBtn.addActionListener(e -> {
            String id = searchField.getText().trim().toUpperCase();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Train ID.");
                return;
            }

            List<Booking> bookings = trainService.getBookingsForTrain(id);
            adminTableModel.setRowCount(0);
            for (Booking b : bookings) {
                adminTableModel.addRow(new Object[]{
                        b.getId(),
                        b.getCustomerEmail(),
                        b.getStartStationId(),
                        b.getEndStationId(),
                        b.getNumSeats()
                });
            }
        });

        delayBtn.addActionListener(e -> {
            String id = searchField.getText().trim().toUpperCase();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a Train ID first.");
                return;
            }

            String mins = JOptionPane.showInputDialog("Enter Delay Minutes:");
            if (mins == null) return;

            try {
                int delayMins = Integer.parseInt(mins);
                trainService.updateDelay(id, delayMins);

                // Shared Alert Message to Customer Tab
                sharedLogArea.append("\n*******************************************************\n");
                sharedLogArea.append("ALERT: Train " + id + " delayed by " + mins + " minutes.\n");
                sharedLogArea.append("Please check your email for schedule updates.\n");
                sharedLogArea.append("*******************************************************\n");
                scrollToBottom();

                JOptionPane.showMessageDialog(this, "Alert broadcasted to Customer Log.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for minutes.");
            }
        });

        // 4. ASSEMBLE
        panel.add(top, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);

        return panel;
    }

    private void loadDropdownData() {
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
