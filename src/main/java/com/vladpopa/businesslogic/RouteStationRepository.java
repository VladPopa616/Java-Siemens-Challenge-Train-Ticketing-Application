package com.vladpopa.businesslogic;

import com.vladpopa.data.RouteStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteStationRepository extends JpaRepository<RouteStation, Integer> {
    // Helps the Admin modify routes as per Requirement (c)
    // Find all route IDs that pass through a specific station
    @Query("SELECT rs.route.id FROM RouteStation rs WHERE rs.station.id = ?1")
    List<Integer> findRouteIdsByStationId(int stationId);

    // Get all station IDs on a specific route, ordered by stop sequence
    @Query("SELECT rs.station.id FROM RouteStation rs WHERE rs.route.id = ?1 ORDER BY rs.stopOrder ASC")
    List<Integer> findStationIdsByRouteId(int routeId);

    // Get the stop order for a specific station on a specific route (for direction validation)
    @Query("SELECT rs.stopOrder FROM RouteStation rs WHERE rs.route.id = ?1 AND rs.station.id = ?2")
    Integer findStopOrder(int routeId, int stationId);
}
