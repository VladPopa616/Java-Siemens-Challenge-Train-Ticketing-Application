package com.vladpopa.businesslogic;

import com.vladpopa.data.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {}