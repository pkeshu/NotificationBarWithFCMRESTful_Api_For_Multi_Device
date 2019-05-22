package sarathicab.notificationbardemoapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {
    private NotificationManager notificationManager;
    public static String message = "";
    private DatabaseReference dRefChange;

    @Override
    public void onReceive(Context context, Intent intent) {
        message = intent.getStringExtra("message");
        String fromLocation = intent.getStringExtra(MainActivity.FROMLOCATION);
        String toLocation = intent.getExtras().getString(MainActivity.TOLOCATION);
        String price = intent.getStringExtra(MainActivity.PRICE);
        String token = intent.getStringExtra(MainActivity.TOKEN);
        String tripId = intent.getStringExtra(MainActivity.TRIPID);
        final int notificationId = intent.getExtras().getInt("notificationId");
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (message.equals("accept")) {
            Data data = new Data(tripId, price, fromLocation, toLocation, "We will contact you later.");
            TripDto tripDto = new TripDto(token, data);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tripDetails").child(tripId);
            databaseReference.setValue(tripDto).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d(MainActivity.TAG, task.getException().getMessage());
                    }
                }
            });
            notificationManager.cancel(notificationId);
            cancelInAllDevices(tripId,notificationId,context);
        }



    }

    private void cancelInAllDevices(String tripId, final int notificationId, final Context context) {
        DatabaseReference dRefChange=FirebaseDatabase.getInstance().getReference("tripDetails").child(tripId).child("data");
        final List<TripDto> tripDtos=new ArrayList<>();
        tripDtos.clear();
        dRefChange.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(!ds.child("accept").equals("")){
                        // Clear all notification
                        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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
