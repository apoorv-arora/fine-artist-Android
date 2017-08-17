package in.fine.artist.home.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import in.fine.artist.home.R;
import in.fine.artist.home.ZApplication;
import in.fine.artist.home.data.Notification;
import in.fine.artist.home.services.FBNotificationService;
import in.fine.artist.home.utils.networking.UploadManager;
import in.fine.artist.home.views.activities.SplashActivity;

/**
 * Created by apoorvarora on 21/04/17.
 */
public class NotificationManager {
    private static volatile NotificationManager _instance;
    private Context context;
    private SharedPreferences prefs, oneTimePrefs;
    private android.app.NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static int notificationId;
    private ZApplication vapp;

    private NotificationManager(Context context) {
        this.context = context;
        mNotificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        prefs = context.getSharedPreferences(CommonLib.APP_SETTINGS, 0);
        oneTimePrefs = context.getSharedPreferences(CommonLib.ONE_LAUNCH_SETTINGS, 0);
        if (context instanceof FBNotificationService) {
            vapp = (ZApplication) ((FBNotificationService) context).getApplication();
        } else if (context instanceof Activity) {
            vapp = (ZApplication) ((Activity) context).getApplication();
        }
    }

    public static NotificationManager getInstance(Context context) {
        if (_instance == null) {
            synchronized (UploadManager.class) {
                if (_instance == null) {
                    _instance = new NotificationManager(context);
                }
            }
        }
        return _instance;
    }

    public void notifyRemoteMessage(RemoteMessage remoteMessage) {
        CommonLib.VLog("Notification", "Received object is : " + remoteMessage.toString());

        if (remoteMessage.getData() == null || remoteMessage.getData().size() == 0) {
            // firebase notifications from console
            if (remoteMessage.getNotification() != null) {
                mNotificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification notification = new Notification();
                notification.setTitle(remoteMessage.getNotification().getTitle());
                notification.setBody(remoteMessage.getNotification().getBody());
                int flags = PendingIntent.FLAG_CANCEL_CURRENT;
                Intent notificationIntent = new Intent(context, SplashActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, flags);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(getNotificationIcon())
                        .setLargeIcon(CommonLib.getBitmap(context, R.mipmap.ic_launcher, context.getResources().getDimensionPixelOffset(R.dimen.size40), context.getResources().getDimensionPixelOffset(R.dimen.size40)))
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getBody())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notification.getBody()))
                        .setAutoCancel(true)
                        .setSound(soundUri);
                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(notificationId++, mBuilder.build());
            }
            return;
        }
    }

    public void cancelAll() {
        if (mNotificationManager != null) {
            try {
                mNotificationManager.cancelAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= 21);
        return useWhiteIcon ? R.drawable.notification_icon : R.mipmap.ic_launcher;
    }
}
