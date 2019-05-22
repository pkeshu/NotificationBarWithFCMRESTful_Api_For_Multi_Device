package sarathicab.notificationbardemoapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static sarathicab.notificationbardemoapp.App.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {
    public static String TAG = MainActivity.class.getSimpleName();
    private EditText tripPrice, tripFromLocation, tripToLocation;
    private Button sendNotiBtn;
    private DatabaseReference databaseReference;
    private String token;
    private NotificationManagerCompat notificationManager;
    public static final String PRICE = "sarathicab.notificationbardemoapp.price";
    public static final String FROMLOCATION = "sarathicab.notificationbardemoapp.fromlocation";
    public static final String TOLOCATION = "sarathicab.notificationbardemoapp.tolocation";
    public static final String TRIPID = "sarathicab.notificationbardemoapp.tripId";
    public static final String TOKEN = "sarathicab.notificationbardemoapp.token";
    private DatabaseReference dRef;
    private List<RegisterTokenDto> listOfToken = new ArrayList<>();
    private String tripId;
    private DatabaseReference dRefUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference = FirebaseDatabase.getInstance().getReference("tripDetails");
        notificationManager = NotificationManagerCompat.from(this);
        bindView();
        dRef = FirebaseDatabase.getInstance().getReference("RegisterDeviceTokens");
        dRefUid = FirebaseDatabase.getInstance().getReference();
//        SharedPreferences prefs = getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
//        token = prefs.getString(getString(R.string.FCM_TOEKN), "");
        tripId = dRefUid.push().getKey();
//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
//            @Override
//            public void onSuccess(InstanceIdResult instanceIdResult) {
//                token = instanceIdResult.getToken();
//                Log.e("newToken", token);
//                SharedPreferences.Editor editor = getSharedPreferences("TOKEN_PREF", MODE_PRIVATE).edit();
//                if (token != null) {
//                    editor.putString("token", token);
//                    editor.apply();
//                }
//                RegisterTokenDto registerTokenDto = new RegisterTokenDto(tripId, token);
//
//                dRef.child(token).setValue(registerTokenDto);
//            }
//        });

//        RegisterTokenDto registerTokenDto=new RegisterTokenDto(tripId,token);
//        dRef.child(token).setValue(registerTokenDto);
        sendNotiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                token = sharedPref.getString(getString(R.string.FCM_TOEKN), "");
                String price, fromLocation, toLocation;
                price = tripPrice.getText().toString().trim();
                fromLocation = tripFromLocation.getText().toString().trim();
                toLocation = tripToLocation.getText().toString().trim();
                saveDataToDatabase(price, fromLocation,toLocation, token, tripId);
//                sendOnChannel(v, price, fromLocation,toLocation, token, id);
                sendNotification(price,fromLocation,toLocation,tripId,token);
            }
        });
    }

    private void sendNotification(String price, String fromLocation, String toLocation, String id, String token) {
        final FirebaseApi apiService =
                FirebaseClient.getClient().create(FirebaseApi.class);
        final String tripFee = price;
        final String fromLoc = fromLocation;
        final String toLoc = toLocation;
        final String tripId = id;
        String tok = token;
        listOfToken.clear();
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    RegisterTokenDto registerTokenDto = ds.getValue(RegisterTokenDto.class);
                    listOfToken.add(registerTokenDto);
                }
//                Toast.makeText(MainActivity.this, String.valueOf(listOfToken.get(0).getDeviceToken()), Toast.LENGTH_SHORT).show();
                for (int i = 0; i < listOfToken.size(); i++) {
                    Call<ResponseBody> call = apiService.sendNotification(listOfToken.get(i).getDeviceToken(), tripFee, fromLoc, toLoc, tripId);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            int code = response.code();
                            if (response.isSuccessful()) {
//                                Toast.makeText(MainActivity.this, "sent noti", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "something went wrong:" + code, Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                            Log.d("hellokeshar:", t.getMessage());
                            Toast.makeText(MainActivity.this, "Failed.", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void sendOnChannel(View v, String price, String fromLocation, String toLocation, String token, String id) {
        String access = "accept";
        String detail = "detail";
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Intent accessIntent = new Intent(this, NotificationReceiver.class);
        accessIntent.putExtra("message", access);
        accessIntent.putExtra(PRICE, price);
        accessIntent.putExtra(FROMLOCATION, fromLocation);
        accessIntent.putExtra(TOLOCATION, toLocation);
        accessIntent.putExtra(TRIPID, id);
        accessIntent.putExtra(TOKEN, token);
        accessIntent.putExtra("notificationId", 0);
        accessIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent accessPendIntent = PendingIntent.getBroadcast(this,
                0, accessIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent detailIntent = new Intent(this, MainActivity.class);
        PendingIntent detailPendIntent = PendingIntent.getActivity(this,
                0, detailIntent, 0);
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notification_layout);
        notificationLayout.setImageViewResource(R.id.circleImageView2, R.drawable.sar);
        notificationLayout.setTextViewText(R.id.trip_fee, "New Trip: Rs " + price);
        notificationLayout.setTextViewText(R.id.to_location, "To: " + fromLocation);
        notificationLayout.setTextViewText(R.id.from_location, "From: " + toLocation);
        notificationLayout.setTextViewText(R.id.accept_notification, "Accept");
        notificationLayout.setTextViewText(R.id.detail_notification, "Details");
        notificationLayout.setOnClickFillInIntent(R.id.accept_notification, accessIntent);
        notificationLayout.setOnClickPendingIntent(R.id.accept_notification, accessPendIntent);
        notificationLayout.setOnClickFillInIntent(R.id.detail_notification, detailIntent);
        notificationLayout.setOnClickPendingIntent(R.id.detail_notification, detailPendIntent);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.sarathi)
                .setContent(notificationLayout)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setCustomBigContentView(notificationLayout);
        notificationManager.notify(0, builder.build());
    }


    private void saveDataToDatabase(String price, String fromLocation, String toLocation, String token, String id) {
        Data data = new Data(id, price, fromLocation, toLocation, "");
        TripDto tripDto = new TripDto(token, data);
        databaseReference.child(id).setValue(tripDto).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, task.getException().getMessage());
                } else {
//                    Toast.makeText(MainActivity.this, "Data saved.", Toast.LENGTH_SHORT).show();
                    tripPrice.setText("");
                    tripFromLocation.setText("");
                    tripToLocation.setText("");
                }
            }
        });
    }


    private void bindView() {
        tripPrice = findViewById(R.id.trip_price);
        tripFromLocation = findViewById(R.id.trip_location);
        tripToLocation = findViewById(R.id.trip_dest_location);
        sendNotiBtn = findViewById(R.id.send_noti_btn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference dRefChange=FirebaseDatabase.getInstance().getReference("tripDetails").child(tripId).child("data");
        final List<TripDto> tripDtos=new ArrayList<>();
        tripDtos.clear();
        dRefChange.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(!ds.child("accept").equals("")){
                        // Clear all notification
                        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        nMgr.cancelAll();
                        break;
                    }
                }
//                for(TripDto tripDto:tripDtos){

//                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
