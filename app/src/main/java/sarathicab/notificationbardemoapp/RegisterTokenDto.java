package sarathicab.notificationbardemoapp;

public class RegisterTokenDto {
    private String id;
    private String deviceToken;

    public RegisterTokenDto() {
    }

    public RegisterTokenDto(String id, String deviceToken) {
        this.id = id;
        this.deviceToken = deviceToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
