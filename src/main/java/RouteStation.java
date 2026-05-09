import jakarta.persistence.*;

@Entity
@Table(name = "route_stations")
public class RouteStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @Column(name = "stop_order")
    private int stopOrder;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }
}
