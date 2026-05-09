package com.vladpopa.businesslogic;

import com.vladpopa.data.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainRepository extends JpaRepository<Train, String> {
    List<Train> findByRouteId(int routeId);
}