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
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));

        JTextField startField = new JTextField();
        JTextField endField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField seatsField = new JTextField();
        JTextField trainIdField = new JTextField();
        JTextArea output = new JTextArea();

        inputPanel.add(new JLabel("Start Station ID:")); inputPanel.add(startField);
        inputPanel.add(new JLabel("End Station ID:")); inputPanel.add(endField);

        JButton searchBtn = new JButton("Search Routes");
        inputPanel.add(searchBtn); inputPanel.add(new JLabel(""));

        inputPanel.add(new JLabel("Train ID to Book:")); inputPanel.add(trainIdField);
        inputPanel.add(new JLabel("Your Email:")); inputPanel.add(emailField);
        inputPanel.add(new JLabel("Seats:")); inputPanel.add(seatsField);

        JButton bookBtn = new JButton("Book Ticket");
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);
        panel.add(bookBtn, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> {
            var routes = trainService.findPossibleRoutes(Integer.parseInt(startField.getText()), Integer.parseInt(endField.getText()));
            output.setText(routes.isEmpty() ? "No direct link found." : String.join("\n", routes));
        });

        bookBtn.addActionListener(e -> {
            String result = trainService.bookTicket(trainIdField.getText(), emailField.getText(),
                    Integer.parseInt(startField.getText()), Integer.parseInt(endField.getText()),
                    Integer.parseInt(seatsField.getText()));
            JOptionPane.showMessageDialog(this, result);
        });

        return panel;
    }

    private JPanel createAdminPanel() {

        JPanel mainPanel = new JPanel(new BorderLayout());

        // 2. Top section for inputs and buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Train ID:"));
        JTextField trainIdSearch = new JTextField(10);
        topPanel.add(trainIdSearch);
        JButton viewBookingsBtn = new JButton("Refresh Bookings List");
        topPanel.add(viewBookingsBtn);
        JButton delayBtn = new JButton("Signal Delay");
        topPanel.add(delayBtn);

        // 3. The Table logic
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Email", "From", "To", "Seats"}, 0);
        JTable table = new JTable(tableModel);

        // 4. THE FIX: Wrap in ScrollPane and set a preferred size
        JScrollPane scrollPane = new JScrollPane(table);
        // This forces the 'viewable area' to be fixed so scrollbars MUST appear
        scrollPane.setPreferredSize(new Dimension(800, 300));

        // 5. Place them correctly
        mainPanel.add(topPanel, BorderLayout.NORTH); // Inputs stay at top
        mainPanel.add(scrollPane, BorderLayout.CENTER);

            //Logic for the Buttons
            viewBookingsBtn.addActionListener(e -> {
                String id = trainIdSearch.getText().trim().toUpperCase();
                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a Train ID first.");
                    return;
                }

                // Fetch data (Requirement c)
                List<Booking> bookings = trainService.getBookingsForTrain(id);

                // Clear old rows
                tableModel.setRowCount(0);

                // Add new rows
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
                    JOptionPane.showMessageDialog(this, "No bookings found for train: " + id);
                }
            });

            delayBtn.addActionListener(e -> {
                String id = trainIdSearch.getText().trim().toUpperCase();
                String mins = JOptionPane.showInputDialog("Enter Delay (minutes):");
                if (!id.isEmpty() && mins != null) {
                    trainService.updateDelay(id, Integer.parseInt(mins));
                    JOptionPane.showMessageDialog(this, "Delay reported and customers notified.");
                }
            });

            // 4. Assemble
            mainPanel.add(topPanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            return mainPanel;

    }
}
