import jakarta.persistence.*;

@Entity
@Table(name = "trains")
public class Train {
    @Id
    private String trainId;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    private int totalCapacity;
    private int currentDelayMinutes;

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getCurrentDelayMinutes() {
        return currentDelayMinutes;
    }

    public void setCurrentDelayMinutes(int currentDelayMinutes) {
        this.currentDelayMinutes = currentDelayMinutes;
    }
}