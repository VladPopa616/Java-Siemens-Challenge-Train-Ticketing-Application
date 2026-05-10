package com.vladpopa.businesslogic;

import com.vladpopa.data.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    // Find all schedules assigned to a specific route
    List<Schedule> findByRouteId(int routeId);

    // Fetch the first available schedule for a route (useful for simple pathfinding)
    Schedule findFirstByRouteId(int routeId);

    // Find schedules for a specific train (useful for Admin view)
    List<Schedule> findByTrainTrainId(String trainId);

    // Custom query to find a schedule that departs after a certain time
    // (Great for changeover logic to ensure the second train hasn't left yet)
    @Query("SELECT s FROM Schedule s WHERE s.route.id = ?1 AND s.departureTime > ?2 ORDER BY s.departureTime ASC")
    List<Schedule> findSchedulesAfterTime(int routeId, String arrivalTime);
}
