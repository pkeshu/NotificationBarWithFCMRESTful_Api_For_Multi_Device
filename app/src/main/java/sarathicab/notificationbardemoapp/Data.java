package sarathicab.notificationbardemoapp;

public class Data {
    private String tripId;
    private String tripFee;
    private String fromLocation;
    private String toLocation;
    private String accept;

    public Data() {
    }

    public Data(String tripId, String tripFee, String fromLocation, String toLocation, String accept) {
        this.tripId = tripId;
        this.tripFee = tripFee;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.accept = accept;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripFee() {
        return tripFee;
    }

    public void setTripFee(String tripFee) {
        this.tripFee = tripFee;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }
}
