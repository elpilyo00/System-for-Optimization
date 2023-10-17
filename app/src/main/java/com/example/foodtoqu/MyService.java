package com.example.foodtoqu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyService extends Service {
    private static final String CHANNEL_ID = "MyServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final int DIARY_NOTIFICATION_ID = 2; // Unique notification ID for diary-related notifications

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
        scheduleTask(intent);
        return START_STICKY;  // Make the service sticky
    }


    private void scheduleTask(Intent intent) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("MyService", "Executing task to remove old diary entries.");
                removeOldDiaryEntriesForCurrentUser();
                // Schedule the task to run again after 15 minutes
                handler.postDelayed(this, 7 * 24 * 60 * 60 * 1000); // 7 days
            }
        }, 7 * 24 * 60 * 60 * 1000); // Initial delay of 7 days
    }
    private void removeOldDiaryEntriesForCurrentUser() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Get the current user
                Log.d("MyService", "Removing old diary entries for the current user.");
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();

                if (currentUser != null) {
                    String userUid = currentUser.getUid();
                    DatabaseReference diaryRef = FirebaseDatabase.getInstance().getReference("diary").child(userUid);

                    // Get the current time in milliseconds
                    long currentTimeMillis = System.currentTimeMillis();

                    // Calculate one minute ago
                    long oneMinuteAgoMillis = currentTimeMillis - (60 * 1000);

                    // Construct a date string for one minute ago
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                    String oneMinuteAgoDateString = dateFormat.format(new Date(oneMinuteAgoMillis));

                    // Query and remove entries older than one minute
                    diaryRef.orderByChild("timestamp").endAt(oneMinuteAgoDateString)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        childSnapshot.getRef().removeValue();
                                        // Show the diary removed notification
                                        showDiaryRemovedNotification();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("MyService", "Error: " + databaseError.getMessage());
                                }
                            });
                }
            }
        }).start();
    }

    private void showDiaryRemovedNotification() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent loginPendingIntent = PendingIntent.getActivity(this, 0, loginIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Diary Entry Removed")
                .setContentText("Your diary entry has been reset . Add it again to your diary.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(loginPendingIntent) // Set the PendingIntent to be triggered on notification click
                .setAutoCancel(true); // Automatically cancel the notification when clicked

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(DIARY_NOTIFICATION_ID, builder.build());
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "My Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("My Service")
                .setContentText("Running in the background")
                .setSmallIcon(R.mipmap.ic_launcher);

        // Make the notification persistent
        builder.setOngoing(true);

        return builder.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // If you want to stop the service, you should remove the notification.
        stopForeground(true);
    }
}

