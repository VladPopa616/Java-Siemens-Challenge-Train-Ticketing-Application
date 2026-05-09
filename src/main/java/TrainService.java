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

    // Requirement (a): Book tickets and prevent overbooking
    public String bookTicket(String trainId, String email, int startId, int endId, int seats) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        int currentlyBooked = bookingRepository.findByTrainTrainId(trainId).stream()
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

        List<Booking> customers = bookingRepository.findByTrainTrainId(trainId);
        for (Booking b : customers) {
            System.out.println("DELAY NOTIFICATION sent to " + b.getCustomerEmail() +
                    ": Train " + trainId + " is delayed by " + minutes + " minutes.");
        }
    }
}
