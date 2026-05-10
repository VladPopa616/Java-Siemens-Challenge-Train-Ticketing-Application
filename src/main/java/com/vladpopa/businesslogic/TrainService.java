package com.vladpopa.businesslogic;

import com.vladpopa.data.Booking;
import com.vladpopa.data.Station;
import com.vladpopa.data.Train;
//import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainService {

    @Autowired
    private TrainRepository trainRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private StationRepository stationRepository;

    // Requirement (a): Book tickets and prevent overbooking
    @Transactional
    public String bookTicket(String trainId, String email, int startId, int endId, int seats) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        int currentlyBooked = bookingRepository.findByTrainTrainIdOrderByIdAsc(trainId).stream()
                .mapToInt(Booking::getNumSeats)
                .sum();

        if (currentlyBooked + seats > train.getTotalCapacity()) {
            return "Error: Not enough seats available. Capacity: " + train.getTotalCapacity();
        }

        Booking newBooking = new Booking();
        newBooking.setTrain(train);
        newBooking.setCustomerEmail(email);
        newBooking.setStartStationId(startId);
        newBooking.setEndStationId(endId);
        newBooking.setNumSeats(seats);

        bookingRepository.save(newBooking);

        // Requirement (a): Confirmation mail simulation
        System.out.println("CONFIRMATION MAIL sent to " + email + " for Train " + trainId);
        return "Booking Successful! Confirmation sent to " + email;
    }

    // Requirement (b): Finding possible departure/arrival times
    public List<String> findPossibleRoutes(int startId, int endId) {
        return routeRepository.findAll().stream()
                .filter(route -> {
                    var stops = route.getRouteStations();
                    int startIdx = -1, endIdx = -1;
                    for (int i = 0; i < stops.size(); i++) {
                        if (stops.get(i).getStation().getId() == startId) startIdx = i;
                        if (stops.get(i).getStation().getId() == endId) endIdx = i;
                    }
                    return startIdx != -1 && endIdx != -1 && startIdx < endIdx;
                })
                .map(r -> "Route: " + r.getRouteName() + " (Direct)")
                .collect(Collectors.toList());
    }

    // Requirement (c): Specify delays and notify customers
    public void updateDelay(String trainId, int minutes) {
        Train train = trainRepository.findById(trainId).orElseThrow();
        train.setCurrentDelayMinutes(minutes);
        trainRepository.save(train);

        List<Booking> customers = bookingRepository.findByTrainTrainIdOrderByIdAsc(trainId);
        for (Booking b : customers) {
            System.out.println("DELAY NOTIFICATION sent to " + b.getCustomerEmail() +
                    ": Train " + trainId + " is delayed by " + minutes + " minutes.");
        }
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsForTrain(String trainId) {
        return bookingRepository.findByTrainTrainIdOrderByIdAsc(trainId);
    }

    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    @Transactional
    public void saveTrain(String id, int capacity) {
        Train train = new Train();
        train.setTrainId(id);
        train.setTotalCapacity(capacity);
        trainRepository.save(train);
    }

    @Transactional
    public void deleteTrain(String id) {
        // Check if the train exists first
        if (!trainRepository.existsById(id)) {
            throw new RuntimeException("Train " + id + " does not exist.");
        }

        // Attempt deletion
        try {
            trainRepository.deleteById(id);
        } catch (Exception e) {
            // This usually triggers if there are still bookings linked to the train
            throw new RuntimeException("Cannot delete train. It still has active bookings.");
        }
    }

    @Transactional
    public void saveStation(String name) {
        Station station = new Station();
        station.setName(name);
        stationRepository.save(station);
    }

    @Transactional
    public void updateBookingSeats(int bookingId, int newSeats) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Requirement (a): Still validate capacity here before saving!
        booking.setNumSeats(newSeats);
        bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(int bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Transactional
    public void updateTrainCapacity(String trainId, int newCapacity) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train " + trainId + " not found."));

        train.setTotalCapacity(newCapacity);
        trainRepository.save(train); // Hibernate updates the existing record
    }
}
