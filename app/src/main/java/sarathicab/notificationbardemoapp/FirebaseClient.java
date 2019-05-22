package sarathicab.notificationbardemoapp;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirebaseClient {
//public static final String BASE_URL = "https://maps-2019-4fb8e.firebaseio.com/api/";
    private static  final String BASE_URL="http://10.10.10.14:4000/api/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
