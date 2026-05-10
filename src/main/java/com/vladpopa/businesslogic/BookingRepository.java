package com.vladpopa.businesslogic;

import com.vladpopa.data.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    // Requirement (c): Find bookings for a specific train, sorted by ID
    List<Booking> findByTrainTrainIdOrderByIdAsc(String trainId);
}