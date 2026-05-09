import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteStationRepository extends JpaRepository<RouteStation, Integer> {
    // Helps the Admin modify routes as per Requirement (c)
    List<RouteStation> findByRouteId(int routeId);
}
