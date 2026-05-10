package com.vladpopa.businesslogic;

import com.vladpopa.data.*;
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
    @Autowired
    private RouteStationRepository routeStationRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

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

    @Transactional(readOnly = true)
    public String findPath(int startId, int endId) {
        List<Integer> startRoutes = routeStationRepository.findRouteIdsByStationId(startId);
        List<Integer> endRoutes = routeStationRepository.findRouteIdsByStationId(endId);

        // 1. Check for DIRECT SCHEDULING
        for (Integer rId : startRoutes) {
            if (endRoutes.contains(rId)) {
                // Find a schedule for this route
                Schedule sched = scheduleRepository.findFirstByRouteId(rId);
                if (sched != null) {
                    return String.format("DIRECT: Route %s | Train: %s | Departs: %s | Arrives: %s",
                            sched.getRoute().getRouteName(), sched.getTrain().getTrainId(),
                            sched.getDepartureTime(), sched.getArrivalTime());
                }
            }
        }

        // 2. Check for CHANGEOVER SCHEDULING
        for (Integer r1 : startRoutes) {
            for (Integer r2 : endRoutes) {
                Integer hubId = findHub(r1, r2);
                if (hubId != null) {
                    Schedule s1 = scheduleRepository.findFirstByRouteId(r1);
                    Schedule s2 = scheduleRepository.findFirstByRouteId(r2);

                    return String.format("CHANGEOVER at Station %d:\n" +
                                    "1. Take %s (Dep: %s, Arr: %s)\n" +
                                    "2. Switch to %s (Dep: %s, Arr: %s)",
                            hubId, s1.getTrain().getTrainId(), s1.getDepartureTime(), s1.getArrivalTime(),
                            s2.getTrain().getTrainId(), s2.getDepartureTime(), s2.getArrivalTime());
                }
            }
        }
        return "ERROR: No schedule found between these stations.";
    }

    @Transactional
    public void updateSchedule(int routeId, String newDepartureTime) {
        // 1. Find the schedule associated with this route
        // (In a more complex system, you might search by schedule_id)
        Schedule schedule = scheduleRepository.findFirstByRouteId(routeId);

        if (schedule == null) {
            throw new RuntimeException("No schedule found for Route ID: " + routeId);
        }

        // 2. Simple validation for HH:mm format
        if (!newDepartureTime.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            throw new RuntimeException("Invalid time format. Please use HH:mm (e.g., 14:30)");
        }

        // 3. Update the departure time
        schedule.setDepartureTime(newDepartureTime);


        schedule.setArrivalTime(calculateArrivalTime(routeId,newDepartureTime));

        scheduleRepository.save(schedule);
    }

    /**
     * Requirement (c): Ability to add a completely new schedule
     */
    @Transactional
    public void createSchedule(String trainId, int routeId, String dep, String arr) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        Schedule newSchedule = new Schedule();
        newSchedule.setTrain(train);
        newSchedule.setRoute(route);
        newSchedule.setDepartureTime(dep);
        newSchedule.setArrivalTime(arr);

        scheduleRepository.save(newSchedule);
    }

    private Integer findHub(int routeId1, int routeId2) {
        // 1. Get all station IDs that belong to the first route
        List<Integer> stationsRoute1 = routeStationRepository.findStationIdsByRouteId(routeId1);

        // 2. Get all station IDs that belong to the second route
        List<Integer> stationsRoute2 = routeStationRepository.findStationIdsByRouteId(routeId2);

        // 3. Find the intersection (the first station that appears in both lists)
        for (Integer stationId : stationsRoute1) {
            if (stationsRoute2.contains(stationId)) {
                return stationId; // We found the transfer point!
            }
        }

        return null; // No common station found between these two routes
    }

    private String calculateArrivalTime(int routeId, String departureTime) {
        // 1. Count how many stations are in this route
        List<Integer> stations = routeStationRepository.findStationIdsByRouteId(routeId);
        int numberOfStops = stations.size() - 1; // Transitions between stations

        if (numberOfStops <= 0) return departureTime;

        // 2. Assume 15 minutes per stop for this simulation
        int totalTravelMinutes = numberOfStops * 15;

        // 3. Parse the departure time (HH:mm)
        String[] parts = departureTime.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        // 4. Add the travel duration
        int totalMinutes = (hours * 60) + minutes + totalTravelMinutes;
        int finalHours = (totalMinutes / 60) % 24;
        int finalMinutes = totalMinutes % 60;

        return String.format("%02d:%02d", finalHours, finalMinutes);
    }

}
