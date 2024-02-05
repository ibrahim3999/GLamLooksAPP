package com.example.glamlooksapp.utils;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.Nullable;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.glamlooksapp.R;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
////    @Override
////    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
////        super.onMessageReceived(remoteMessage);
////        getFirebaseMessage(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
////
////    }
//
//    public void getFirebaseMessage(String title, String msg) {
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myFirebaseChannel")
//                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
//                .setContentTitle(title)
//                .setContentText(msg)
//                .setAutoCancel(true);
//
//
//        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        manager.notify(101, builder.build());
//
//    }
//}
//




