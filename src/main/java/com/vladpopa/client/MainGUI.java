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

    @Autowired
    private
    TrainService trainService;
    @Autowired
    private TrainRepository trainRepository;

    public void init() {
        setTitle("Train Ticketing System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Customer: Search & Book", createCustomerPanel());
        tabs.addTab("Admin: Management", createAdminPanel());

        add(tabs);
        setVisible(true);
    }

    private JPanel createCustomerPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // --- TOP: Input Fields ---
        JPanel inputGrid = new JPanel(new GridLayout(0, 2, 5, 5));
        inputGrid.setBorder(BorderFactory.createTitledBorder("Booking Details"));

        JComboBox<String> trainBox = new JComboBox<>();
        JComboBox<String> fromBox = new JComboBox<>();
        JComboBox<String> toBox = new JComboBox<>();
        JTextField emailField = new JTextField();
        JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        // Fill Dropdowns
        trainService.getAllTrains().forEach(t -> trainBox.addItem(t.getTrainId()));
        trainService.getAllStations().forEach(s -> {
            fromBox.addItem(s.getName() + " (ID: " + s.getId() + ")");
            toBox.addItem(s.getName() + " (ID: " + s.getId() + ")");
        });

        inputGrid.add(new JLabel("Select Train:"));
        inputGrid.add(trainBox);
        inputGrid.add(new JLabel("From Station:"));
        inputGrid.add(fromBox);
        inputGrid.add(new JLabel("To Station:"));
        inputGrid.add(toBox);
        inputGrid.add(new JLabel("Email Address:"));
        inputGrid.add(emailField);
        inputGrid.add(new JLabel("Number of Seats:"));
        inputGrid.add(seatsSpinner);

        // --- CENTER: Status Log ---
        JTextArea logArea = new JTextArea(12, 50);
        logArea.setEditable(false);
        logArea.setBackground(new Color(245, 245, 245));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Transaction Log"));

        // --- BOTTOM: Action Button ---
        JButton bookBtn = new JButton("Confirm Booking");
        bookBtn.setPreferredSize(new Dimension(0, 40));

        bookBtn.addActionListener(e -> {
            String tId = (String) trainBox.getSelectedItem();
            // Regex to grab only the ID number from the dropdown string
            int fId = Integer.parseInt(fromBox.getSelectedItem().toString().replaceAll(".*ID: (\\d+).*", "$1"));
            int tIdStat = Integer.parseInt(toBox.getSelectedItem().toString().replaceAll(".*ID: (\\d+).*", "$1"));
            String email = emailField.getText().trim();
            int seats = (int) seatsSpinner.getValue();

            if(email.isEmpty()) {
                logArea.append("> ERROR: Email is required.\n");
                return;
            }

            try {
                String msg = trainService.bookTicket(tId, email, tIdStat, fId, seats);
                logArea.append("> SUCCESS: " + msg + " for " + email + "\n");
            } catch (Exception ex) {
                logArea.append("> FAILURE: " + ex.getMessage() + "\n");
            }
        });

        mainPanel.add(inputGrid, BorderLayout.NORTH);
        mainPanel.add(logScroll, BorderLayout.CENTER);
        mainPanel.add(bookBtn, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createAdminPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 1. Top Control Bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField trainIdSearch = new JTextField(10);
        JButton viewBookingsBtn = new JButton("Refresh Bookings List");
        JButton delayBtn = new JButton("Signal Delay");

        topPanel.add(new JLabel("Train ID:"));
        topPanel.add(trainIdSearch);
        topPanel.add(viewBookingsBtn);
        topPanel.add(delayBtn);

        // 2. Table Setup
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[]{"ID", "Email", "From", "To", "Seats"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        // 3. ScrollPane with Forced Constraints
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        // This forces the scrollbar to engage by limiting visible height
        scrollPane.setPreferredSize(new Dimension(800, 250));

        // 4. Button Logic
        viewBookingsBtn.addActionListener(e -> {
            String id = trainIdSearch.getText().trim().toUpperCase();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Train ID.");
                return;
            }

            List<Booking> bookings = trainService.getBookingsForTrain(id);
            tableModel.setRowCount(0);

            for (Booking b : bookings) {
                tableModel.addRow(new Object[]{
                        b.getId(),
                        b.getCustomerEmail(),
                        b.getStartStationId(),
                        b.getEndStationId(),
                        b.getNumSeats()
                });
            }

            if (bookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No bookings found for " + id);
            }
        });

        delayBtn.addActionListener(e -> {
            String id = trainIdSearch.getText().trim().toUpperCase();
            String mins = JOptionPane.showInputDialog("Enter Delay (minutes):");
            if (id.isEmpty() || mins == null) return;

            try {
                trainService.updateDelay(id, Integer.parseInt(mins));
                JOptionPane.showMessageDialog(this, "Delay of " + mins + "m reported for " + id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        });

        // 5. Final Assembly
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }
}
