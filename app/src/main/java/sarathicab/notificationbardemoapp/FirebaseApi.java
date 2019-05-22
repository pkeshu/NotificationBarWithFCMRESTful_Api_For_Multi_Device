package sarathicab.notificationbardemoapp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FirebaseApi {
    @FormUrlEncoded
    @POST("send")
    Call<ResponseBody> sendNotification(
            @Field("token") String token,
            @Field("tripFee") String tripFee,
            @Field("fromLocation") String fromLocation,
            @Field("toLocation") String toLocation,
            @Field("tripId") String tripId
    );
}
