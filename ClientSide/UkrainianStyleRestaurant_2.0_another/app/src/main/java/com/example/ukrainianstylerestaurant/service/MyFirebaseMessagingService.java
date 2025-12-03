package com.example.ukrainianstylerestaurant.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.example.ukrainianstylerestaurant.LocalStorage;
import com.example.ukrainianstylerestaurant.MainActivity;
import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.data.AuthRepository;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // Цей метод викликається, коли токен оновлюється (наприклад, при першому запуску)
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Якщо користувач вже залогінений, відправляємо токен на сервер
        String username = LocalStorage.getUsername(this);
        if (LocalStorage.isLoggedIn(this) && !username.isEmpty()) {
            new AuthRepository().sendTokenToServer(username, "Client", token);
        }
    }

    // Цей метод викликається, коли приходить повідомлення (пуш)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        // Якщо є payload повідомлення
        if (message.getNotification() != null) {
            sendNotification(message.getNotification().getTitle(), message.getNotification().getBody());
        }
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Прапор mutability обов'язковий для Android 12+
        int flags = PendingIntent.FLAG_ONE_SHOT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flags);

        String channelId = "OrderUpdates";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher) // Переконайся, що іконка існує, або зміни на R.drawable.ic_...
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Для Android 8.0+ потрібен канал
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Order Updates",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}