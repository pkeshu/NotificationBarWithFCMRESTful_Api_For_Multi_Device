package sarathicab.notificationbardemoapp;

public class TripDto {
    private String token;
    private Data data;

    public TripDto() {
    }

    public TripDto(String token, Data data) {
        this.token = token;
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
